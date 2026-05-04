/*
 * TEST CASES FOR SentinelContext CLASS
 *
 * 1. Constructor validation tests:
 *    - Test constructor with null resourceName throws IllegalArgumentException
 *    - Test constructor with empty resourceName throws IllegalArgumentException
 *    - Test constructor with null contextName throws IllegalArgumentException
 *    - Test constructor with empty contextName throws IllegalArgumentException
 *    - Test constructor with null origin throws IllegalArgumentException
 *    - Test constructor with null entry throws IllegalArgumentException
 *    - Test constructor with valid parameters creates instance successfully
 *
 * 2. Getter method tests:
 *    - Test getResourceName() returns expected resource name
 *    - Test getContextName() returns expected context name
 *    - Test getOrigin() returns expected origin
 *    - Test getEntry() returns expected entry
 *
 * 3. Result handling tests:
 *    - Test setResult() sets result correctly and returns this instance
 *    - Test getResult() returns null when result not set
 *    - Test getResult() returns set value when result is set
 *
 * 4. Failure handling tests:
 *    - Test setFailure() sets failure correctly and returns this instance
 *    - Test getFailure() returns null when failure not set
 *    - Test getFailure() returns set exception when failure is set
 *
 * 5. Attribute management tests:
 *    - Test setAttribute() adds attribute to map and returns this instance
 *    - Test hasAttribute() returns false for non-existent attribute
 *    - Test hasAttribute() returns true for existing attribute
 *    - Test getAttribute(String) returns null for non-existent attribute
 *    - Test getAttribute(String) returns correct value for existing attribute
 *    - Test getAttribute(String, defaultValue) returns default for non-existent attribute
 *    - Test getAttribute(String, defaultValue) returns correct value for existing attribute
 *    - Test removeAttribute() returns null for non-existent attribute
 *    - Test removeAttribute() returns correct value for existing attribute
 *    - Test removeAttributes() clears all attributes
 *    - Test getAttributes() returns unmodifiable map
 *
 * 6. Edge case tests:
 *    - Test toString() method returns expected string representation
 *    - Test behavior when multiple attributes are set
 *    - Test behavior when attributes are set, retrieved, and removed in sequence
 *
 * 7. Integration tests:
 *    - Test complete flow: create context, set result, set failure, set attributes, verify all values
 *    - Test context lifecycle: create, modify, verify, clean up
 */

package io.microsphere.sentinel.common;

import com.alibaba.csp.sentinel.Entry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static io.microsphere.sentinel.common.SentinelContext.clearContext;
import static io.microsphere.sentinel.common.SentinelContext.getContext;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test cases for SentinelContext class
 */
@ExtendWith(MockitoExtension.class)
public class SentinelContextTest {

    @Mock
    private Entry mockEntry;

    private String testResourceName = "test-resource";
    private String testContextName = "test-context";
    private String testOrigin = "test-origin";

    private SentinelContext context;

    @BeforeEach
    void setUp() {
        // Initialize test context with mock entry
        context = new SentinelContext(testResourceName, testContextName, testOrigin, mockEntry);
    }

    /*
     * CONSTRUCTOR VALIDATION TESTS
     */
    @Test
    @DisplayName("Constructor should throw IllegalArgumentException when resourceName is null")
    void testConstructorNullResourceName() {
        // Given null resource name
        // When creating context with null resource name
        // Then IllegalArgumentException should be thrown
        assertThrows(IllegalArgumentException.class, () -> {
            new SentinelContext(null, testContextName, testOrigin, mockEntry);
        });
    }

    @Test
    @DisplayName("Constructor should throw IllegalArgumentException when resourceName is empty")
    void testConstructorEmptyResourceName() {
        // Given empty resource name
        // When creating context with empty resource name
        // Then IllegalArgumentException should be thrown
        assertThrows(IllegalArgumentException.class, () -> {
            new SentinelContext("", testContextName, testOrigin, mockEntry);
        });
    }

    @Test
    @DisplayName("Constructor should throw IllegalArgumentException when contextName is null")
    void testConstructorNullContextName() {
        // Given null context name
        // When creating context with null context name
        // Then IllegalArgumentException should be thrown
        assertThrows(IllegalArgumentException.class, () -> {
            new SentinelContext(testResourceName, null, testOrigin, mockEntry);
        });
    }

