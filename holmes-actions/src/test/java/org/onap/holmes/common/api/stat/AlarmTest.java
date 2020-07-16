/**
 * Copyright 2017-2020 ZTE Corporation.
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

package org.onap.holmes.common.api.stat;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class AlarmTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Alarm alarm;

    @Before
    public void before() {
        alarm = new Alarm();
    }

    @After
    public void after() {
    }

    @Test
    public void testContainNode_NoContainLink() {
        alarm.addLinkIdNodeIdx(1, 2);
        assertThat(false, equalTo(alarm.containNode(2, 2)));
    }

    @Test
    public void testContainNode_ContainLinkNoIdx() {
        alarm.addLinkIdNodeIdx(1, 2);
        assertFalse(alarm.containNode(1, 3));
    }

    @Test
    public void testContainNode_ContainLinkAndIdx() {
        alarm.addLinkIdNodeIdx(1, 2);
        assertTrue(alarm.containNode(1, 2));
    }

    @Test
    public void testGetDataType() {
        assertThat(Alarm.APLUS_EVENT, equalTo(alarm.getDataType()));
    }

    @Test
    public void testToString() {
        Alarm alarmTempA = new Alarm();
        Alarm alarmTempB = new Alarm();
        Date date = new Date();
        alarmTempA.setClearedTime(date);
        alarmTempA.setRaisedTime(date);
        alarmTempA.setRaisedServerTime(date);
        alarmTempB.setClearedTime(date);
        alarmTempB.setRaisedTime(date);
        alarmTempB.setRaisedServerTime(date);
        assertThat(alarmTempA.toString(),equalTo(alarmTempB.toString()));
    }

    @Test
    public void testHashCode() {
        final Alarm alarmTemp = new Alarm();
        final String alarmKey = "alarmKey";
        alarm.setAlarmKey(alarmKey);
        alarmTemp.setAlarmKey(alarmKey);
        assertThat(alarm.hashCode(), equalTo(alarmTemp.hashCode()));
    }

    @Test
    public void testEqualsAnd_NotNull() {
        final Alarm alarmTemp = new Alarm();
        final String alarmKey = "alarmKey";
        alarm.setAlarmKey(alarmKey);
        alarmTemp.setAlarmKey(alarmKey);
        assertTrue(alarm.equals(alarmTemp));
    }

    @Test
    public void testEqualsAndH_isNull() {
        assertFalse(alarm.equals(null));
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        alarm.setAlarmKey("alarmKey");
        Alarm alarmTemp = (Alarm) alarm.clone();
        assertTrue(alarm.equals(alarmTemp));
        assertFalse(alarm == alarmTemp);
    }

    @Test
    public void testGetObjectId() {
        alarm.setId(11);
        assertThat("11", equalTo(alarm.getObjectId()));
    }

    @Test
    public void testAddLinkIds() {
        final int linkId = 11;
        alarm.addLinkIds(linkId);
        assertTrue(alarm.getLinkIds().contains(linkId));
    }

    @Test
    public void testContainsPriority_true() {
        String ruleId = "ruleId";
        alarm.getPriorityMap().put(ruleId, 2);
        assertTrue(alarm.containsPriority(ruleId));
    }

    @Test
    public void testContainsPriority_false() {
        final String ruleId = "ruleId";
        assertFalse(alarm.containsPriority(ruleId));
    }

    @Test
    public void testGetPriority_isNull() {
        final String ruleId = "ruleId";
        alarm.getPriorityMap().put(ruleId, null);
        assertThat(0, equalTo(alarm.getPriority(ruleId)));
    }

    @Test
    public void testGetPriority_notNull() {
        final String ruleId = "ruleId";
        final int priority = 2;
        alarm.getPriorityMap().put(ruleId, priority);
        assertThat(priority, equalTo(alarm.getPriority(ruleId)));
    }

    @Test
    public void testGetAlarmTypeRuleId_isNull() {
        final String ruleId = "ruleId";
        alarm.getRootAlarmTypeMap().put(ruleId, null);
        assertThat(-1, equalTo(alarm.getRootAlarmType(ruleId)));
    }

    @Test
    public void testGetAlarmTypeRuleId_notNull() {
        final String ruleId = "ruleId";
        final int rootAlarmType = 2;
        alarm.getRootAlarmTypeMap().put(ruleId, rootAlarmType);
        assertThat(rootAlarmType, equalTo(alarm.getRootAlarmType(ruleId)));
    }

    @Test
    public void getterAndSetter4CenterType() {
        final int centerType = 1;
        alarm.setCenterType(centerType);
        assertThat(centerType, equalTo(alarm.getCenterType()));
    }
}
