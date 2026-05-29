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

import io.microsphere.annotation.Nonnull;

import static io.microsphere.sentinel.common.util.SentinelUtils.getDefaultContextName;
import static io.microsphere.sentinel.common.util.SentinelUtils.isPluginEnabled;

/**
 * The Plugin interface for Sentinel
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface SentinelPlugin {

    /**
     * Determine whether the plugin is enabled
     *
     * @return if enabled , return {@code true} , else return {@code false}
     */
    default boolean isEnabled() {
        return isPluginEnabled(getName());
    }

    /**
     * Set the plugin enabled or not
     *
     * @param enabled
     */
    void setEnabled(boolean enabled);

    /**
     * The plugin name of Sentinel
     *
     * @return non-null
     */
    @Nonnull
    String getName();

    /**
     * The context name that Sentinel Plugin belongs to
     *
     * @return non-null
     */
    @Nonnull
    default String getContextName() {
        return getDefaultContextName(getName());
    }

    /**
     * The origin that Sentinel Plugin belongs to
     *
     * @return non-null
     */
    @Nonnull
    String getOrigin();
}
