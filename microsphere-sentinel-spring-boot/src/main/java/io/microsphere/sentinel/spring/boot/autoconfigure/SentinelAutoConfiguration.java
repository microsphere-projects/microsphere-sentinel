package io.microsphere.sentinel.spring.boot.autoconfigure;

import io.microsphere.sentinel.mybatis.SentinelMyBatisInterceptor;
import io.microsphere.sentinel.spring.boot.condition.ConditionalOnSentinelEnabled;
import io.microsphere.sentinel.spring.druid.SentinelDruidFilterBeanPostProcessor;
import io.microsphere.sentinel.spring.hibernate.SentinelHibernateInterceptorBeanPostProcessor;
import io.microsphere.sentinel.spring.redis.SentinelRedisCommandInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static io.microsphere.constants.PropertyConstants.ENABLED_PROPERTY_NAME;
import static io.microsphere.sentinel.spring.boot.condition.ConditionalOnSentinelEnabled.PREFIX;

/**
 * Microsphere Sentinel Spring Boot Auto-Configuration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @see com.alibaba.cloud.sentinel.custom.SentinelAutoConfiguration
 * @since 1.0.0
 */
@ConditionalOnSentinelEnabled
@AutoConfigureAfter(name = {
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
        "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration",
        "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration",
})
@Import(value = {
        SentinelAutoConfiguration.RedisConfiguration.class,
        SentinelAutoConfiguration.HibernateConfiguration.class,
        SentinelAutoConfiguration.DruidConfiguration.class,
        SentinelAutoConfiguration.MyBatisConfiguration.class
})
public class SentinelAutoConfiguration {

    @ConditionalOnProperty(
            prefix = PREFIX + "redis",
            name = ENABLED_PROPERTY_NAME,
            matchIfMissing = true
    )
    @ConditionalOnClass(name = {
            "org.springframework.data.redis.connection.RedisConnection",
            "io.microsphere.redis.spring.interceptor.RedisConnectionInterceptor"
    })
    static class RedisConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public SentinelRedisCommandInterceptor sentinelRedisCommandInterceptor() {
            return new SentinelRedisCommandInterceptor();
        }
    }

    @ConditionalOnProperty(
            prefix = PREFIX + "hibernate",
            name = ENABLED_PROPERTY_NAME,
            matchIfMissing = true
    )
    @ConditionalOnClass(name = {
            "org.hibernate.SessionFactory", // Hibernate
            "org.springframework.orm.hibernate5.LocalSessionFactoryBean" // Spring ORM
    })
    static class HibernateConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public BeanPostProcessor sentinelHibernateInterceptorBeanPostProcessor() {
            return new SentinelHibernateInterceptorBeanPostProcessor();
        }
    }

    @ConditionalOnProperty(
            prefix = PREFIX + "druid",
            name = ENABLED_PROPERTY_NAME,
            matchIfMissing = true
    )
    @ConditionalOnClass(name = {
            "com.alibaba.druid.pool.DruidDataSource"
    })
    static class DruidConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public BeanPostProcessor sentinelDruidFilterBeanPostProcessor() {
            return new SentinelDruidFilterBeanPostProcessor();
        }
    }

    @ConditionalOnProperty(
            prefix = PREFIX + "mybatis",
            name = ENABLED_PROPERTY_NAME,
            matchIfMissing = true
    )
    @ConditionalOnClass(name = {
            "org.apache.ibatis.executor.Executor"
    })
    static class MyBatisConfiguration {

        @Autowired
        public void initSentinelMyBatisInterceptor(ObjectProvider<SqlSessionFactory[]> sqlSessionFactoryProvider) {
            SentinelMyBatisInterceptor interceptor = new SentinelMyBatisInterceptor();
            SqlSessionFactory[] sqlSessionFactories = sqlSessionFactoryProvider.getIfAvailable();
            for (SqlSessionFactory sqlSessionFactory : sqlSessionFactories) {
                sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
            }
        }
    }
}