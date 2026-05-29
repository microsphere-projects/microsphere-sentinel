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

package io.microsphere.alibaba.sentinel.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static com.alibaba.csp.sentinel.EntryType.IN;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON;
import static io.microsphere.alibaba.sentinel.common.SentinelPluginRepository.INSTANCE;
import static io.microsphere.alibaba.sentinel.common.constants.SentinelConstants.DEFAULT_CONTEXT_NAME;
import static io.microsphere.alibaba.sentinel.common.constants.SentinelConstants.DEFAULT_ORIGIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SentinelPluginRepository} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SentinelPluginRepository
 * @since 1.0.0
 */
public class SentinelPluginRepositoryTest {

    private SentinelPluginRepository repository;

    @BeforeEach
    void setUp() {
        this.repository = createSentinelPluginRepository();
    }

    @AfterEach
    void tearDown() {
        this.repository.clear();
    }

    /**
     * Create a new {@link SentinelPluginRepository}
     *
     * @return a new {@link SentinelPluginRepository}
     */
    protected SentinelPluginRepository createSentinelPluginRepository() {
        return INSTANCE;
    }

    @Test
    void test() {
        String pluginName1 = "plugin-1";
        String pluginName2 = "plugin-2";
        String pluginName3 = "plugin-3";
        SentinelPlugin plugin1 = newPlugin(pluginName1);
        SentinelPlugin plugin2 = newPlugin(pluginName2);

        // test install
        assertTrue(this.repository.install(plugin1));
        assertTrue(this.repository.install(plugin2));
        assertFalse(this.repository.install(plugin2));
        assertThrows(NullPointerException.class, () -> this.repository.install(null));
        this.repository.clear();
        assertTrue(this.repository.install(plugin1));
        assertTrue(this.repository.install(plugin2));

        // test isInstalled
        assertTrue(this.repository.isInstalled(pluginName1));
        assertTrue(this.repository.isInstalled(pluginName2));
        assertFalse(this.repository.isInstalled(pluginName3));

        // test get
        SentinelPlugin plugin = this.repository.get(pluginName1);
        assertEquals(plugin1, plugin);
        plugin = this.repository.get(pluginName2);
        assertEquals(plugin2, plugin);
        plugin = this.repository.get(pluginName3);
        assertNull(plugin);

        // test getAll
        Collection<SentinelPlugin> plugins = this.repository.getAll();
        assertEquals(2, plugins.size());

        // test uninstall
        assertTrue(this.repository.uninstall(newPlugin(pluginName1)));
        plugins = this.repository.getAll();
        assertEquals(1, plugins.size());

        assertTrue(this.repository.uninstall(pluginName2));
        assertFalse(this.repository.uninstall(pluginName2));
        plugins = this.repository.getAll();
        assertEquals(0, plugins.size());

        assertThrows(NullPointerException.class, () -> this.repository.uninstall((SentinelPlugin) null));
        assertThrows(NullPointerException.class, () -> this.repository.uninstall((String) null));
    }

    SentinelPlugin newPlugin(String name) {
        return new SimpleSentinelPlugin(name, DEFAULT_CONTEXT_NAME, DEFAULT_ORIGIN, COMMON, IN, false);
    }
}