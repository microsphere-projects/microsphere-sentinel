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

package io.microsphere.sentinel.spring.web;


import io.microsphere.spring.test.web.controller.TestController;
import io.microsphere.spring.webmvc.annotation.EnableWebMvcExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * {@link SentinelHandlerMethodInterceptor} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SentinelHandlerMethodInterceptor
 * @since 1.0.0
 */
@SpringJUnitConfig
@WebAppConfiguration
@ContextConfiguration(classes = {
        TestController.class,           // Test Controller
        SentinelHandlerMethodInterceptor.class,
        SentinelHandlerMethodInterceptorTest.class  // Test RouterFunction
})
@EnableWebMvc
@EnableWebMvcExtension
class SentinelHandlerMethodInterceptorTest {

    @Autowired
    protected ConfigurableWebApplicationContext context;

    @Autowired
    private TestController testController;

    @Autowired
    private SentinelHandlerMethodInterceptor interceptor;

    protected MockMvc mockMvc;

    @BeforeEach
    protected void setUp() {
        this.mockMvc = webAppContextSetup(this.context).build();
    }

    @Test
    void test() throws Exception {
        this.testHelloWorld();
        this.testGreeting();
    }

    @Test
    void testDisabled() throws Exception {
        this.interceptor.setEnabled(false);
        assertFalse(this.interceptor.isEnabled());
        test();
        this.interceptor.setEnabled(true);
        assertTrue(this.interceptor.isEnabled());
    }

    /**
     * Test {@link TestController#helloWorld()}
     *
     * @throws Exception If failed to execute {@link MockMvc#perform(RequestBuilder)}
     */
    protected void testHelloWorld() throws Exception {
        this.mockMvc.perform(get("/test/helloworld"))
                .andExpect(status().isOk())
                .andExpect(content().string(this.testController.helloWorld()));
    }

    /**
     * Test {@link TestController#helloWorld()}
     *
     * @throws Exception If failed to execute {@link MockMvc#perform(RequestBuilder)}
     */
    protected void testGreeting() throws Exception {
        String pattern = "/test/greeting/{message}";
        String message = "Mercy";
        this.mockMvc.perform(get(pattern, message))
                .andExpect(status().isOk())
                .andExpect(content().string(this.testController.greeting(message)));
    }
}