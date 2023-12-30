package io.microsphere.sentinel.util;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.ResourceTypeConstants;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

import static com.alibaba.csp.sentinel.Constants.CONTEXT_DEFAULT_NAME;
import static io.microsphere.reflect.FieldUtils.getFieldValue;
import static io.microsphere.reflect.FieldUtils.getStaticFieldValue;
import static io.microsphere.text.FormatUtils.format;
import static io.microsphere.util.ClassUtils.getSimpleName;
import static io.microsphere.util.ExceptionUtils.throwTarget;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/**
 * Alibaba Sentinel Utilities Class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class SentinelUtils {

    private static final Logger logger = LoggerFactory.getLogger(SentinelUtils.class);

    public static final String DEFAULT_ORIGIN = "";

    public static final String FLOW_DATA_ID_PATTERN = "{}-flow-rules";

    private static final Map<Integer, String> resourceTypeToLabelMapping = initResourceTypeToLabelMapping();

    private static volatile ScheduledExecutorService sentinelMetricsTaskExecutor;

    private static Map<Integer, String> initResourceTypeToLabelMapping() {
        Field[] fields = ResourceTypeConstants.class.getFields();
        Map<Integer, String> resourceTypeToLabelMapping = new HashMap<>(fields.length);
        for (Field field : fields) {
            if (isStatic(field.getModifiers())) {
                Integer value = getFieldValue(null, field);
                resourceTypeToLabelMapping.put(value, field.getName());
            }
        }
        return resourceTypeToLabelMapping;
    }

    private SentinelUtils() {
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
     * Executes a callback in sentinel
     *
     * @param resourceName the resource of Sentinel's resource
     * @param callback     callback
     * @param <T>          the return type
     * @return nullable
     */
    public static <T> T doInSentinel(String resourceName, Callable<T> callback) throws Throwable {
        return doInSentinel(resourceName, CONTEXT_DEFAULT_NAME, DEFAULT_ORIGIN, callback);
    }

    /**
     * Executes a callback in sentinel
     *
     * @param <T>          the return type
     * @param resourceName the resource of Sentinel's resource
     * @param contextName  the name of {@link Context}
     * @param origin       the origin of {@link Context}
     * @param callback     callback
     * @return nullable
     */
    public static <T> T doInSentinel(String resourceName, String contextName, String origin, Callable<T> callback) throws Throwable {
        T result = null;
        String actualContextName = contextName == null ? CONTEXT_DEFAULT_NAME : contextName;
        String actualOrigin = origin == null ? DEFAULT_ORIGIN : origin;
        ContextUtil.enter(actualContextName, actualOrigin);
        Entry entry = null;
        try {
            entry = SphU.entry(resourceName);
            result = callback.call();
        } catch (Throwable e) {
            if (!BlockException.isBlockException(e)) {
                Tracer.trace(e);
            }
            if (logger.isErrorEnabled()) {
                logger.error("A callback '{}' of Sentinel context[name :'{}' , origin : '{}'] resource[name :'{}'] execution is failed", callback, contextName, origin, resourceName, e);
            }
            throw e;
        } finally {
            if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
            if (logger.isDebugEnabled()) {
                logger.debug("A callback '{}' of Sentinel context[name :'{}' , origin : '{}'] resource[name :'{}'] was executed", callback, contextName, origin, resourceName);
            }
        }
        return result;
    }

    /**
     * Executes a callback in sentinel
     *
     * @param <T>              the return type
     * @param resourceName     the resource of Sentinel's resource
     * @param contextName      the name of {@link Context}
     * @param origin           the origin of {@link Context}
     * @param callback         callback
     * @param exceptionHandler the handler for {@link Throwable}
     * @return nullable
     */
    public static <T> T doInSentinel(String resourceName, @Nullable String contextName, @Nullable String origin, Callable<T> callback, Consumer<Throwable> exceptionHandler) {
        T result = null;
        try {
            result = doInSentinel(resourceName, contextName, origin, callback);
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return result;
    }

    /**
     * Executes a callback in sentinel
     *
     * @param <T>           the return type
     * @param <TT>          the throwable type
     * @param resourceName  the resource of Sentinel's resource
     * @param contextName   the name of {@link Context}
     * @param origin        the origin of {@link Context}
     * @param callback      callback
     * @param throwableType the handler for {@link Throwable}
     * @return nullable
     */
    public static <T, TT extends Throwable> T doInSentinel(String resourceName, String contextName, String origin, Callable<T> callback, Class<TT> throwableType) throws TT {
        T result = null;
        try {
            result = doInSentinel(resourceName, contextName, origin, callback);
        } catch (Throwable e) {
            throwTarget(e, throwableType);
        }
        return result;
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
        String fieldName = "SCHEDULER";
        ScheduledExecutorService scheduledExecutorService = null;
        try {
            scheduledExecutorService = getStaticFieldValue(FlowRuleManager.class, "SCHEDULER");
        } catch (Throwable e) {
            logger.warn("The static field[name : '{}'] can't be found in the {}", fieldName, FlowRuleManager.class, e);
        }
        return scheduledExecutorService;
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
            scheduledExecutorService = findSentinelMetricsTaskExecutor();
            if (scheduledExecutorService == null) {
                scheduledExecutorService = newSingleThreadScheduledExecutor(new NamedThreadFactory("sentinel-metrics-task", true));
            }
            sentinelMetricsTaskExecutor = scheduledExecutorService;
        }

        return scheduledExecutorService;
    }

}