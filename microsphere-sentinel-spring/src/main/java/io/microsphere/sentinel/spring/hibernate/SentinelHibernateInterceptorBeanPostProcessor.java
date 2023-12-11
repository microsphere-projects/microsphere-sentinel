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
package io.microsphere.sentinel.spring.hibernate;

import io.microsphere.reflect.FieldUtils;
import io.microsphere.sentinel.hibernate.SentinelHibernateInterceptor;
import io.microsphere.spring.beans.factory.config.GenericBeanPostProcessorAdapter;
import org.hibernate.Interceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

/**
 * {@link LocalSessionFactoryBean} {@link BeanPostProcessor} uses {@link SentinelHibernateInterceptor} wrapping the
 * associated {@link Interceptor}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see LocalSessionFactoryBean
 * @since 1.0.0
 */
public class SentinelHibernateInterceptorBeanPostProcessor extends GenericBeanPostProcessorAdapter<LocalSessionFactoryBean> {

    @Override
    protected void processBeforeInitialization(LocalSessionFactoryBean bean, String beanName) throws BeansException {
        Interceptor entityInterceptor = FieldUtils.getFieldValue(bean, "entityInterceptor");
        if (entityInterceptor instanceof SentinelHibernateInterceptor) {
            return;
        }
        entityInterceptor = new SentinelHibernateInterceptor(entityInterceptor);
        bean.setEntityInterceptor(entityInterceptor);
    }
}
