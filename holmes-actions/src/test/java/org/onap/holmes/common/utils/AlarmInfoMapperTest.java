/**
 * Copyright 2017 ZTE Corporation.
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

package org.onap.holmes.common.utils;

import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.sql.ResultSet;

import static org.easymock.EasyMock.expect;

public class AlarmInfoMapperTest {

    @Test
    public void map() throws Exception {
        AlarmInfoMapper mapper = new AlarmInfoMapper();
        ResultSet resultSet = PowerMock.createMock(ResultSet.class);
        expect(resultSet.getString("eventid")).andReturn("");
        expect(resultSet.getString("eventname")).andReturn("");
        expect(resultSet.getString("sourceid")).andReturn("");
        expect(resultSet.getString("sourcename")).andReturn("");
	expect(resultSet.getInt("sequence")).andReturn(0);
        expect(resultSet.getLong("startepochmicrosec")).andReturn(0L);
        expect(resultSet.getLong("lastepochmicrosec")).andReturn(0L);
        expect(resultSet.getInt("alarmiscleared")).andReturn(0);
        expect(resultSet.getInt("rootflag")).andReturn(0);
        PowerMock.replay(resultSet);
        mapper.map(0, resultSet, null);
        PowerMock.verify(resultSet);
    }
}
