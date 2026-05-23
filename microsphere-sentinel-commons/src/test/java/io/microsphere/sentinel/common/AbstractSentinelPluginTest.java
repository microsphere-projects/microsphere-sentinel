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

package io.microsphere.sentinel.common;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.alibaba.csp.sentinel.EntryType.IN;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON;
import static io.microsphere.sentinel.common.constants.SentinelConstants.DEFAULT_CONTEXT_NAME;
import static io.microsphere.sentinel.common.constants.SentinelConstants.DEFAULT_ORIGIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link AbstractSentinelPlugin} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see AbstractSentinelPlugin
 * @since 1.0.0
 */
class AbstractSentinelPluginTest {

    private AbstractSentinelPlugin plugin;

    @BeforeEach
    void setUp() {
        this.plugin = new SimpleSentinelPlugin("default");
    }

    @Test
    void testGetters() {
        assertEquals("default", plugin.getName());
        assertEquals(DEFAULT_CONTEXT_NAME, plugin.getContextName());
        assertEquals(DEFAULT_ORIGIN, plugin.getOrigin());
        assertEquals(COMMON, plugin.getResourceType());
        assertEquals(IN, plugin.getTrafficType());
        assertTrue(plugin.isEnabled());
    }

    @Test
    void testEnable() {
        assertTrue(plugin.isEnabled());
        plugin.disable();
        assertFalse(plugin.isEnabled());
        plugin.enable();
        assertTrue(plugin.isEnabled());
    }
}