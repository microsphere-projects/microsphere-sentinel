package io.microsphere.sentinel.spring.boot.autoconfigure;

import com.alibaba.csp.sentinel.SphU;
import io.microsphere.sentinel.mybatis.SentinelMyBatisInterceptor;
import io.microsphere.sentinel.spring.redis.SentinelRedisCommandInterceptor;
import io.microsphere.redis.spring.interceptor.RedisMethodInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static io.microsphere.constants.PropertyConstants.ENABLED_PROPERTY_NAME;
import static io.microsphere.sentinel.spring.boot.autoconfigure.SentinelAutoConfiguration.PROPERTY_NAME_PREFIX;
import static io.microsphere.spring.boot.constants.PropertyConstants.MICROSPHERE_SPRING_BOOT_PROPERTY_NAME_PREFIX;

/**
 * Sentinel Auto-Configuration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy<a/>
 * @since 1.0.0
 */
@ConditionalOnProperty(prefix = PROPERTY_NAME_PREFIX, name = ENABLED_PROPERTY_NAME, matchIfMissing = true)
@ConditionalOnClass({SphU.class})
@Import(value = {
        SentinelAutoConfiguration.MyBatisConfiguration.class,
        SentinelAutoConfiguration.RedisConfiguration.class
})
@AutoConfigureBefore(name = {
        "com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration"
})
@AutoConfigureAfter(value = {
        DataSourceAutoConfiguration.class
})
public class SentinelAutoConfiguration {

    public static final String PROPERTY_NAME_PREFIX = MICROSPHERE_SPRING_BOOT_PROPERTY_NAME_PREFIX + "sentinel";

    @ConditionalOnClass(RedisMethodInterceptor.class)
    static class RedisConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public SentinelRedisCommandInterceptor sentinelRedisCommandInterceptor() {
            return new SentinelRedisCommandInterceptor();
        }
    }

    @ConditionalOnClass(Interceptor.class)
    static class MyBatisConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnBean(SqlSessionFactory.class)
        public SentinelMyBatisInterceptor sentinelInterceptor(SqlSessionFactory sqlSessionFactory) {
            SentinelMyBatisInterceptor interceptor = new SentinelMyBatisInterceptor();
            sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
            return interceptor;
        }
    }
}
