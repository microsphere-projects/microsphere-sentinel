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

import com.alibaba.csp.sentinel.EntryType;

import static com.alibaba.csp.sentinel.EntryType.IN;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON;

/**
 * Abstract {@link SentinelPlugin} class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SentinelPlugin
 * @since 1.0.0
 */
public abstract class AbstractSentinelPlugin implements SentinelPlugin {

    protected final String name;

    protected final String contextName;

    protected final String origin;

    protected final int resourceType;

    protected final EntryType trafficType;

    private volatile boolean enabled;

    protected AbstractSentinelPlugin(String name, String contextName, String origin) {
        this(name, contextName, origin, COMMON);
    }

    protected AbstractSentinelPlugin(String name, String contextName, String origin, int resourceType) {
        this(name, contextName, origin, resourceType, IN);
    }

    protected AbstractSentinelPlugin(String name, String contextName, String origin, int resourceType, EntryType trafficType) {
        this.name = name;
        this.contextName = contextName;
        this.origin = origin;
        this.resourceType = resourceType;
        this.trafficType = trafficType;
        this.enabled = SentinelPlugin.super.isEnabled();
    }

    /**
     * Enable this {@link SentinelPlugin}
     */
    public void enable() {
        setEnabled(true);
    }

    /**
     * Disable this {@link SentinelPlugin}
     */
    public void disable() {
        setEnabled(false);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getContextName() {
        return this.contextName;
    }

    @Override
    public String getOrigin() {
        return this.origin;
    }

    public int getResourceType() {
        return this.resourceType;
    }

    public EntryType getTrafficType() {
        return this.trafficType;
    }
}