    @Test
    @DisplayName("Constructor should throw IllegalArgumentException when contextName is empty")
    void testConstructorEmptyContextName() {
        // Given empty context name
        // When creating context with empty context name
        // Then IllegalArgumentException should be thrown
        assertThrows(IllegalArgumentException.class, () -> {
            new SentinelContext(testResourceName, "", testOrigin, mockEntry);
        });
    }

    @Test
    @DisplayName("Constructor should throw IllegalArgumentException when origin is null")
    void testConstructorNullOrigin() {
        // Given null origin
        // When creating context with null origin
        // Then IllegalArgumentException should be thrown
        assertThrows(IllegalArgumentException.class, () -> {
            new SentinelContext(testResourceName, testContextName, null, mockEntry);
        });
    }

    @Test
    @DisplayName("Constructor should throw IllegalArgumentException when entry is null")
    void testConstructorNullEntry() {
        // Given null entry
        // When creating context with null entry
        // Then IllegalArgumentException should be thrown
        assertThrows(IllegalArgumentException.class, () -> {
            new SentinelContext(testResourceName, testContextName, testOrigin, null);
        });
    }

    @Test
    @DisplayName("Constructor should create instance successfully with valid parameters")
    void testConstructorValidParameters() {
        // Given valid parameters
        // When creating context with valid parameters
        // Then instance should be created successfully
        SentinelContext validContext = new SentinelContext(testResourceName, testContextName, testOrigin, mockEntry);
        assertNotNull(validContext);
        assertEquals(testResourceName, validContext.getResourceName());
        assertEquals(testContextName, validContext.getContextName());
        assertEquals(testOrigin, validContext.getOrigin());
        assertEquals(mockEntry, validContext.getEntry());
    }

    /*
     * GETTER METHOD TESTS
     */
    @Test
    @DisplayName("getResourceName() should return the configured resource name")
    void testGetResourceName() {
        // Given context with resource name
        // When getResourceName() is called
        // Then it should return the configured resource name
        assertEquals(testResourceName, context.getResourceName());
    }

    @Test
    @DisplayName("getContextName() should return the configured context name")
    void testGetContextName() {
        // Given context with context name
        // When getContextName() is called
        // Then it should return the configured context name
        assertEquals(testContextName, context.getContextName());
    }

    @Test
    @DisplayName("getOrigin() should return the configured origin")
    void testGetOrigin() {
        // Given context with origin
        // When getOrigin() is called
        // Then it should return the configured origin
        assertEquals(testOrigin, context.getOrigin());
    }

    @Test
    @DisplayName("getEntry() should return the configured entry")
    void testGetEntry() {
        // Given context with entry
        // When getEntry() is called
        // Then it should return the configured entry
        assertEquals(mockEntry, context.getEntry());
    }

    /*
     * RESULT HANDLING TESTS
     */
    @Test
    @DisplayName("setResult() should set result and return this instance")
    void testSetResult() {
        // Given context
        // When setResult() is called with a value
        // Then it should set the result and return this instance
        Object testResult = "test-result";
        SentinelContext result = context.setResult(testResult);
        assertSame(context, result);
        assertEquals(testResult, context.getResult());
    }

    @Test
    @DisplayName("getResult() should return null when result is not set")
    void testGetResultNull() {
        // Given context without result set
        // When getResult() is called
        // Then it should return null
        assertNull(context.getResult());
    }

    @Test
    @DisplayName("getResult() should return set value when result is set")
    void testGetResultValue() {
        // Given context with result set
        context.setResult("test-result");
        // When getResult() is called
        // Then it should return the set value
        assertEquals("test-result", context.getResult());
    }

    /*
     * FAILURE HANDLING TESTS
     */
    @Test
    @DisplayName("setFailure() should set failure and return this instance")
    void testSetFailure() {
        // Given context
        // When setFailure() is called with an exception
        // Then it should set the failure and return this instance
        Throwable testException = new RuntimeException("test-exception");
        SentinelContext result = context.setFailure(testException);
        assertSame(context, result);
        assertEquals(testException, context.getFailure());
    }

    @Test
    @DisplayName("getFailure() should return null when failure is not set")
    void testGetFailureNull() {
        // Given context without failure set
        // When getFailure() is called
        // Then it should return null
        assertNull(context.getFailure());
    }

