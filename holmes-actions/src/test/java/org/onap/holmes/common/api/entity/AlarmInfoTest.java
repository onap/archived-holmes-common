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
package org.onap.holmes.common.api.entity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class AlarmInfoTest {

    private AlarmInfo alarmInfo;

    @Before
    public void before() throws Exception {
        alarmInfo = new AlarmInfo();
    }

    @After
    public void after() throws Exception {

    }

    @Test
    public void getterAndSetter4EventID() throws Exception {
        final String eventID = "eventId";
        alarmInfo.setEventId(eventID);
        assertThat(alarmInfo.getEventId(), equalTo(eventID));
    }

    @Test
    public void getterAndSetter4EventName() throws Exception {
        final String eventName = "eventName";
        alarmInfo.setEventName(eventName);
        assertThat(alarmInfo.getEventName(), equalTo(eventName));
    }

    @Test
    public void getterAndSetter4StartEpochMicroSec() throws Exception {
        final long startEpochMicroSec = 1L;
        alarmInfo.setStartEpochMicroSec(startEpochMicroSec);
        assertThat(alarmInfo.getStartEpochMicroSec(), equalTo(startEpochMicroSec));
    }

    @Test
    public void getterAndSetter4LastEpochMicroSec() throws Exception {
        final long lastEpochMicroSec = 1L;
        alarmInfo.setLastEpochMicroSec(lastEpochMicroSec);
        assertThat(alarmInfo.getLastEpochMicroSec(), equalTo(lastEpochMicroSec));
    }

    @Test
    public void getterAndSetter4SourceID() throws Exception {
        final String sourceID = "sourceId";
        alarmInfo.setSourceId(sourceID);
        assertThat(alarmInfo.getSourceId(), equalTo(sourceID));
    }

    @Test
    public void getterAndSetter4SourceName() throws Exception {
        final String sourceName = "sourceName";
        alarmInfo.setSourceName(sourceName);
        assertThat(alarmInfo.getSourceName(), equalTo(sourceName));
    }

    @Test
    public void getterAndSetter4AlarmIsCleared() throws Exception {
        final int alarmIsCleared = 1;
        alarmInfo.setAlarmIsCleared(alarmIsCleared);
        assertThat(alarmInfo.getAlarmIsCleared(), equalTo(alarmIsCleared));
    }

    @Test
    public void getterAndSetter4RootFlag() throws Exception {
        final int rootFlag = 1;
        alarmInfo.setRootFlag(rootFlag);
        assertThat(alarmInfo.getRootFlag(), equalTo(rootFlag));
    }
}
