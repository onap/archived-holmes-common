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
import java.util.Map;
import java.util.HashMap;
import java.util.TimeZone;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VesAlarm implements Cloneable, Serializable{
    private String domain;
    private String eventId;
    private String eventName;
    private String eventType;
    //Temporarily make it transient cuz no details of this field is provided and it is not used for now.
    transient private Object internalHeaderFields;
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
    private String version;

    private Map<String, String> alarmAdditionalInformation;
    private String alarmCondition;
    private String alarmInterfaceA;
    private String eventCategory;
    private String eventSeverity;
    private String eventSourceType;
    private String faultFieldsVersion;
    private String specificProblem;
    private String vfStatus;
    private String parentId;
    private int alarmIsCleared = 0;  //mark as 1 when alarm type is cleared, else mark as 0
    private int rootFlag = 0;        // mark as 1 when alarm is a root alarm , else mark as 0
    private String nfVendorName;
    private String vesEventListenerVersion;
    private TimeZone timeZoneOffset;

    @Override
    public int hashCode() {
        return (this.getSourceId() + this.eventName.replace("Cleared", "")).hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof VesAlarm)) {
            return false;
        }
        return this.eventName.replace("Cleared", "")
                .equals(((VesAlarm) object).getEventName().replace("Cleared", ""))
                && this.getSourceId().equals(((VesAlarm) object).getSourceId());
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
           Map<String, String> alarmAdditionalFields = new HashMap<String, String>();
           for(String key: alarmAdditionalInformation.keySet()) {
              alarmAdditionalFields.put(key, alarmAdditionalInformation.get(key));
           }
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
        vesAlarm.setAlarmIsCleared(this.alarmIsCleared);
        vesAlarm.setRootFlag(this.rootFlag);
	vesAlarm.setVesEventListenerVersion(this.vesEventListenerVersion);
        vesAlarm.setNfVendorName(this.nfVendorName);
        vesAlarm.setTimeZoneOffset(this.timeZoneOffset);

        return vesAlarm;
    }
}