    @Test
    @DisplayName("getFailure() should return set exception when failure is set")
    void testGetFailureException() {
        // Given context with failure set
        Throwable testException = new RuntimeException("test-exception");
        context.setFailure(testException);
        // When getFailure() is called
        // Then it should return the set exception
        assertEquals(testException, context.getFailure());
    }

    /*
     * ATTRIBUTE MANAGEMENT TESTS
     */
    @Test
    @DisplayName("setAttribute() should add attribute to map and return this instance")
    void testSetAttribute() {
        // Given context
        // When setAttribute() is called with name and value
        // Then it should add attribute and return this instance
        String attrName = "test-attr";
        String attrValue = "test-value";
        SentinelContext result = context.setAttribute(attrName, attrValue);
        assertSame(context, result);
        assertTrue(context.hasAttribute(attrName));
        assertEquals(attrValue, context.getAttribute(attrName));
    }

    @Test
    @DisplayName("hasAttribute() should return false for non-existent attribute")
    void testHasAttributeFalse() {
        // Given context without attribute
        // When hasAttribute() is called for non-existent attribute
        // Then it should return false
        assertFalse(context.hasAttribute("non-existent-attr"));
    }

    @Test
    @DisplayName("hasAttribute() should return true for existing attribute")
    void testHasAttributeTrue() {
        // Given context with attribute set
        context.setAttribute("test-attr", "test-value");
        // When hasAttribute() is called for existing attribute
        // Then it should return true
        assertTrue(context.hasAttribute("test-attr"));
    }

    @Test
    @DisplayName("getAttribute(String) should return null for non-existent attribute")
    void testGetAttributeNull() {
        // Given context without attribute
        // When getAttribute() is called for non-existent attribute
        // Then it should return null
        assertNull(context.getAttribute("non-existent-attr"));
    }

    @Test
    @DisplayName("getAttribute(String) should return correct value for existing attribute")
    void testGetAttributeValue() {
        // Given context with attribute set
        String attrName = "test-attr";
        String attrValue = "test-value";
        context.setAttribute(attrName, attrValue);
        // When getAttribute() is called for existing attribute
        // Then it should return the correct value
        assertEquals(attrValue, context.getAttribute(attrName));
    }

    @Test
    @DisplayName("getAttribute(String, defaultValue) should return default for non-existent attribute")
    void testGetAttributeDefault() {
        // Given context without attribute
        // When getAttribute() is called with default value for non-existent attribute
        // Then it should return the default value
        String defaultValue = "default-value";
        assertEquals(defaultValue, context.getAttribute("non-existent-attr", defaultValue));
    }

    @Test
    @DisplayName("getAttribute(String, defaultValue) should return correct value for existing attribute")
    void testGetAttributeDefaultValue() {
        // Given context with attribute set
        String attrName = "test-attr";
        String attrValue = "test-value";
        context.setAttribute(attrName, attrValue);
        // When getAttribute() is called with default value for existing attribute
        // Then it should return the correct value
        String defaultValue = "default-value";
        assertEquals(attrValue, context.getAttribute(attrName, defaultValue));
    }

    @Test
    @DisplayName("removeAttribute() should return null for non-existent attribute")
    void testRemoveAttributeNull() {
        // Given context without attribute
        // When removeAttribute() is called for non-existent attribute
        // Then it should return null
        assertNull(context.removeAttribute("non-existent-attr"));
    }

    @Test
    @DisplayName("removeAttribute() should return correct value for existing attribute")
    void testRemoveAttributeValue() {
        // Given context with attribute set
        String attrName = "test-attr";
        String attrValue = "test-value";
        context.setAttribute(attrName, attrValue);
        // When removeAttribute() is called for existing attribute
        // Then it should return the correct value
        assertEquals(attrValue, context.removeAttribute(attrName));
        assertFalse(context.hasAttribute(attrName));
    }

    @Test
    @DisplayName("removeAttributes() should clear all attributes")
    void testRemoveAttributes() {
        // Given context with multiple attributes
        context.setAttribute("attr1", "value1");
        context.setAttribute("attr2", "value2");
        context.setAttribute("attr3", "value3");
        // When removeAttributes() is called
        // Then all attributes should be cleared
        context.removeAttributes();
        assertFalse(context.hasAttribute("attr1"));
        assertFalse(context.hasAttribute("attr2"));
        assertFalse(context.hasAttribute("attr3"));
    }

