package io.microsphere.sentinel.util;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.ResourceTypeConstants;
import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import io.microsphere.logging.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import static io.microsphere.collection.MapUtils.newFixedHashMap;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.reflect.FieldUtils.getFieldValue;
import static io.microsphere.reflect.FieldUtils.getStaticFieldValue;
import static io.microsphere.text.FormatUtils.format;
import static io.microsphere.util.ClassUtils.getSimpleName;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/**
 * Alibaba Sentinel Utilities Class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class SentinelUtils {

    private static final Logger logger = getLogger(SentinelUtils.class);

    public static final String FLOW_DATA_ID_PATTERN = "{}-flow-rules";

    private static final ThreadLocal<Entry> entryThreadLocal = new ThreadLocal<>();

    private static final Map<Integer, String> resourceTypeToLabelMapping = initResourceTypeToLabelMapping();

    private static volatile ScheduledExecutorService sentinelMetricsTaskExecutor;

    private static Map<Integer, String> initResourceTypeToLabelMapping() {
        Field[] fields = ResourceTypeConstants.class.getFields();
        Map<Integer, String> resourceTypeToLabelMapping = newFixedHashMap(fields.length);
        for (Field field : fields) {
            if (isStatic(field.getModifiers())) {
                Integer value = getFieldValue(null, field);
                resourceTypeToLabelMapping.put(value, field.getName());
            }
        }
        return resourceTypeToLabelMapping;
    }

    /**
     * The Resource name of the build execution method
     *
     * @param method {@link Method}
     * @return The method signature (simple class) serves as the Resource name
     */
    public static String buildResourceName(Method method) {
        String prefix = getSimpleName(method.getDeclaringClass()) + "." + method.getName();
        StringJoiner resourceNameBuilder = new StringJoiner(",", prefix + "(", ")");
        for (Class<?> parameterType : method.getParameterTypes()) {
            resourceNameBuilder.add(getSimpleName(parameterType));
        }
        return resourceNameBuilder.toString();
    }

    /**
     * Get the Flow Data ID
     *
     * @param appName the name of application
     * @return non-null
     */
    public static String getFlowDataId(String appName) {
        return format(FLOW_DATA_ID_PATTERN, appName);
    }

    /**
     * Get the label string for resource type
     *
     * @param resourceType resource type
     * @return non-null
     */
    public static String getResourceTypeAsString(int resourceType) {
        return resourceTypeToLabelMapping.getOrDefault(resourceType, "UNKNOWN");
    }

    /**
     * Find the Sentinel Metrics Task {@link ScheduledExecutorService Executor} from {@link FlowRuleManager}
     *
     * @return <code>null</code> if can't be found
     * @see FlowRuleManager#SCHEDULER
     */
    public static ScheduledExecutorService findSentinelMetricsTaskExecutor() {
        return getStaticFieldValue(FlowRuleManager.class, "SCHEDULER");
    }

    /**
     * Get the Sentinel Metrics Task {@link ScheduledExecutorService Executor}
     *
     * @return {@link Executors#newSingleThreadScheduledExecutor(ThreadFactory) new a single ScheduledExecutorService}
     * if can't be {@link #findSentinelMetricsTaskExecutor() found}
     * @see #findSentinelMetricsTaskExecutor()
     */
    public static ScheduledExecutorService getSentinelMetricsTaskExecutor() {
        ScheduledExecutorService scheduledExecutorService = sentinelMetricsTaskExecutor;

        if (scheduledExecutorService == null) {
            scheduledExecutorService = getSentinelMetricsTaskExecutor(findSentinelMetricsTaskExecutor());
            sentinelMetricsTaskExecutor = scheduledExecutorService;
        }

        return scheduledExecutorService;
    }

    static ScheduledExecutorService getSentinelMetricsTaskExecutor(ScheduledExecutorService defaultScheduledExecutorService) {
        return defaultScheduledExecutorService == null ?
                newSingleThreadScheduledExecutor(new NamedThreadFactory("sentinel-metrics-task", true)) :
                defaultScheduledExecutorService;
    }

    private SentinelUtils() {
    }
}