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
import io.microsphere.lang.function.ThrowableBiFunction;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import static com.alibaba.csp.sentinel.EntryType.IN;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON;
import static io.microsphere.lang.function.ThrowableSupplier.execute;
import static io.microsphere.text.FormatUtils.format;
import static io.microsphere.util.ShutdownHookUtils.addShutdownHookCallback;
import static java.lang.management.ManagementFactory.getPlatformMBeanServer;

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

    protected final boolean autoRegisterMBean;

    private volatile boolean enabled;

    protected AbstractSentinelPlugin(String name, String contextName, String origin) {
        this(name, contextName, origin, COMMON);
    }

    protected AbstractSentinelPlugin(String name, String contextName, String origin, int resourceType) {
        this(name, contextName, origin, resourceType, IN);
    }

    protected AbstractSentinelPlugin(String name, String contextName, String origin, int resourceType, EntryType trafficType) {
        this(name, contextName, origin, resourceType, trafficType, true);
    }

    protected AbstractSentinelPlugin(String name, String contextName, String origin, int resourceType, EntryType trafficType, boolean autoRegisterMBean) {
        this.name = name;
        this.contextName = contextName;
        this.origin = origin;
        this.resourceType = resourceType;
        this.trafficType = trafficType;
        this.autoRegisterMBean = autoRegisterMBean;
        this.enabled = SentinelPlugin.super.isEnabled();
        if (autoRegisterMBean) {
            registerMBean();
            addShutdownHookCallback(this::unregisterMBean);
        }
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

    /**
     * Is this {@link SentinelPlugin} enabled ?
     *
     * @return If enabled, return <code>true</code>
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Set whether this {@link SentinelPlugin} enabled or not
     *
     * @param enabled If enabled, set <code>true</code>
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the name of this {@link SentinelPlugin}
     *
     * @return the name of this {@link SentinelPlugin}
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the context name of this {@link SentinelPlugin}
     *
     * @return the context name of this {@link SentinelPlugin}
     */
    @Override
    public String getContextName() {
        return this.contextName;
    }

    /**
     * Get the origin of this {@link SentinelPlugin}
     *
     * @return the origin of this {@link SentinelPlugin}
     */
    @Override
    public String getOrigin() {
        return this.origin;
    }

    /**
     * Get the resource type of this {@link SentinelPlugin}
     *
     * @return the resource type of this {@link SentinelPlugin}
     */
    public int getResourceType() {
        return this.resourceType;
    }

    /**
     * Get the traffic type of this {@link SentinelPlugin}
     *
     * @return the traffic type of this {@link SentinelPlugin}
     */
    public EntryType getTrafficType() {
        return this.trafficType;
    }

    /**
     * Is auto register MBean ?
     *
     * @return If auto register MBean, return <code>true</code>
     */
    public boolean isAutoRegisterMBean() {
        return this.autoRegisterMBean;
    }

    /**
     * Is registered MBean ?
     *
     * @return If registered MBean, return <code>true</code>
     */
    public boolean isRegisteredMBean() {
        return doInMBeanServer((mBeanServer, objectName) -> mBeanServer.isRegistered(objectName));
    }

    /**
     * Register MBean for this {@link SentinelPlugin}
     *
     * @return If registered MBean, return <code>true</code>
     */
    public boolean registerMBean() {
        return doInMBeanServer((mBeanServer, objectName) -> {
            if (mBeanServer.isRegistered(objectName)) {
                return false;
            }
            StandardMBean mBean = new StandardMBean(this, SentinelPlugin.class);
            mBeanServer.registerMBean(mBean, objectName);
            return true;
        });
    }

    /**
     * Unregister MBean for this {@link SentinelPlugin}
     *
     * @return If unregistered MBean, return <code>true</code>
     */
    public boolean unregisterMBean() {
        return doInMBeanServer((mBeanServer, objectName) -> {
            if (mBeanServer.isRegistered(objectName)) {
                mBeanServer.unregisterMBean(objectName);
                return true;
            }
            return false;
        });
    }

    protected ObjectName newObjectName() throws Exception {
        String name = format("io.microsphere.sentinel:type=SentinelPlugin,name={},contextName={},origin={}"
                , getName(), getContextName(), getOrigin());
        return new ObjectName(name);
    }

    protected <R> R doInMBeanServer(ThrowableBiFunction<MBeanServer, ObjectName, R> consumer) {
        MBeanServer mBeanServer = getPlatformMBeanServer();
        return execute(() -> {
            ObjectName objectName = newObjectName();
            return consumer.apply(mBeanServer, objectName);
        });
    }

}