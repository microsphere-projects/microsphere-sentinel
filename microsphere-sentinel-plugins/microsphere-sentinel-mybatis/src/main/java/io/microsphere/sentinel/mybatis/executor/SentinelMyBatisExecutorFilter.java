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

package io.microsphere.sentinel.mybatis.executor;

import io.microsphere.mybatis.executor.ExecutorFilter;
import io.microsphere.mybatis.executor.ExecutorFilterChain;
import io.microsphere.sentinel.common.SentinelOperations;
import io.microsphere.sentinel.common.SentinelPlugin;
import io.microsphere.sentinel.common.SentinelTemplate;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

import static com.alibaba.csp.sentinel.ResourceTypeConstants.COMMON_DB_SQL;

/**
 * Sentinel x MyBatis {@link ExecutorFilter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ExecutorFilter
 * @since 1.0.0
 */
public class SentinelMyBatisExecutorFilter implements ExecutorFilter, SentinelPlugin {

    public static final String DEFAULT_CONTEXT_NAME = "microsphere_sentinel_mybatis_context";

    public static final String DEFAULT_ORIGIN = "Statement";

    private final String contextName;

    private final String origin;

    private final SentinelOperations sentinelOperations;

    public SentinelMyBatisExecutorFilter() {
        this(DEFAULT_CONTEXT_NAME, DEFAULT_ORIGIN);
    }

    public SentinelMyBatisExecutorFilter(String contextName, String origin) {
        this.contextName = contextName;
        this.origin = origin;
        this.sentinelOperations = new SentinelTemplate(COMMON_DB_SQL);
    }

    @Override
    public int update(MappedStatement ms, Object parameter, ExecutorFilterChain chain) throws SQLException {
        return doInSentinel(ms, () -> ExecutorFilter.super.update(ms, parameter, chain));
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql, ExecutorFilterChain chain) throws SQLException {
        return doInSentinel(ms, () -> ExecutorFilter.super.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql, chain));
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, ExecutorFilterChain chain) throws SQLException {
        return doInSentinel(ms, () -> ExecutorFilter.super.query(ms, parameter, rowBounds, resultHandler, chain));
    }

    protected <T> T doInSentinel(MappedStatement ms, Callable<T> callable) throws SQLException {
        String resourceName = getSentinelResourceName(ms);
        return sentinelOperations.call(resourceName, this.contextName, this.origin,
                context -> callable.call(), SQLException.class);
    }

    protected String getSentinelResourceName(MappedStatement ms) {
        return ms.getId();
    }
}