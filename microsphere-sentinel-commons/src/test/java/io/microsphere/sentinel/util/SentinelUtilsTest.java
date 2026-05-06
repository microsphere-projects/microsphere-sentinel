package io.microsphere.sentinel.util;


import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_API_GATEWAY;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_DB_SQL;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_RPC;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_WEB;
import static io.microsphere.reflect.MethodUtils.findMethod;
import static io.microsphere.sentinel.util.SentinelUtils.DEFAULT_CONTEXT_NAME;
import static io.microsphere.sentinel.util.SentinelUtils.DEFAULT_CONTEXT_NAME_PATTERN;
import static io.microsphere.sentinel.util.SentinelUtils.DEFAULT_ORIGIN;
import static io.microsphere.sentinel.util.SentinelUtils.FLOW_DATA_ID_PATTERN;
import static io.microsphere.sentinel.util.SentinelUtils.PROPERTY_NAME_PREFIX;
import static io.microsphere.sentinel.util.SentinelUtils.buildResourceName;
import static io.microsphere.sentinel.util.SentinelUtils.findSentinelMetricsTaskExecutor;
import static io.microsphere.sentinel.util.SentinelUtils.getDefaultContextName;
import static io.microsphere.sentinel.util.SentinelUtils.getFlowDataId;
import static io.microsphere.sentinel.util.SentinelUtils.getPluginEnabledPropertyName;
import static io.microsphere.sentinel.util.SentinelUtils.getResourceTypeAsString;
import static io.microsphere.sentinel.util.SentinelUtils.getSentinelMetricsTaskExecutor;
import static io.microsphere.sentinel.util.SentinelUtils.isPluginEnabled;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void testConstants() {
        assertEquals("{}-flow-rules", FLOW_DATA_ID_PATTERN);
        assertEquals("microsphere_sentinel_{}_context", DEFAULT_CONTEXT_NAME_PATTERN);
        assertEquals("microsphere_sentinel_default_context", DEFAULT_CONTEXT_NAME);
        assertEquals("", DEFAULT_ORIGIN);
        assertEquals("microsphere.sentinel.", PROPERTY_NAME_PREFIX);
    }

    @Test
    void testBuildResourceName() {
        Method method = findMethod(SentinelUtils.class, "buildResourceName", Method.class);
        String resourceName = buildResourceName(method);
        assertEquals("SentinelUtils.buildResourceName(Method)", resourceName);
    }

    @Test
    void testGetPluginEnabledPropertyName() {
        assertEquals("microsphere.sentinel.default.enabled", getPluginEnabledPropertyName("default"));
    }

    @Test
    void testIsPluginEnabled() {
        assertFalse(isPluginEnabled("default"));
    }

    @Test
    void testGetDefaultContextName() {
        assertEquals(DEFAULT_CONTEXT_NAME, getDefaultContextName("default"));
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