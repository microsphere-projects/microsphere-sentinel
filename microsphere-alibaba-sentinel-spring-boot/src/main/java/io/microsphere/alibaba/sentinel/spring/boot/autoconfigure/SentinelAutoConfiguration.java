package io.microsphere.alibaba.sentinel.spring.boot.autoconfigure;

import io.microsphere.alibaba.sentinel.spring.boot.condition.ConditionalOnSentinelEnabled;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

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
public class SentinelAutoConfiguration {

//    @ConditionalOnProperty(
//            prefix = PREFIX + "redis",
//            name = ENABLED_PROPERTY_NAME,
//            matchIfMissing = true
//    )
//    @ConditionalOnClass(name = {
//            "org.springframework.data.redis.connection.RedisConnection",
//            "io.microsphere.redis.spring.interceptor.RedisConnectionInterceptor"
//    })
//    static class RedisConfiguration {
//
//        @Bean
//        @ConditionalOnMissingBean
//        public SentinelRedisCommandInterceptor sentinelRedisCommandInterceptor() {
//            return new SentinelRedisCommandInterceptor();
//        }
//    }

//    @ConditionalOnProperty(
//            prefix = PREFIX + "hibernate",
//            name = ENABLED_PROPERTY_NAME,
//            matchIfMissing = true
//    )
//    @ConditionalOnClass(name = {
//            "org.hibernate.SessionFactory", // Hibernate
//            "org.springframework.orm.hibernate5.LocalSessionFactoryBean" // Spring ORM
//    })
//    static class HibernateConfiguration {
//
//        @Bean
//        @ConditionalOnMissingBean
//        public BeanPostProcessor sentinelHibernateInterceptorBeanPostProcessor() {
//            return new SentinelHibernateInterceptorBeanPostProcessor();
//        }
//    }
//
//    @ConditionalOnProperty(
//            prefix = PREFIX + "druid",
//            name = ENABLED_PROPERTY_NAME,
//            matchIfMissing = true
//    )
//    @ConditionalOnClass(name = {
//            "com.alibaba.druid.pool.DruidDataSource"
//    })
//    static class DruidConfiguration {
//
//        @Bean
//        @ConditionalOnMissingBean
//        public BeanPostProcessor sentinelDruidFilterBeanPostProcessor() {
//            return new SentinelDruidFilterBeanPostProcessor();
//        }
//    }
//
//    @ConditionalOnProperty(
//            prefix = PREFIX + "mybatis",
//            name = ENABLED_PROPERTY_NAME,
//            matchIfMissing = true
//    )
//    @ConditionalOnClass(name = {
//            "org.apache.ibatis.executor.Executor"
//    })
//    static class MyBatisConfiguration {
//
//        @Autowired
//        public void initSentinelMyBatisInterceptor(ObjectProvider<SqlSessionFactory[]> sqlSessionFactoryProvider) {
//            SentinelMyBatisInterceptor interceptor = new SentinelMyBatisInterceptor();
//            SqlSessionFactory[] sqlSessionFactories = sqlSessionFactoryProvider.getIfAvailable();
//            for (SqlSessionFactory sqlSessionFactory : sqlSessionFactories) {
//                sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
//            }
//        }
//    }
}