package io.microsphere.sentinel.hibernate;

import org.hibernate.Interceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit 6 test cases for {@link DelegatingInterceptor}.
 */
@ExtendWith(MockitoExtension.class)
class DelegatingInterceptorTest {

    @Mock
    private Interceptor delegate;

    private DelegatingInterceptor delegatingInterceptor;

    @BeforeEach
    void setUp() {
        delegatingInterceptor = new DelegatingInterceptor(delegate);
    }

    // Constructor tests
    @Test
    void testConstructorWithNullDelegate() {
        DelegatingInterceptor interceptor = new DelegatingInterceptor(null);
        assertNotNull(interceptor);
        // Verify it uses EmptyInterceptor.INSTANCE internally
    }

    @Test
    void testConstructorWithNonNullDelegate() {
        assertNotNull(delegatingInterceptor);
        assertSame(delegate, delegatingInterceptor.getDelegate());
    }

    @Test
    void testDefaultConstructor() {
        DelegatingInterceptor interceptor = new DelegatingInterceptor();
        assertNotNull(interceptor);
        // Should use EmptyInterceptor.INSTANCE as delegate
    }

    // onLoad method tests
    @Test
    void testOnLoadDelegation() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        when(delegate.onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types)))
                .thenReturn(true);

        boolean result = delegatingInterceptor.onLoad(entity, id, state, propertyNames, types);

        assertTrue(result);
        verify(delegate).onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    @Test
    void testDeprecatedOnLoadSignatureDelegation() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Serializable id = "test-id";
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        when(delegate.onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types)))
                .thenReturn(true);

        boolean result = delegatingInterceptor.onLoad(entity, id, state, propertyNames, types);

        assertTrue(result);
        verify(delegate).onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // onPersist method tests
    @Test
    void testOnPersistDelegation() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        when(delegate.onPersist(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types)))
                .thenReturn(true);

        boolean result = delegatingInterceptor.onPersist(entity, id, state, propertyNames, types);

        assertTrue(result);
        verify(delegate).onPersist(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // onRemove method tests
    @Test
    void testOnRemoveDelegation() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        delegatingInterceptor.onRemove(entity, id, state, propertyNames, types);

        verify(delegate).onRemove(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // onFlushDirty method tests
    @Test
    void testOnFlushDirtyDelegation() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] currentState = new Object[]{};
        Object[] previousState = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        when(delegate.onFlushDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types)))
                .thenReturn(true);

        boolean result = delegatingInterceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);

        assertTrue(result);
        verify(delegate).onFlushDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types));
    }

    @Test
    void testDeprecatedOnFlushDirtySignatureDelegation() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Serializable id = "test-id";
        Object[] currentState = new Object[]{};
        Object[] previousState = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        when(delegate.onFlushDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types)))
                .thenReturn(true);

        boolean result = delegatingInterceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);

        assertTrue(result);
        verify(delegate).onFlushDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types));
    }

    // onRemove method tests
    @Test
    void testOnRemoveDelegationWithSerializableId() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Serializable id = "test-id";
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        delegatingInterceptor.onRemove(entity, id, state, propertyNames, types);

        verify(delegate).onRemove(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // preFlush method tests
    @Test
    void testPreFlushDelegation() throws org.hibernate.CallbackException {
        Iterator<Object> entities = mock(Iterator.class);

        delegatingInterceptor.preFlush(entities);

        verify(delegate).preFlush(entities);
    }

    // postFlush method tests
    @Test
    void testPostFlushDelegation() throws org.hibernate.CallbackException {
        Iterator<Object> entities = mock(Iterator.class);

        delegatingInterceptor.postFlush(entities);

        verify(delegate).postFlush(entities);
    }

    // isTransient method tests
    @Test
    void testIsTransientDelegation() {
        Object entity = new Object();
        Boolean expected = true;

        when(delegate.isTransient(entity)).thenReturn(expected);

        Boolean result = delegatingInterceptor.isTransient(entity);

        assertEquals(expected, result);
        verify(delegate).isTransient(entity);
    }

    // findDirty method tests
    @Test
    void testFindDirtyDelegation() {
        Object entity = new Object();
        Object id = new Object();
        Object[] currentState = new Object[]{};
        Object[] previousState = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};
        int[] expected = new int[]{0, 1};

        when(delegate.findDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types)))
                .thenReturn(expected);

        int[] result = delegatingInterceptor.findDirty(entity, id, currentState, previousState, propertyNames, types);

        assertArrayEquals(expected, result);
        verify(delegate).findDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types));
    }

    // instantiate method tests
    @Test
    void testInstantiateDelegation() throws org.hibernate.CallbackException {
        String entityName = "test-entity";
        org.hibernate.metamodel.spi.EntityRepresentationStrategy representationStrategy = mock(org.hibernate.metamodel.spi.EntityRepresentationStrategy.class);
        Object id = new Object();
        Object expected = new Object();

        when(delegate.instantiate(eq(entityName), eq(representationStrategy), eq(id)))
                .thenReturn(expected);

        Object result = delegatingInterceptor.instantiate(entityName, representationStrategy, id);

        assertEquals(expected, result);
        verify(delegate).instantiate(eq(entityName), eq(representationStrategy), eq(id));
    }

    // getEntityName method tests
    @Test
    void testGetEntityNameDelegation() throws org.hibernate.CallbackException {
        Object object = new Object();
        String expected = "test-entity";

        when(delegate.getEntityName(object)).thenReturn(expected);

        String result = delegatingInterceptor.getEntityName(object);

        assertEquals(expected, result);
        verify(delegate).getEntityName(object);
    }

    // getEntity method tests
    @Test
    void testGetEntityDelegation() throws org.hibernate.CallbackException {
        String entityName = "test-entity";
        Object id = new Object();
        Object expected = new Object();

        when(delegate.getEntity(eq(entityName), eq(id))).thenReturn(expected);

        Object result = delegatingInterceptor.getEntity(entityName, id);

        assertEquals(expected, result);
        verify(delegate).getEntity(eq(entityName), eq(id));
    }

    // transaction lifecycle tests
    @Test
    void testAfterTransactionBeginDelegation() {
        org.hibernate.Transaction tx = mock(org.hibernate.Transaction.class);

        delegatingInterceptor.afterTransactionBegin(tx);

        verify(delegate).afterTransactionBegin(tx);
    }

    @Test
    void testBeforeTransactionCompletionDelegation() {
        org.hibernate.Transaction tx = mock(org.hibernate.Transaction.class);

        delegatingInterceptor.beforeTransactionCompletion(tx);

        verify(delegate).beforeTransactionCompletion(tx);
    }

    @Test
    void testAfterTransactionCompletionDelegation() {
        org.hibernate.Transaction tx = mock(org.hibernate.Transaction.class);

        delegatingInterceptor.afterTransactionCompletion(tx);

        verify(delegate).afterTransactionCompletion(tx);
    }

    // onInsert method tests
    @Test
    void testOnInsertDelegation() {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] propertyTypes = new org.hibernate.type.Type[]{};

        delegatingInterceptor.onInsert(entity, id, state, propertyNames, propertyTypes);

        verify(delegate).onInsert(eq(entity), eq(id), eq(state), eq(propertyNames), eq(propertyTypes));
    }

    // onUpdate method tests
    @Test
    void testOnUpdateDelegation() {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] propertyTypes = new org.hibernate.type.Type[]{};

        delegatingInterceptor.onUpdate(entity, id, state, propertyNames, propertyTypes);

        verify(delegate).onUpdate(eq(entity), eq(id), eq(state), eq(propertyNames), eq(propertyTypes));
    }

    // onUpsert method tests
    @Test
    void testOnUpsertDelegation() {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] propertyTypes = new org.hibernate.type.Type[]{};

        delegatingInterceptor.onUpsert(entity, id, state, propertyNames, propertyTypes);

        verify(delegate).onUpsert(eq(entity), eq(id), eq(state), eq(propertyNames), eq(propertyTypes));
    }

    // onDelete method tests
    @Test
    void testOnDeleteDelegation() {
        Object entity = new Object();
        Object id = new Object();
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] propertyTypes = new org.hibernate.type.Type[]{};

        delegatingInterceptor.onDelete(entity, id, propertyNames, propertyTypes);

        verify(delegate).onDelete(eq(entity), eq(id), eq(propertyNames), eq(propertyTypes));
    }

    // getDelegate method tests
    @Test
    void testGetDelegateReturnsWrappedInterceptor() {
        assertSame(delegate, delegatingInterceptor.getDelegate());
    }

    // Exception propagation tests
    @Test
    void testCallbackExceptionPropagation() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        org.hibernate.CallbackException expectedException = new org.hibernate.CallbackException("test exception");
        doThrow(expectedException).when(delegate).onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));

        org.hibernate.CallbackException thrownException = assertThrows(
                org.hibernate.CallbackException.class,
                () -> delegatingInterceptor.onLoad(entity, id, state, propertyNames, types)
        );

        assertEquals(expectedException.getMessage(), thrownException.getMessage());
        verify(delegate).onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // Null parameter handling tests
    @Test
    void testOnLoadWithNullParameters() throws org.hibernate.CallbackException {
        // Test with null parameters - should not throw NPE
        boolean result = delegatingInterceptor.onLoad(null, null, null, null, null);
        // Should delegate and handle gracefully
        verify(delegate, atLeastOnce()).onLoad(any(), any(), any(), any(), any());
    }
}
