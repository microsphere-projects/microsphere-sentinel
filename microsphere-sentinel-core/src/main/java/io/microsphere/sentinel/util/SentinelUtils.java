package io.microsphere.sentinel.util;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static com.alibaba.csp.sentinel.Constants.CONTEXT_DEFAULT_NAME;
import static io.microsphere.util.ClassUtils.getSimpleName;
import static io.microsphere.util.ExceptionUtils.throwTarget;

/**
 * Alibaba Sentinel Utilities Class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class SentinelUtils {

    private static final Logger logger = LoggerFactory.getLogger(SentinelUtils.class);

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
        return doInSentinel(CONTEXT_DEFAULT_NAME, "", resourceName, callback);
    }

    /**
     * Executes a callback in sentinel
     *
     * @param contextName  the name of {@link Context}
     * @param origin       the origin of {@link Context}
     * @param resourceName the resource of Sentinel's resource
     * @param callback     callback
     * @param <T>          the return type
     * @return nullable
     */
    public static <T> T doInSentinel(String contextName, String origin, String resourceName, Callable<T> callback) throws Throwable {
        T result = null;
        ContextUtil.enter(contextName, origin);
        Entry entry = null;
        try {
            entry = SphU.entry(resourceName);
            result = callback.call();
        } catch (Throwable e) {
            if (!BlockException.isBlockException(e)) {
                Tracer.trace(e);
            }
            if (logger.isErrorEnabled()) {
                logger.error("A callback '{}' of Sentinel context[name :'{}' , origin : '{}'] resource[name :'{}'] execution is failed",
                        callback, contextName, origin, resourceName, e);
            }
            throw e;
        } finally {
            if (entry != null) {
                entry.exit();
            }
            ContextUtil.exit();
            if (logger.isDebugEnabled()) {
                logger.debug("A callback '{}' of Sentinel context[name :'{}' , origin : '{}'] resource[name :'{}'] was executed",
                        callback, contextName, origin, resourceName);
            }
        }
        return result;
    }

    /**
     * Executes a callback in sentinel
     *
     * @param contextName      the name of {@link Context}
     * @param origin           the origin of {@link Context}
     * @param resourceName     the resource of Sentinel's resource
     * @param callback         callback
     * @param <T>              the return type
     * @param exceptionHandler the handler for {@link Throwable}
     * @return nullable
     */
    public static <T> T doInSentinel(String contextName, String origin, String resourceName, Callable<T> callback, Consumer<Throwable> exceptionHandler) {
        T result = null;
        try {
            result = doInSentinel(contextName, origin, resourceName, callback);
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return result;
    }

    /**
     * Executes a callback in sentinel
     *
     * @param contextName   the name of {@link Context}
     * @param origin        the origin of {@link Context}
     * @param resourceName  the resource of Sentinel's resource
     * @param callback      callback
     * @param throwableType the handler for {@link Throwable}
     * @param <T>           the return type
     * @param <TT>          the throwable type
     * @return nullable
     */
    public static <T, TT extends Throwable> T doInSentinel(String contextName, String origin, String resourceName,
                                                           Callable<T> callback, Class<TT> throwableType) throws TT {
        T result = null;
        try {
            result = doInSentinel(contextName, origin, resourceName, callback);
        } catch (Throwable e) {
            throwTarget(e, throwableType);
        }
        return result;
    }
}
