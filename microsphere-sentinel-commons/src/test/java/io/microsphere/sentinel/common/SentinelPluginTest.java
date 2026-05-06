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

import static io.microsphere.sentinel.util.SentinelUtils.DEFAULT_ORIGIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * {@link SentinelPlugin} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SentinelPlugin
 * @since 1.0.0
 */
class SentinelPluginTest {

    private SentinelPlugin plugin;

    @BeforeEach
    void setUp() {
        this.plugin = new SentinelPlugin() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getOrigin() {
                return "";
            }
        };
    }

    @Test
    void testGetters() {
        assertEquals("test", plugin.getName());
        assertEquals("microsphere_sentinel_test_context", plugin.getContextName());
        assertEquals(DEFAULT_ORIGIN, plugin.getOrigin());
        assertFalse(plugin.isEnabled());
    }
}