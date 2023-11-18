package io.microsphere.sentinel.util;

import java.lang.reflect.Method;
import java.util.StringJoiner;

import static io.microsphere.util.ClassUtils.getSimpleName;

/**
 * Alibaba Sentinel Utilities Class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public abstract class SentinelUtils {

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
}
