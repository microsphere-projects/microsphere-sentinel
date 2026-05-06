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

package io.microsphere.sentinel.hibernate;

import io.microsphere.sentinel.hibernate.test.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.microsphere.sentinel.hibernate.Constants.PLUGIN_NAME;
import static io.microsphere.sentinel.util.SentinelUtils.getPluginEnabledPropertyName;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.setProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Abstract Sentinel x Hibernate Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
abstract class AbstractSentinelHibernateTest {

    protected Configuration configuration;

    protected SessionFactory sessionFactory;

    protected Session session;

    protected StatelessSession statelessSession;

    protected User user;

    @BeforeEach
    void setUp() {
        setEnabled(false);
        this.configuration = new Configuration()
                .addAnnotatedClass(User.class)
                .setProperty("jakarta.persistence.jdbc.driver", "org.h2.Driver")
                .setProperty("jakarta.persistence.jdbc.url", "jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1")
                .setProperty("jakarta.persistence.jdbc.user", "sa")
                .setProperty("jakarta.persistence.jdbc.password", "")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.format_sql", "true")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "update");

        customizeConfiguration(configuration);

        this.sessionFactory = configuration.buildSessionFactory();
        this.session = sessionFactory.openSession();
        this.statelessSession = sessionFactory.openStatelessSession();
        this.user = new User();
        this.user.setId(currentTimeMillis());
        this.user.setName("Mercy");
    }

    @AfterEach
    void tearDown() {
        this.session.close();
        this.statelessSession.close();
        this.sessionFactory.close();
        setEnabled(true);
    }

    @Test
    @DisplayName("Test Session: save & update & flush & get & delete")
    void testSession1() {
        long userId = this.user.getId();
        assertEquals(userId, this.session.save(this.user));

        this.user.setName("Ma");
        this.session.update(this.user);

        // flush
        Transaction transaction = this.session.beginTransaction();
        this.session.flush();
        transaction.commit();

        this.user = this.session.get(User.class, userId);
        assertNotNull(this.user);

        this.session.delete(this.user);
    }

    @Test
    @DisplayName("Test : persist & saveOrUpdate & load & remove")
    void testSession2() {
        long userId = this.user.getId();
        this.session.persist(this.user);

        this.user.setId(currentTimeMillis());
        this.user.setName("Ma");
        this.session.saveOrUpdate(this.user);

        this.user = this.session.load(User.class, userId);
        assertNotNull(this.user);

        this.session.remove(this.user);
    }

    @Test
    @DisplayName("Test : persist & find & merge & remove")
    void testSession3() {
        long userId = this.user.getId();
        this.session.persist(this.user);

        this.user = this.session.find(User.class, userId);
        assertNotNull(this.user);

        this.user.setId(currentTimeMillis());
        this.user.setName("Ma");
        this.session.merge(this.user);

        this.session.remove(this.user);
    }

    @Test
    @DisplayName("Test Stateless Session: insert & update & get & delete")
    void testStatelessSession1() {
        long userId = this.user.getId();

        // insert
        assertEquals(userId, this.statelessSession.insert(this.user));

        this.user.setName("Ma");
        // update
        this.statelessSession.update(this.user);

        // upsert
        this.statelessSession.upsert(this.user);

        // get
        assertEquals(this.user, this.statelessSession.get(User.class, userId));

        // delete
        this.statelessSession.delete(this.user);
    }

    protected void customizeConfiguration(Configuration configuration) {
    }

    protected void setEnabled(boolean enabled) {
        String propertyName = getPluginEnabledPropertyName(PLUGIN_NAME);
        setProperty(propertyName, Boolean.toString(enabled));
    }
}