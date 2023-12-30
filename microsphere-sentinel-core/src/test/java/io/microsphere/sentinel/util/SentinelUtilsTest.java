package io.microsphere.sentinel.util;

import org.junit.Test;

import java.lang.reflect.Method;

import static io.microsphere.reflect.MethodUtils.findMethod;
import static io.microsphere.sentinel.util.SentinelUtils.buildResourceName;
import static org.junit.Assert.assertEquals;

/**
 * {@link SentinelUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
public class SentinelUtilsTest {

    @Test
    public void testBuildResourceName() {
        Method method = findMethod(SentinelUtils.class, "buildResourceName", Method.class);
        String resourceName = buildResourceName(method);
        assertEquals("SentinelUtils.buildResourceName(Method)", resourceName);
    }

}
