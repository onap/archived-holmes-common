/**
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.holmes.common.utils;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class DbDaoUtilTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DBI jdbi;

    private Environment environmentProvider;

    private DataSourceFactory dataSourceFactoryProvider;

    private DbDaoUtil dbDaoUtil;

    private DBIFactory factory;

    @Before
    public void before() throws Exception {
        dbDaoUtil = new DbDaoUtil();

        jdbi = PowerMock.createMock(DBI.class);
        environmentProvider = PowerMock.createMock(Environment.class);
        dataSourceFactoryProvider = PowerMock.createMock(DataSourceFactory.class);
        factory = PowerMock.createMock(DBIFactory.class);

        Whitebox.setInternalState(dbDaoUtil, "environmentProvider", environmentProvider);
        Whitebox.setInternalState(dbDaoUtil, "dataSourceFactoryProvider",
                dataSourceFactoryProvider);
        Whitebox.setInternalState(dbDaoUtil, "factory", factory);

        PowerMock.resetAll();
    }

    @Test
    public void init() throws Exception {
        DBI jdbi = PowerMock.createMock(DBI.class);

        expect(factory.build(anyObject(Environment.class), anyObject(DataSourceFactory.class),
                anyObject(String.class))).andReturn(jdbi);

        PowerMock.replayAll();

        dbDaoUtil.init();

        PowerMock.verifyAll();
    }

    @Test
    public void getDao_normal() throws Exception {
        Whitebox.setInternalState(dbDaoUtil, "jdbi", jdbi);
        expect(jdbi.open(anyObject(Class.class))).andReturn(Class.class);

        PowerMock.replayAll();

        dbDaoUtil.getDao(String.class);

        PowerMock.verifyAll();
    }

    @Test
    public void getDao_exception() throws Exception {
        Whitebox.setInternalState(dbDaoUtil, "jdbi", jdbi);

        expect(jdbi.open(anyObject(Class.class))).andThrow(new RuntimeException(""));

        PowerMock.replayAll();

        Object o = dbDaoUtil.getDao(String.class);

        PowerMock.verifyAll();

        assertThat(o, equalTo(null));
    }

    @Test
    public void getHandle_normal() throws Exception {
        Handle handle = PowerMock.createMock(Handle.class);

        Whitebox.setInternalState(dbDaoUtil, "jdbi", jdbi);
        expect(jdbi.open()).andReturn(handle);

        PowerMock.replayAll();

        dbDaoUtil.getHandle();

        PowerMock.verifyAll();
    }
    @Test
    public void getHandle_exception() throws Exception {
        Handle handle = PowerMock.createMock(Handle.class);

        Whitebox.setInternalState(dbDaoUtil, "jdbi", jdbi);
        expect(jdbi.open()).andThrow(new RuntimeException(""));

        PowerMock.replayAll();

        Handle handle1 = dbDaoUtil.getHandle();

        PowerMock.verifyAll();

        assertThat(handle1, equalTo(null));
    }

    @Test
    public void close_normal() throws Exception {
        Whitebox.setInternalState(dbDaoUtil, "jdbi", jdbi);
        jdbi.close(anyObject());

        PowerMock.replayAll();

        dbDaoUtil.close(new Object());

        PowerMock.verifyAll();
    }

    @Test
    public void close_exception() throws Exception {
        Whitebox.setInternalState(dbDaoUtil, "jdbi", jdbi);
        jdbi.close(anyObject());
        EasyMock.expectLastCall().andThrow(new RuntimeException(""));
        PowerMock.replayAll();

        dbDaoUtil.close(new Object());

        PowerMock.verifyAll();
    }
    @Test
    public void testGetJdbiDaoByOnDemand() throws Exception {
        Whitebox.setInternalState(dbDaoUtil, "jdbi", jdbi);
        expect(jdbi.onDemand(anyObject(Class.class))).andReturn(Class.class);

        PowerMock.replayAll();

        dbDaoUtil.getJdbiDaoByOnDemand(String.class);

        PowerMock.verifyAll();
    }

    @Test
    public void testGetJdbiDaoByOpen() throws Exception {
        Whitebox.setInternalState(dbDaoUtil, "jdbi", jdbi);
        expect(jdbi.open(anyObject(Class.class))).andReturn(Class.class);

        PowerMock.replayAll();

        dbDaoUtil.getJdbiDaoByOpen(String.class);

        PowerMock.verifyAll();
    }
}
