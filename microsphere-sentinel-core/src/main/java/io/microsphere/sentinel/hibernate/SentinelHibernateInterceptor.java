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

import io.microsphere.sentinel.util.SentinelUtils;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Sentinel x Hibernate {@link Interceptor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see DelegatingInterceptor
 * @see Interceptor
 * @since 1.0.0
 */
public class SentinelHibernateInterceptor extends DelegatingInterceptor {

    public SentinelHibernateInterceptor(Interceptor delegate) {
        super(delegate == null ? EmptyInterceptor.INSTANCE : delegate);
    }

    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return doInSentinel(entity, "LOAD", () -> super.onLoad(entity, id, state, propertyNames, types));
    }

    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        return doInSentinel(entity, "SAVE", () -> super.onSave(entity, id, state, propertyNames, types));
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        doInSentinel(entity, "DELETE", () -> super.onDelete(entity, id, state, propertyNames, types));
    }

    protected void doInSentinel(Object entity, String action, Runnable runnable) {
        doInSentinel(entity, action, () -> {
            runnable.run();
            return null;
        });
    }

    protected <T> T doInSentinel(Object entity, String action, Callable<T> callable) {
        String resourceName = getSentinelResourceName(entity, action);
        return SentinelUtils.doInSentinel(resourceName, "microsphere-hibernate-context", "SessionFactory", callable, e -> {
            if (e instanceof CallbackException) {
                throw (CallbackException) e;
            } else {
                throw new RuntimeException(e);
            }
        });
    }

    private String getSentinelResourceName(Object entity, String action) {
        String className = entity.getClass().getName();
        return "Entity:" + action + ":" + className;
    }
}
