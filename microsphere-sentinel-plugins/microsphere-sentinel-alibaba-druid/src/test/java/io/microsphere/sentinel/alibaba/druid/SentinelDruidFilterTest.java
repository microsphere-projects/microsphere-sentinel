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

package io.microsphere.sentinel.alibaba.druid;


import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import io.microsphere.alibaba.druid.test.AbstractAlibabaDruidTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.microsphere.sentinel.alibaba.druid.SentinelDruidFilter.DEFAULT_CONTEXT_NAME;
import static io.microsphere.sentinel.alibaba.druid.SentinelDruidFilter.DEFAULT_ORIGIN;
import static io.microsphere.sentinel.alibaba.druid.SentinelDruidFilter.ENABLED_PROPERTY_NAME;
import static io.microsphere.sentinel.alibaba.druid.SentinelDruidFilter.PLUGIN_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link SentinelDruidFilter} Testt
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SentinelDruidFilter
 * @see AbstractAlibabaDruidTest
 * @since 1.0.0
 */
class SentinelDruidFilterTest extends AbstractAlibabaDruidTest {

    @Test
    void testConstants() {
        assertEquals("alibaba-druid", PLUGIN_NAME);
        assertEquals("microsphere_sentinel_alibaba_druid_context", DEFAULT_CONTEXT_NAME);
        assertEquals("Filter", DEFAULT_ORIGIN);
        assertEquals("microsphere.sentinel.alibaba-druid.enabled", ENABLED_PROPERTY_NAME);
    }

    @Test
    void testEnable() throws Throwable {
        setEnable(true);
        super.test();
    }

    @Test
    void testDisable() throws Throwable {
        setEnable(false);
        super.test();
    }

    void setEnable(boolean enabled) {
        DruidDataSource dataSource = getDruidDataSource();
        List<Filter> proxyFilters = dataSource.getProxyFilters();
        for (Filter proxyFilter : proxyFilters) {
            if (proxyFilter instanceof SentinelDruidFilter filter) {
                assertEquals(PLUGIN_NAME, filter.getName());
                filter.setEnabled(enabled);
            }
        }
    }
}