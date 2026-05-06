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


import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;

import static io.microsphere.sentinel.hibernate.SentinelHibernateInterceptor.DEFAULT_CONTEXT_NAME;
import static io.microsphere.sentinel.hibernate.SentinelHibernateInterceptor.DEFAULT_ORIGIN;
import static io.microsphere.sentinel.hibernate.SentinelHibernateInterceptor.PLUGIN_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SentinelHibernateInterceptor} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SentinelHibernateInterceptor
 * @since 1.0.0
 */
class SentinelHibernateInterceptorTest extends AbstractSentinelHibernateTest {

    private SentinelHibernateInterceptor listener;

    @Override
    protected void customizeConfiguration(Configuration configuration) {
        this.listener = new SentinelHibernateInterceptor();
        // Reset the default enabled state
        this.listener.setEnabled(true);
        configuration.setInterceptor(this.listener);
    }

    @Test
    void testConstants() {
        assertEquals("hibernate", PLUGIN_NAME);
        assertEquals("microsphere_sentinel_hibernate_context", DEFAULT_CONTEXT_NAME);
        assertEquals("SessionFactory", DEFAULT_ORIGIN);
    }

    @Test
    void testDefaults() {
        assertTrue(this.listener.isEnabled());
        assertEquals(PLUGIN_NAME, this.listener.getName());
        assertEquals(DEFAULT_CONTEXT_NAME, this.listener.getContextName());
        assertEquals(DEFAULT_ORIGIN, this.listener.getOrigin());
    }

    @Test
    void testOnLoad() {
        assertFalse(this.listener.onLoad(null, null, null, null, null));
    }
}