package io.microsphere.sentinel.util;


import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_API_GATEWAY;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_DB_SQL;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_RPC;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_WEB;
import static io.microsphere.reflect.MethodUtils.findMethod;
import static io.microsphere.sentinel.util.SentinelUtils.buildResourceName;
import static io.microsphere.sentinel.util.SentinelUtils.findSentinelMetricsTaskExecutor;
import static io.microsphere.sentinel.util.SentinelUtils.getFlowDataId;
import static io.microsphere.sentinel.util.SentinelUtils.getResourceTypeAsString;
import static io.microsphere.sentinel.util.SentinelUtils.getSentinelMetricsTaskExecutor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * {@link SentinelUtils} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
class SentinelUtilsTest {

    @Test
    void testBuildResourceName() {
        Method method = findMethod(SentinelUtils.class, "buildResourceName", Method.class);
        String resourceName = buildResourceName(method);
        assertEquals("SentinelUtils.buildResourceName(Method)", resourceName);
    }

    @Test
    void testGetFlowDataId() {
        String flowDataId = getFlowDataId("test");
        assertEquals("test-flow-rules", flowDataId);
    }

    @Test
    void testGetResourceTypeAsString() {
        assertEquals("COMMON", getResourceTypeAsString(COMMON));
        assertEquals("COMMON_WEB", getResourceTypeAsString(COMMON_WEB));
        assertEquals("COMMON_RPC", getResourceTypeAsString(COMMON_RPC));
        assertEquals("COMMON_API_GATEWAY", getResourceTypeAsString(COMMON_API_GATEWAY));
        assertEquals("COMMON_DB_SQL", getResourceTypeAsString(COMMON_DB_SQL));
        assertEquals("UNKNOWN", getResourceTypeAsString(-1));
    }

    @Test
    void testFindSentinelMetricsTaskExecutor() {
        assertNotNull(findSentinelMetricsTaskExecutor());
    }

    @Test
    void testGetSentinelMetricsTaskExecutor() {
        assertSame(findSentinelMetricsTaskExecutor(), getSentinelMetricsTaskExecutor());
        assertSame(findSentinelMetricsTaskExecutor(), getSentinelMetricsTaskExecutor());
        assertNotEquals(findSentinelMetricsTaskExecutor(), getSentinelMetricsTaskExecutor(null));
    }
}