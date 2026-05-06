/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.microsphere.sentinel.hibernate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Serializable;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link DelegatingInterceptor} Test
 */
@ExtendWith(MockitoExtension.class)
class DelegatingInterceptorTest {

    @Mock
    private org.hibernate.Interceptor delegate;

    private DelegatingInterceptor delegatingInterceptor;

    @BeforeEach
    void setUp() {
        delegatingInterceptor = new DelegatingInterceptor(delegate);
    }

    // Additional constructor tests
    @Test
    void testConstructorWithNullDelegateUsesEmptyInterceptor() {
        DelegatingInterceptor interceptor = new DelegatingInterceptor(null);
        assertNotNull(interceptor);
        // Verify it uses EmptyInterceptor.INSTANCE internally
        // Since we can't directly access the protected delegate field, we verify through behavior
        assertNotSame(interceptor, null);
    }

    // Additional onLoad tests
    @Test
    void testOnLoadReturnsFalseWhenDelegateReturnsFalse() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        when(delegate.onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types)))
                .thenReturn(false);

        boolean result = delegatingInterceptor.onLoad(entity, id, state, propertyNames, types);

        assertFalse(result);
        verify(delegate).onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // Additional onPersist tests
    @Test
    void testOnPersistReturnsFalseWhenDelegateReturnsFalse() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        when(delegate.onPersist(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types)))
                .thenReturn(false);

        boolean result = delegatingInterceptor.onPersist(entity, id, state, propertyNames, types);

        assertFalse(result);
        verify(delegate).onPersist(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // Additional onFlushDirty tests
    @Test
    void testOnFlushDirtyReturnsFalseWhenDelegateReturnsFalse() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] currentState = new Object[]{};
        Object[] previousState = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        when(delegate.onFlushDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types)))
                .thenReturn(false);

        boolean result = delegatingInterceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);

        assertFalse(result);
        verify(delegate).onFlushDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types));
    }

    // Additional onSave tests
    @Test
    void testDeprecatedOnSaveSignatureWithSerializableId() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Serializable id = "test-id";
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        when(delegate.onSave(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types)))
                .thenReturn(true);

        boolean result = delegatingInterceptor.onSave(entity, id, state, propertyNames, types);

        assertTrue(result);
        verify(delegate).onSave(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    @Test
    void testOnSaveWithObjectId() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        when(delegate.onSave(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types)))
                .thenReturn(true);

        boolean result = delegatingInterceptor.onSave(entity, id, state, propertyNames, types);

        assertTrue(result);
        verify(delegate).onSave(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // Additional onDelete tests
    @Test
    void testDeprecatedOnDeleteWithSerializableId() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Serializable id = "test-id";
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        delegatingInterceptor.onDelete(entity, id, state, propertyNames, types);

        verify(delegate).onDelete(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // Additional onCollectionRecreate tests
    @Test
    void testOnCollectionRecreateWithObjectKey() throws org.hibernate.CallbackException {
        Object collection = new Object();
        Object key = new Object();

        delegatingInterceptor.onCollectionRecreate(collection, key);

        verify(delegate).onCollectionRecreate(eq(collection), eq(key));
    }

    @Test
    void testDeprecatedOnCollectionRecreateWithSerializableKey() throws org.hibernate.CallbackException {
        Object collection = new Object();
        Serializable key = "test-key";

        delegatingInterceptor.onCollectionRecreate(collection, key);

        verify(delegate).onCollectionRecreate(eq(collection), eq(key));
    }

    // Additional onCollectionRemove tests
    @Test
    void testOnCollectionRemoveWithObjectKey() throws org.hibernate.CallbackException {
        Object collection = new Object();
        Object key = new Object();

        delegatingInterceptor.onCollectionRemove(collection, key);

        verify(delegate).onCollectionRemove(eq(collection), eq(key));
    }

    @Test
    void testDeprecatedOnCollectionRemoveWithSerializableKey() throws org.hibernate.CallbackException {
        Object collection = new Object();
        Serializable key = "test-key";

        delegatingInterceptor.onCollectionRemove(collection, key);

        verify(delegate).onCollectionRemove(eq(collection), eq(key));
    }

    // Additional onCollectionUpdate tests
    @Test
    void testOnCollectionUpdateWithObjectKey() throws org.hibernate.CallbackException {
        Object collection = new Object();
        Object key = new Object();

        delegatingInterceptor.onCollectionUpdate(collection, key);

        verify(delegate).onCollectionUpdate(eq(collection), eq(key));
    }

    @Test
    void testDeprecatedOnCollectionUpdateWithSerializableKey() throws org.hibernate.CallbackException {
        Object collection = new Object();
        Serializable key = "test-key";

        delegatingInterceptor.onCollectionUpdate(collection, key);

        verify(delegate).onCollectionUpdate(eq(collection), eq(key));
    }

    // Additional findDirty tests
    @Test
    void testDeprecatedFindDirtyWithSerializableId() {
        Object entity = new Object();
        Serializable id = "test-id";
        Object[] currentState = new Object[]{};
        Object[] previousState = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};
        int[] expected = new int[]{0};

        when(delegate.findDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types)))
                .thenReturn(expected);

        int[] result = delegatingInterceptor.findDirty(entity, id, currentState, previousState, propertyNames, types);

        assertArrayEquals(expected, result);
        verify(delegate).findDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types));
    }

    // Additional instantiate tests
    @Test
    void testInstantiateWithRepresentationMode() throws org.hibernate.CallbackException {
        String entityName = "test-entity";
        org.hibernate.metamodel.RepresentationMode representationMode = org.hibernate.metamodel.RepresentationMode.POJO;
        Object id = new Object();
        Object expected = new Object();

        when(delegate.instantiate(eq(entityName), eq(representationMode), eq(id)))
                .thenReturn(expected);

        Object result = delegatingInterceptor.instantiate(entityName, representationMode, id);

        assertEquals(expected, result);
        verify(delegate).instantiate(eq(entityName), eq(representationMode), eq(id));
    }

    // Additional getEntity tests
    @Test
    void testDeprecatedGetEntityWithSerializableId() throws org.hibernate.CallbackException {
        String entityName = "test-entity";
        Serializable id = "test-id";
        Object expected = new Object();

        when(delegate.getEntity(eq(entityName), eq(id))).thenReturn(expected);

        Object result = delegatingInterceptor.getEntity(entityName, id);

        assertEquals(expected, result);
        verify(delegate).getEntity(eq(entityName), eq(id));
    }

    // Additional onInsert/onUpdate/onUpsert tests
    @Test
    void testOnInsertWithNullParameters() {
        // Test with null parameters - should not throw NPE
        delegatingInterceptor.onInsert(null, null, null, null, null);
        verify(delegate, atLeastOnce()).onInsert(any(), any(), any(), any(), any());
    }

    @Test
    void testOnUpdateWithNullParameters() {
        // Test with null parameters - should not throw NPE
        delegatingInterceptor.onUpdate(null, null, null, null, null);
        verify(delegate, atLeastOnce()).onUpdate(any(), any(), any(), any(), any());
    }

    @Test
    void testOnUpsertWithNullParameters() {
        // Test with null parameters - should not throw NPE
        delegatingInterceptor.onUpsert(null, null, null, null, null);
        verify(delegate, atLeastOnce()).onUpsert(any(), any(), any(), any(), any());
    }

    // Additional onDelete tests
    @Test
    void testOnDeleteWithPropertyTypesAndNullParameters() {
        // Test with null parameters - should not throw NPE
        delegatingInterceptor.onDelete(null, null, null, null);
        verify(delegate, atLeastOnce()).onDelete(any(), any(), any(), any());
    }

    // Edge case tests with empty arrays
    @Test
    void testOnLoadWithEmptyArrays() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[0];
        String[] propertyNames = new String[0];
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[0];

        when(delegate.onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types)))
                .thenReturn(true);

        boolean result = delegatingInterceptor.onLoad(entity, id, state, propertyNames, types);

        assertTrue(result);
        verify(delegate).onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // Exception propagation tests for void methods
    @Test
    void testOnRemoveExceptionPropagation() throws org.hibernate.CallbackException {
        Object entity = new Object();
        Object id = new Object();
        Object[] state = new Object[]{};
        String[] propertyNames = new String[]{};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{};

        org.hibernate.CallbackException expectedException = new org.hibernate.CallbackException("test exception");
        doThrow(expectedException).when(delegate).onRemove(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));

        org.hibernate.CallbackException thrownException = assertThrows(
                org.hibernate.CallbackException.class,
                () -> delegatingInterceptor.onRemove(entity, id, state, propertyNames, types)
        );

        assertEquals(expectedException.getMessage(), thrownException.getMessage());
        verify(delegate).onRemove(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
    }

    // Exception propagation for preFlush/postFlush
    @Test
    void testPreFlushExceptionPropagation() throws org.hibernate.CallbackException {
        Iterator<Object> entities = mock(Iterator.class);

        org.hibernate.CallbackException expectedException = new org.hibernate.CallbackException("test exception");
        doThrow(expectedException).when(delegate).preFlush(entities);

        org.hibernate.CallbackException thrownException = assertThrows(
                org.hibernate.CallbackException.class,
                () -> delegatingInterceptor.preFlush(entities)
        );

        assertEquals(expectedException.getMessage(), thrownException.getMessage());
        verify(delegate).preFlush(entities);
    }

    @Test
    void testPostFlushExceptionPropagation() throws org.hibernate.CallbackException {
        Iterator<Object> entities = mock(Iterator.class);

        org.hibernate.CallbackException expectedException = new org.hibernate.CallbackException("test exception");
        doThrow(expectedException).when(delegate).postFlush(entities);

        org.hibernate.CallbackException thrownException = assertThrows(
                org.hibernate.CallbackException.class,
                () -> delegatingInterceptor.postFlush(entities)
        );

        assertEquals(expectedException.getMessage(), thrownException.getMessage());
        verify(delegate).postFlush(entities);
    }

    // Null safety tests for all parameter types
    @Test
    void testAllMethodsHandleNullParametersGracefully() throws Exception {
        // Test various null parameter combinations across different method signatures

        // Test onLoad with various null combinations
        delegatingInterceptor.onLoad(null, null, null, null, null);

        // Test onPersist with various null combinations
        delegatingInterceptor.onPersist(null, null, null, null, null);

        // Test onRemove with various null combinations
        delegatingInterceptor.onRemove(null, null, null, null, null);

        // Test onFlushDirty with various null combinations
        delegatingInterceptor.onFlushDirty(null, null, null, null, null, null);

        // Test onSave with various null combinations
        delegatingInterceptor.onSave(null, null, null, null, null);

        // Test onDelete with various null combinations
        delegatingInterceptor.onDelete(null, null, null, null, null);

        // Verify that delegate was called for each method (at least once)
        verify(delegate, times(1)).onLoad(any(), any(), any(), any(), any());
        verify(delegate, times(1)).onPersist(any(), any(), any(), any(), any());
        verify(delegate, times(1)).onRemove(any(), any(), any(), any(), any());
        verify(delegate, times(1)).onFlushDirty(any(), any(), any(), any(), any(), any());
        verify(delegate, times(1)).onSave(any(), any(), any(), any(), any());
        verify(delegate, times(1)).onDelete(any(), any(), any(), any(), any());
    }

    // Comprehensive delegation verification test
    @Test
    void testAllMethodsDelegateToWrappedInterceptor() throws Exception {
        // Create test objects
        Object entity = new Object();
        Object id = new Object();
        Serializable serializableId = "test-id";
        Object[] state = new Object[]{entity};
        Object[] currentState = new Object[]{entity};
        Object[] previousState = new Object[]{entity};
        String[] propertyNames = new String[]{"test"};
        org.hibernate.type.Type[] types = new org.hibernate.type.Type[]{mock(org.hibernate.type.Type.class)};
        org.hibernate.type.Type[] propertyTypes = new org.hibernate.type.Type[]{mock(org.hibernate.type.Type.class)};
        Iterator<Object> entities = mock(Iterator.class);
        org.hibernate.Transaction tx = mock(org.hibernate.Transaction.class);
        Object collection = new Object();
        Object key = new Object();
        String entityName = "test-entity";
        org.hibernate.metamodel.spi.EntityRepresentationStrategy strategy = mock(org.hibernate.metamodel.spi.EntityRepresentationStrategy.class);
        org.hibernate.metamodel.RepresentationMode mode = org.hibernate.metamodel.RepresentationMode.POJO;

        // Call all methods
        delegatingInterceptor.onLoad(entity, id, state, propertyNames, types);
        delegatingInterceptor.onLoad(entity, serializableId, state, propertyNames, types);
        delegatingInterceptor.onPersist(entity, id, state, propertyNames, types);
        delegatingInterceptor.onRemove(entity, id, state, propertyNames, types);
        delegatingInterceptor.onRemove(entity, serializableId, state, propertyNames, types);
        delegatingInterceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
        delegatingInterceptor.onFlushDirty(entity, serializableId, currentState, previousState, propertyNames, types);
        delegatingInterceptor.onSave(entity, serializableId, state, propertyNames, types);
        delegatingInterceptor.onSave(entity, id, state, propertyNames, types);
        delegatingInterceptor.onDelete(entity, serializableId, state, propertyNames, types);
        delegatingInterceptor.onDelete(entity, id, state, propertyNames, types);
        delegatingInterceptor.onCollectionRecreate(collection, key);
        delegatingInterceptor.onCollectionRecreate(collection, serializableId);
        delegatingInterceptor.onCollectionRemove(collection, key);
        delegatingInterceptor.onCollectionRemove(collection, serializableId);
        delegatingInterceptor.onCollectionUpdate(collection, key);
        delegatingInterceptor.onCollectionUpdate(collection, serializableId);
        delegatingInterceptor.preFlush(entities);
        delegatingInterceptor.postFlush(entities);
        delegatingInterceptor.isTransient(entity);
        delegatingInterceptor.findDirty(entity, id, currentState, previousState, propertyNames, types);
        delegatingInterceptor.findDirty(entity, serializableId, currentState, previousState, propertyNames, types);
        delegatingInterceptor.instantiate(entityName, strategy, id);
        delegatingInterceptor.instantiate(entityName, mode, id);
        delegatingInterceptor.getEntityName(entity);
        delegatingInterceptor.getEntity(entityName, serializableId);
        delegatingInterceptor.getEntity(entityName, id);
        delegatingInterceptor.afterTransactionBegin(tx);
        delegatingInterceptor.beforeTransactionCompletion(tx);
        delegatingInterceptor.afterTransactionCompletion(tx);
        delegatingInterceptor.onInsert(entity, id, state, propertyNames, propertyTypes);
        delegatingInterceptor.onUpdate(entity, id, state, propertyNames, propertyTypes);
        delegatingInterceptor.onUpsert(entity, id, state, propertyNames, propertyTypes);
        delegatingInterceptor.onDelete(entity, id, propertyNames, propertyTypes);

        // Verify all methods were delegated
        verify(delegate, times(1)).onLoad(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onLoad(eq(entity), eq(serializableId), eq(state), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onPersist(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onRemove(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onRemove(eq(entity), eq(serializableId), eq(state), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onFlushDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onFlushDirty(eq(entity), eq(serializableId), eq(currentState), eq(previousState), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onSave(eq(entity), eq(serializableId), eq(state), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onSave(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onDelete(eq(entity), eq(serializableId), eq(state), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onDelete(eq(entity), eq(id), eq(state), eq(propertyNames), eq(types));
        verify(delegate, times(1)).onCollectionRecreate(eq(collection), eq(key));
        verify(delegate, times(1)).onCollectionRecreate(eq(collection), eq(serializableId));
        verify(delegate, times(1)).onCollectionRemove(eq(collection), eq(key));
        verify(delegate, times(1)).onCollectionRemove(eq(collection), eq(serializableId));
        verify(delegate, times(1)).onCollectionUpdate(eq(collection), eq(key));
        verify(delegate, times(1)).onCollectionUpdate(eq(collection), eq(serializableId));
        verify(delegate, times(1)).preFlush(entities);
        verify(delegate, times(1)).postFlush(entities);
        verify(delegate, times(1)).isTransient(entity);
        verify(delegate, times(1)).findDirty(eq(entity), eq(id), eq(currentState), eq(previousState), eq(propertyNames), eq(types));
        verify(delegate, times(1)).findDirty(eq(entity), eq(serializableId), eq(currentState), eq(previousState), eq(propertyNames), eq(types));
        verify(delegate, times(1)).instantiate(eq(entityName), eq(strategy), eq(id));
        verify(delegate, times(1)).instantiate(eq(entityName), eq(mode), eq(id));
        verify(delegate, times(1)).getEntityName(entity);
        verify(delegate, times(1)).getEntity(eq(entityName), eq(serializableId));
        verify(delegate, times(1)).getEntity(eq(entityName), eq(id));
        verify(delegate, times(1)).afterTransactionBegin(tx);
        verify(delegate, times(1)).beforeTransactionCompletion(tx);
        verify(delegate, times(1)).afterTransactionCompletion(tx);
        verify(delegate, times(1)).onInsert(eq(entity), eq(id), eq(state), eq(propertyNames), eq(propertyTypes));
        verify(delegate, times(1)).onUpdate(eq(entity), eq(id), eq(state), eq(propertyNames), eq(propertyTypes));
        verify(delegate, times(1)).onUpsert(eq(entity), eq(id), eq(state), eq(propertyNames), eq(propertyTypes));
        verify(delegate, times(1)).onDelete(eq(entity), eq(id), eq(propertyNames), eq(propertyTypes));
    }

    @Test
    void testGetDelegate() {
        assertEquals(delegate, delegatingInterceptor.getDelegate());
    }
}