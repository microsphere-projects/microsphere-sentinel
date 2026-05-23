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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import static com.alibaba.csp.sentinel.EntryType.IN;
import static com.alibaba.csp.sentinel.EntryType.OUT;
import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON;
import static io.microsphere.sentinel.common.constants.SentinelConstants.DEFAULT_CONTEXT_NAME;
import static io.microsphere.sentinel.common.constants.SentinelConstants.DEFAULT_ORIGIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SimpleSentinelPlugin}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SimpleSentinelPlugin
 * @since 1.0.0
 */
class SimpleSentinelPluginTest {

    private SimpleSentinelPlugin plugin;

    @BeforeEach
    void setUp(RepetitionInfo repetitionInfo) {
        int currentRepetition = repetitionInfo.getCurrentRepetition();
        String value = value(currentRepetition);
        switch (currentRepetition) {
            case 1 -> this.plugin = new SimpleSentinelPlugin(value);
            case 2 -> this.plugin = new SimpleSentinelPlugin(value, value, value(currentRepetition));
            case 3 -> this.plugin = new SimpleSentinelPlugin(value, value, value, currentRepetition);
            case 4 -> this.plugin = new SimpleSentinelPlugin(value, value, value, currentRepetition, OUT);
        }
    }

    @RepeatedTest(value = 4, name = "Test SimpleSentinelPlugin with {currentRepetition} constructor argument(s)")
    void test(RepetitionInfo repetitionInfo) {
        String value = value(repetitionInfo.getCurrentRepetition());
        int currentRepetition = repetitionInfo.getCurrentRepetition();
        assertTrue(plugin.isEnabled());
        assertEquals(value, plugin.getName());

        switch (currentRepetition) {
            case 1 -> {
                assertEquals(DEFAULT_CONTEXT_NAME, plugin.getContextName());
                assertEquals(DEFAULT_ORIGIN, plugin.getOrigin());
                assertEquals(COMMON, plugin.getResourceType());
                assertEquals(IN, plugin.getTrafficType());
            }
            case 2 -> {
                assertEquals(value, plugin.getContextName());
                assertEquals(value, plugin.getOrigin());
                assertEquals(COMMON, plugin.getResourceType());
                assertEquals(IN, plugin.getTrafficType());
            }
            case 3 -> {
                assertEquals(value, plugin.getContextName());
                assertEquals(value, plugin.getOrigin());
                assertEquals(currentRepetition, plugin.getResourceType());
                assertEquals(IN, plugin.getTrafficType());
            }
            case 4 -> {
                assertEquals(value, plugin.getContextName());
                assertEquals(value, plugin.getOrigin());
                assertEquals(currentRepetition, plugin.getResourceType());
                assertEquals(OUT, plugin.getTrafficType());
            }
        }
    }

    String value(int currentRepetition) {
        return "value - " + currentRepetition;
    }
}