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
package io.microsphere.sentinel.spring.druid;

import com.alibaba.druid.pool.DruidDataSource;
import io.microsphere.sentinel.druid.SentinelDruidFilter;
import io.microsphere.spring.beans.factory.config.GenericBeanPostProcessorAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.sql.SQLException;

/**
 * {@link DruidDataSource} {@link BeanPostProcessor} injects {@link SentinelDruidFilter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see DruidDataSource
 * @see BeanPostProcessor
 * @see SentinelDruidFilter
 * @since 1.0.0
 */
public class SentinelDruidFilterBeanPostProcessor extends GenericBeanPostProcessorAdapter<DruidDataSource> {

    @Override
    protected void processBeforeInitialization(DruidDataSource bean, String beanName) throws BeansException {
        try {
            bean.addFilters(SentinelDruidFilter.class.getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