    @Test
    @DisplayName("getAttributes() should return unmodifiable map")
    void testGetAttributesUnmodifiable() {
        Map<String, Object> attributes = context.getAttributes();
        assertSame(emptyMap(), attributes);

        // Given context with attributes
        context.setAttribute("test-attr", "test-value");
        // When trying to modify the returned map
        // Then UnsupportedOperationException should be thrown
        assertThrows(UnsupportedOperationException.class, () -> {
            context.getAttributes().put("new-attr", "new-value");
        });
    }

    /*
     * EDGE CASE TESTS
     */
    @Test
    @DisplayName("toString() should return expected string representation")
    void testToString() {
        // Given context with basic values
        // When toString() is called
        // Then it should return expected string format
        String toString = context.toString();
        assertTrue(toString.contains("SentinelContext{"));
        assertTrue(toString.contains("resourceName='test-resource'"));
        assertTrue(toString.contains("contextName='test-context'"));
        assertTrue(toString.contains("origin='test-origin'"));
        assertTrue(toString.contains("entry="));
        assertTrue(toString.contains("result=null"));
        assertTrue(toString.contains("failure=null"));
        assertTrue(toString.contains("attributes=null"));
    }

    @Test
    @DisplayName("Multiple attributes should be handled correctly")
    void testMultipleAttributes() {
        // Given context
        // When setting multiple attributes
        // Then all should be retrievable independently
        context.setAttribute("attr1", "value1");
        context.setAttribute("attr2", "value2");
        context.setAttribute("attr3", "value3");

        assertEquals("value1", context.getAttribute("attr1"));
        assertEquals("value2", context.getAttribute("attr2"));
        assertEquals("value3", context.getAttribute("attr3"));

        assertTrue(context.hasAttribute("attr1"));
        assertTrue(context.hasAttribute("attr2"));
        assertTrue(context.hasAttribute("attr3"));
    }

    @Test
    @DisplayName("Attribute lifecycle: set, get, remove should work correctly")
    void testAttributeLifecycle() {
        // Given context
        String attrName = "lifecycle-attr";
        String attrValue = "lifecycle-value";

        // When setting attribute
        context.setAttribute(attrName, attrValue);
        assertTrue(context.hasAttribute(attrName));
        assertEquals(attrValue, context.getAttribute(attrName));

        // When removing attribute
        Object removedValue = context.removeAttribute(attrName);
        assertEquals(attrValue, removedValue);
        assertFalse(context.hasAttribute(attrName));

        // When getting removed attribute
        assertNull(context.getAttribute(attrName));
    }

    @Test
    @DisplayName("removeAttributes() on initialized status")
    void testRemoveAttributesOnInitializedStatus() {
        assertSame(context, context.removeAttributes());
    }

    /*
     * INTEGRATION TESTS
     */
    @Test
    @DisplayName("Complete flow: create, modify, verify, clean up")
    void testCompleteFlow() {
        // Given new context
        SentinelContext flowContext = new SentinelContext("flow-resource", "flow-context", "flow-origin", mockEntry);

        // When modifying context
        flowContext.setResult("flow-result");
        flowContext.setFailure(new RuntimeException("flow-failure"));
        flowContext.setAttribute("flow-attr", "flow-value");

        // Then verify all values
        assertEquals("flow-resource", flowContext.getResourceName());
        assertEquals("flow-context", flowContext.getContextName());
        assertEquals("flow-origin", flowContext.getOrigin());
        assertEquals("flow-result", flowContext.getResult());
        assertNotNull(flowContext.getFailure());
        assertTrue(flowContext.hasAttribute("flow-attr"));
        assertEquals("flow-value", flowContext.getAttribute("flow-attr"));

        // When cleaning up
        flowContext.removeAttributes();

        // Then verify cleanup
        assertFalse(flowContext.hasAttribute("flow-attr"));
    }

    @Test
    @DisplayName("Context Holder: set, get and remove")
    public void testContextHolder() {
        assertNull(getContext());
        this.context.setContext();
        assertSame(this.context, getContext());
        clearContext();
        assertNull(getContext());
    }
}
