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

package io.microsphere.alibaba.sentinel.common.constants;

import io.microsphere.alibaba.sentinel.common.SentinelContext;

/**
 * The constants of Sentinel
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public interface SentinelConstants {

    /***
     * The Flow Data ID pattern
     */
    String FLOW_DATA_ID_PATTERN = "{}-flow-rules";

    /**
     * The default Context name pattern
     */
    String DEFAULT_CONTEXT_NAME_PATTERN = "microsphere_sentinel_{}_context";

    /**
     * The default Context name : "microsphere_sentinel_default_context"
     */
    String DEFAULT_CONTEXT_NAME = "microsphere_sentinel_default_context";

    /**
     * The default origin : ""
     */
    String DEFAULT_ORIGIN = "";

    /**
     * The Property Name Prefix of Sentinel : "microsphere.sentinel."
     */
    String PROPERTY_NAME_PREFIX = "microsphere.sentinel.";

    /**
     * The attribute name of {@link SentinelContext}
     *
     * @see SentinelContext
     */
    String SENTINEL_CONTEXT_ATTRIBUTE_NAME = "sentinel-context";

    /**
     * The default priority for Sentinel Plugins
     */
    int DEFAULT_PRIORITY = 9;
}