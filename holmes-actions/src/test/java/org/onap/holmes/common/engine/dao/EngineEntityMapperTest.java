/**
 * Copyright 2020 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.holmes.common.engine.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.holmes.common.engine.entity.EngineEntity;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.ResultSet;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ResultSet.class})
public class EngineEntityMapperTest {
    private EngineEntityMapper mapper = new EngineEntityMapper();
    @Test
    public void map() throws Exception {
        long lastModified = System.currentTimeMillis();
        ResultSet rsMock = PowerMock.createMock(ResultSet.class);
        expect(rsMock.getString("ip")).andReturn("127.0.0.1");
        expect(rsMock.getInt("port")).andReturn(80);
        expect(rsMock.getLong("lastmodified")).andReturn(lastModified);

        PowerMock.replay(rsMock);

        EngineEntity entity = mapper.map( rsMock, null);

        PowerMock.verify(rsMock);

        assertThat(entity.getId(), equalTo("127.0.0.1_80"));
        assertThat(entity.getIp(), equalTo("127.0.0.1"));
        assertThat(entity.getPort(), is(80));
        assertThat(entity.getLastModified(), is(lastModified));
    }
}