/*
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

package org.onap.holmes.common.api.stat;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.Test;

public class VesAlarmTest {

    @Test
    public void hashCodeTest() throws Exception {
        VesAlarm alarm = new VesAlarm();
        alarm.setEventId("unique");
        VesAlarm alarmClone = (VesAlarm)alarm.clone();
        assertTrue(alarm.hashCode() == alarmClone.hashCode());
    }

    @Test
    public void equalsTest() throws Exception {
        VesAlarm alarm = new VesAlarm();
        alarm.setEventId("unique");
        VesAlarm alarmClone = (VesAlarm)alarm.clone();
        assertTrue(alarm.equals(alarmClone));
    }

    @Test
    public void cloneTest() throws Exception {
        VesAlarm alarm = new VesAlarm();
        alarm.setDomain("Test");
        alarm.setEventId("unique");
        assertThat(alarm, equalTo(alarm.clone()));
    }

    @Test
    public void getterAndSetterTest() {
        VesAlarm alarm = new VesAlarm();
        alarm.setDomain("");
        alarm.setEventId("");
        alarm.setEventName("");
        alarm.setEventType("");
        alarm.setInternalHeaderFields(new Object());
        alarm.setLastEpochMicrosec(0L);
        alarm.setNfcNamingCode("");
        alarm.setNfNamingCode("");
        alarm.setPriority("");
        alarm.setReportingEntityId("");
        alarm.setReportingEntityName("");
        alarm.setSequence(1);
        alarm.setSourceId("");
        alarm.setSourceName("");
        alarm.setStartEpochMicrosec(0L);
        alarm.setVersion(0L);
        alarm.setAlarmAdditionalInformation(new ArrayList<>());
        alarm.setAlarmCondition("");
        alarm.setAlarmInterfaceA("");
        alarm.setEventCategory("");
        alarm.setEventSeverity("");
        alarm.setEventSourceType("");
        alarm.setFaultFieldsVersion(0L);
        alarm.setSpecificProblem("");
        alarm.setVfStatus("");

        alarm.getDomain();
        alarm.getEventId();
        alarm.getEventName();
        alarm.getEventType();
        alarm.getInternalHeaderFields();
        alarm.getLastEpochMicrosec();
        alarm.getNfcNamingCode();
        alarm.getNfNamingCode();
        alarm.getPriority();
        alarm.getReportingEntityId();
        alarm.getReportingEntityName();
        alarm.getSequence();
        alarm.getSourceId();
        alarm.getSourceName();
        alarm.getStartEpochMicrosec();
        alarm.getVersion();
        alarm.getAlarmAdditionalInformation();
        alarm.getAlarmCondition();
        alarm.getAlarmInterfaceA();
        alarm.getEventCategory();
        alarm.getEventSeverity();
        alarm.getEventSourceType();
        alarm.getFaultFieldsVersion();
        alarm.getSpecificProblem();
        alarm.getVfStatus();
    }

}