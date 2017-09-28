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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VesAlarm implements Cloneable, Serializable{
    private String domain;
    private String eventId;
    private String eventName;
    private String eventType;
    private Object internalHeaderFields;
    private Long lastEpochMicrosec;
    private String nfcNamingCode;
    private String nfNamingCode;
    private String priority;
    private String reportingEntityId;
    private String reportingEntityName;
    private Integer sequence;
    private String sourceId;
    private String sourceName;
    private Long startEpochMicrosec;
    private Long version;

    private List<AlarmAdditionalField> alarmAdditionalInformation;
    private String alarmCondition;
    private String alarmInterfaceA;
    private String eventCategory;
    private String eventSeverity;
    private String eventSourceType;
    private Long faultFieldsVersion;
    private String specificProblem;
    private String vfStatus;
    private String parentId;
    private int alarmIsCleared;  //mark as 1 when alarm type is cleared, else mark as 2

    @Override
    public int hashCode() {
        return this.version.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof VesAlarm)) {
            return false;
        }
        return this.version.equals(((VesAlarm) object).getVersion());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        VesAlarm vesAlarm = new VesAlarm();

        vesAlarm.setDomain(this.domain);
        vesAlarm.setEventId(this.getEventId());
        vesAlarm.setEventName(this.getEventName());
        vesAlarm.setEventType(this.getEventType());
        vesAlarm.setInternalHeaderFields(this.getInternalHeaderFields());
        vesAlarm.setLastEpochMicrosec(this.getLastEpochMicrosec());
        vesAlarm.setNfcNamingCode(this.nfcNamingCode);
        vesAlarm.setNfNamingCode(this.getNfNamingCode());
        vesAlarm.setPriority(this.getPriority());
        vesAlarm.setReportingEntityId(this.getReportingEntityId());
        vesAlarm.setReportingEntityName(this.reportingEntityName);
        vesAlarm.setSequence(this.getSequence());
        vesAlarm.setSourceId(this.getSourceId());
        vesAlarm.setSourceName(this.getSourceName());
        vesAlarm.setStartEpochMicrosec(this.getStartEpochMicrosec());
        vesAlarm.setVersion(this.getVersion());

        if (alarmAdditionalInformation != null) {
            List<AlarmAdditionalField> alarmAdditionalFields = new ArrayList<>();
            alarmAdditionalInformation.forEach(alarmAdditionalField -> {
                AlarmAdditionalField alarmAdditionalField1 = new AlarmAdditionalField();
                alarmAdditionalField1.setName(alarmAdditionalField.getName());
                alarmAdditionalField1.setName(alarmAdditionalField.getValue());
                alarmAdditionalFields.add(alarmAdditionalField1);
            });
            vesAlarm.setAlarmAdditionalInformation(alarmAdditionalFields);
        }

        vesAlarm.setAlarmCondition(this.getAlarmCondition());
        vesAlarm.setAlarmInterfaceA(this.getAlarmInterfaceA());
        vesAlarm.setEventCategory(this.getEventCategory());
        vesAlarm.setEventSeverity(this.getEventSeverity());
        vesAlarm.setEventSourceType(this.getEventSourceType());
        vesAlarm.setFaultFieldsVersion(this.getFaultFieldsVersion());
        vesAlarm.setSpecificProblem(this.getSpecificProblem());
        vesAlarm.setVfStatus(this.vfStatus);

        return vesAlarm;
    }
}
