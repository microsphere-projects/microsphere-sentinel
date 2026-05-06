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

import io.microsphere.annotation.Nonnull;
import io.microsphere.annotation.Nullable;
import io.microsphere.sentinel.common.SentinelOperations;
import io.microsphere.sentinel.common.SentinelPlugin;
import io.microsphere.sentinel.common.SentinelTemplate;
import io.microsphere.sentinel.common.SimpleSentinelPlugin;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

import java.util.function.Supplier;

import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_DB_SQL;
import static io.microsphere.sentinel.hibernate.Constants.DEFAULT_CONTEXT_NAME;
import static io.microsphere.sentinel.hibernate.Constants.DEFAULT_ORIGIN;
import static io.microsphere.sentinel.hibernate.Constants.PLUGIN_NAME;

/**
 * Sentinel x Hibernate {@link Interceptor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see Interceptor
 * @since 1.0.0
 */
public class SentinelHibernateInterceptor extends DelegatingInterceptor implements Interceptor, SentinelPlugin {

    private final SentinelPlugin delegate;

    private final SentinelOperations sentinelOperations;

    public SentinelHibernateInterceptor() {
        this(null);
    }

    public SentinelHibernateInterceptor(@Nullable Interceptor interceptor) {
        this(interceptor, DEFAULT_CONTEXT_NAME, DEFAULT_ORIGIN);
    }

    public SentinelHibernateInterceptor(@Nullable Interceptor interceptor, @Nonnull String contextName, @Nonnull String origin) {
        super(interceptor);
        this.delegate = new SimpleSentinelPlugin(PLUGIN_NAME, contextName, origin);
        this.sentinelOperations = new SentinelTemplate(COMMON_DB_SQL);
    }

    @Override
    public boolean onLoad(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return doInSentinel(entity, "LOAD", () -> super.onLoad(entity, id, state, propertyNames, types));
    }

    @Override
    public boolean onPersist(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return doInSentinel(entity, "PERSIST", () -> super.onPersist(entity, id, state, propertyNames, types));
    }

    @Override
    public void onRemove(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        doInSentinel(entity, "REMOVE", () -> super.onRemove(entity, id, state, propertyNames, types));
    }

    @Override
    public boolean onFlushDirty(Object entity, Object id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        return doInSentinel(entity, "FLUSH_DIRTY", () -> super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types));
    }

    // For {@link StatelessSession}
    @Override
    public void onInsert(Object entity, Object id, Object[] state, String[] propertyNames, Type[] propertyTypes) {
        doInSentinel(entity, "INSERT", () -> super.onInsert(entity, id, state, propertyNames, propertyTypes));
    }

    @Override
    public void onUpdate(Object entity, Object id, Object[] state, String[] propertyNames, Type[] propertyTypes) {
        doInSentinel(entity, "UPDATE", () -> super.onUpdate(entity, id, state, propertyNames, propertyTypes));
    }

    @Override
    public void onUpsert(Object entity, Object id, Object[] state, String[] propertyNames, Type[] propertyTypes) {
        doInSentinel(entity, "UPSERT", () -> super.onUpsert(entity, id, state, propertyNames, propertyTypes));
    }

    @Override
    public void onDelete(Object entity, Object id, String[] propertyNames, Type[] propertyTypes) {
        doInSentinel(entity, "DELETE", () -> super.onDelete(entity, id, propertyNames, propertyTypes));
    }

    protected void doInSentinel(Object entity, String action, Runnable runnable) {
        doInSentinel(entity, action, () -> {
            runnable.run();
            return null;
        });
    }

    protected <T> T doInSentinel(Object entity, String action, Supplier<T> callable) {
        if (isEnabled()) {
            String resourceName = getSentinelResourceName(entity, action);
            return this.sentinelOperations.execute(resourceName, this.getContextName(), this.getOrigin(), context -> (T) callable.get());
        } else {
            return callable.get();
        }
    }

    protected String getSentinelResourceName(Object entity, String action) {
        String className = entity.getClass().getName();
        return "Entity:" + action + ":" + className;
    }

    @Override
    public boolean isEnabled() {
        return this.delegate.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.delegate.setEnabled(enabled);
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public String getContextName() {
        return this.delegate.getContextName();
    }

    @Override
    public String getOrigin() {
        return this.delegate.getOrigin();
    }
}