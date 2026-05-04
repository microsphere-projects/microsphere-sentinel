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

package io.microsphere.sentinel.common;


import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import io.microsphere.logging.test.jupiter.LoggingLevelsClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.microsphere.sentinel.common.SentinelOperations.DEFAULT_CONTEXT_NAME;
import static io.microsphere.sentinel.common.SentinelOperations.DEFAULT_ORIGIN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link SentinelTemplate} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see SentinelTemplate
 * @since 1.0.0
 */
@LoggingLevelsClass(levels = {"TRACE", "INFO", "OFF"})
class SentinelTemplateTest {

    private String resourceName = "test-resource";

    private String contextName = "test-context";

    private String origin = "test-origin";

    private SentinelTemplate sentinelTemplate;

    @BeforeEach
    void setUp() {
        this.sentinelTemplate = new SentinelTemplate();
    }

    @Test
    void testExecuteWithRunnable() {
        assertDoesNotThrow(() -> this.sentinelTemplate.execute(this.resourceName, () -> {
        }));

        assertDoesNotThrow(() -> this.sentinelTemplate.execute(this.resourceName, this.contextName, this.origin, () -> {
        }));
    }

    @Test
    void testExecuteWithRunnableOnRuntimeException() {
        assertThrows(RuntimeException.class, () -> this.sentinelTemplate.execute(this.resourceName, () -> {
            throw new RuntimeException("For testing...");
        }));

        assertThrows(RuntimeException.class, () -> this.sentinelTemplate.execute(this.resourceName, this.contextName, this.origin, () -> {
            throw new RuntimeException("For testing...");
        }));
    }

    @Test
    void testExecuteWithConsumer() {
        assertDoesNotThrow(() -> this.sentinelTemplate.execute(this.resourceName, context -> {
            assertSentinelContext(this.resourceName, DEFAULT_CONTEXT_NAME, DEFAULT_ORIGIN, context);
        }));

        assertDoesNotThrow(() -> this.sentinelTemplate.execute(this.resourceName, this.contextName, this.origin, context -> {
            assertSentinelContext(this.resourceName, this.contextName, this.origin, context);
        }));
    }

    @Test
    void testExecuteWithFunction() {
        assertEquals(this.resourceName, this.sentinelTemplate.execute(this.resourceName, context -> {
            assertSentinelContext(this.resourceName, DEFAULT_CONTEXT_NAME, DEFAULT_ORIGIN, context);
            return this.resourceName;
        }));

        assertEquals(this.contextName, this.sentinelTemplate.execute(this.resourceName, this.contextName, this.origin, context -> {
            assertSentinelContext(this.resourceName, this.contextName, this.origin, context);
            return this.contextName;
        }));
    }

    @Test
    void testCallWithThrowableAction() {
        assertDoesNotThrow(() -> this.sentinelTemplate.call(this.resourceName, () -> {
        }));

        assertDoesNotThrow(() -> this.sentinelTemplate.call(this.resourceName, this.contextName, this.origin, () -> {
        }));
    }

    @Test
    void testCallWithThrowableActionOnException() {
        assertThrows(RuntimeException.class, () -> this.sentinelTemplate.call(this.resourceName, () -> {
            throw new RuntimeException("For testing...");
        }));

        assertThrows(BlockException.class, () -> this.sentinelTemplate.call(this.resourceName, this.contextName, this.origin, () -> {
            throw new FlowException("For testing...");
        }));
    }

    @Test
    void testExecuteWithThrowableConsumer() {
        assertDoesNotThrow(() -> this.sentinelTemplate.call(this.resourceName, context -> {
            assertSentinelContext(this.resourceName, DEFAULT_CONTEXT_NAME, DEFAULT_ORIGIN, context);
        }));

        assertDoesNotThrow(() -> this.sentinelTemplate.call(this.resourceName, this.contextName, this.origin, context -> {
            assertSentinelContext(this.resourceName, this.contextName, this.origin, context);
        }));
    }

    @Test
    void testExecuteWithThrowableFunction() throws Throwable {
        assertEquals(this.resourceName, this.sentinelTemplate.call(this.resourceName, context -> {
            assertSentinelContext(this.resourceName, DEFAULT_CONTEXT_NAME, DEFAULT_ORIGIN, context);
            return this.resourceName;
        }));

        assertEquals(this.contextName, this.sentinelTemplate.call(this.resourceName, this.contextName, this.origin, context -> {
            assertSentinelContext(this.resourceName, this.contextName, this.origin, context);
            return this.contextName;
        }));
    }

    @Test
    void testBeginAndEnd() throws Throwable {
        SentinelContext context = this.sentinelTemplate.begin(this.resourceName, this.contextName, this.origin);
        context.setFailure(new RuntimeException("For testing..."));
        this.sentinelTemplate.end(context);
    }

    void assertSentinelContext(String resourceName, String contextName, String origin, SentinelContext context) {
        assertEquals(resourceName, context.getResourceName());
        assertEquals(contextName, context.getContextName());
        assertEquals(origin, context.getOrigin());
        assertNull(context.getResult());
        assertNull(context.getFailure());
        assertTrue(context.getAttributes().isEmpty());
    }
}