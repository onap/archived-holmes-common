/**
 * Copyright 2017-2020 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.holmes.common.utils;

import org.jdbi.v3.core.Jdbi;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.database.DbDaoUtil;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

@RunWith(PowerMockRunner.class)
public class DbDaoUtilTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void testGetJdbiDaoByOnDemand() {
            Jdbi jdbi = PowerMock.createMock(Jdbi.class);
            DbDaoUtil dbDaoUtil = new DbDaoUtil(jdbi);
        expect(jdbi.onDemand(anyObject(Class.class))).andReturn(Class.class);

        PowerMock.replayAll();

        dbDaoUtil.getJdbiDaoByOnDemand(String.class);

        PowerMock.verifyAll();
    }

}
