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

import io.microsphere.annotation.Nonnull;
import io.microsphere.annotation.Nullable;

import java.util.Collection;

import static io.microsphere.util.ServiceLoaderUtils.loadFirstService;

/**
 * {@link SentinelPlugin} Repository
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SentinelPlugin
 * @since 1.0.0
 */
public interface SentinelPluginRepository {

    /**
     * The singleton instance
     */
    SentinelPluginRepository INSTANCE = loadFirstService(SentinelPluginRepository.class, true);

    /**
     * Install the {@link SentinelPlugin Sentinel Plugin}
     *
     * @param plugin the {@link SentinelPlugin} to be installed
     * @return if the {@link SentinelPlugin} is installed in the first time, return <code>true</code>, or <code>false</code>
     * @throws NullPointerException if the {@link SentinelPlugin} is null
     */
    boolean install(SentinelPlugin plugin) throws NullPointerException;

    /**
     * Whether the {@link SentinelPlugin} is installed
     *
     * @param pluginName the name of {@link SentinelPlugin}
     * @return if the {@link SentinelPlugin} is installed, return <code>true</code>, or <code>false</code>
     */
    boolean isInstalled(String pluginName);

    /**
     * Get the {@link SentinelPlugin} by its name
     *
     * @param pluginName the name of {@link SentinelPlugin}
     * @return the {@link SentinelPlugin} or <code>null</code> if not found
     */
    @Nullable
    SentinelPlugin get(String pluginName);

    /**
     * Get all installed {@link SentinelPlugin Sentinel Plugins}
     *
     * @return the {@link SentinelPlugin Sentinel Plugins} never be null
     */
    @Nonnull
    Collection<SentinelPlugin> getAll();

    /**
     * Uninstall the {@link SentinelPlugin}
     *
     * @param plugin the {@link SentinelPlugin} to be uninstalled
     * @return if the {@link SentinelPlugin} is uninstalled, return <code>true</code>, or <code>false</code>
     * @throws NullPointerException if the {@link SentinelPlugin} is null
     */
    default boolean uninstall(SentinelPlugin plugin) throws NullPointerException {
        return uninstall(plugin.getName());
    }

    /**
     * Uninstall the {@link SentinelPlugin}
     *
     * @param pluginName the name of {@link SentinelPlugin} to be uninstalled
     * @return if the {@link SentinelPlugin} is uninstalled by its name, return <code>true</code>, or <code>false</code>
     * @throws NullPointerException if the <code>the</code> is null
     */
    boolean uninstall(String pluginName) throws NullPointerException;

    /**
     * Clear all installed {@link SentinelPlugin Sentinel Plugins}
     */
    void clear();

}