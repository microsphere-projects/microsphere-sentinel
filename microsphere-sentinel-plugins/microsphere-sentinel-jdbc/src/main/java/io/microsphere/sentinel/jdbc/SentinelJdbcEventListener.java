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
package io.microsphere.sentinel.jdbc;

import com.p6spy.engine.common.PreparedStatementInformation;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.event.SimpleJdbcEventListener;
import io.microsphere.logging.Logger;
import io.microsphere.sentinel.common.SentinelContext;
import io.microsphere.sentinel.common.SentinelOperations;
import io.microsphere.sentinel.common.SentinelTemplate;

import java.sql.SQLException;

import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_DB_SQL;
import static io.microsphere.lang.function.ThrowableAction.execute;
import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.sentinel.common.SentinelContext.doInContext;

/**
 * {@link JdbcEventListener} based on Alibaba Sentinel
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SentinelJdbcEventListener extends SimpleJdbcEventListener {

    public static final String DEFAULT_CONTEXT_NAME = "microsphere_sentinel_jdbc_context";

    public static final String DEFAULT_ORIGIN = "Statement";

    private static final Logger logger = getLogger(SentinelJdbcEventListener.class);

    private final String contextName;

    private final String origin;

    private final SentinelOperations sentinelOperations;

    public SentinelJdbcEventListener() {
        this(DEFAULT_CONTEXT_NAME, DEFAULT_ORIGIN);
    }

    public SentinelJdbcEventListener(String contextName, String origin) {
        this.contextName = contextName;
        this.origin = origin;
        this.sentinelOperations = new SentinelTemplate(COMMON_DB_SQL);
    }

    @Override
    public void onBeforeAnyExecute(StatementInformation statementInformation) {
        if (isEligibleStatement(statementInformation)) {
            execute(() -> {
                String resourceName = getResourceName(statementInformation);
                SentinelContext context = this.sentinelOperations.begin(resourceName, this.contextName, this.origin);
                context.setContext();
            });
        }
    }

    @Override
    public void onAfterAnyExecute(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
        if (isEligibleStatement(statementInformation)) {
            doInContext(context -> {
                context.setFailure(e);
                this.sentinelOperations.end(context);
            }, true);
        }
    }

    protected String getResourceName(StatementInformation statementInformation) {
        String resourceName = statementInformation.getSql();
        logger.trace("Sentinel JDBC StatementInformation resource[name : '{}']", resourceName);
        return resourceName;
    }

    /**
     * Determine whether the specified {@link StatementInformation} is eligible
     *
     * @param statementInformation
     * @return
     */
    protected boolean isEligibleStatement(StatementInformation statementInformation) {
        return statementInformation instanceof PreparedStatementInformation;
    }
}