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
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import io.microsphere.alibaba.druid.filter.AbstractStatementFilter;
import io.microsphere.sentinel.common.SentinelContext;
import io.microsphere.sentinel.common.SentinelOperations;
import io.microsphere.sentinel.common.SentinelTemplate;

import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_DB_SQL;
import static io.microsphere.sentinel.common.SentinelContext.removeContext;

/**
 * Sentinel x Druid {@link Filter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see Filter
 * @see FilterAdapter
 * @since 1.0.0
 */
public class SentinelDruidFilter extends AbstractStatementFilter {

    public static final String DEFAULT_CONTEXT_NAME = "microsphere_sentinel_alibaba_druid_context";

    public static final String DEFAULT_ORIGIN = "Statement";

    private final String contextName;

    private final String origin;

    private final SentinelOperations sentinelOperations;

    public SentinelDruidFilter() {
        this(DEFAULT_CONTEXT_NAME, DEFAULT_ORIGIN);
    }

    public SentinelDruidFilter(String contextName, String origin) {
        this.contextName = contextName;
        this.origin = origin;
        this.sentinelOperations = new SentinelTemplate(COMMON_DB_SQL);
    }

    @Override
    protected void beforeExecute(StatementProxy statement, String resourceName) throws Throwable {
        SentinelContext context = this.sentinelOperations.begin(resourceName, this.contextName, this.origin);
        context.setContext();
    }

    @Override
    protected void afterExecute(StatementProxy statement, String resourceName, Object result, Throwable failure) {
        SentinelContext context = removeContext();
        context.setResult(result);
        context.setFailure(failure);
        this.sentinelOperations.end(context);
    }
}