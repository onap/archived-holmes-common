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

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlarmInfo {

    @SerializedName(value = "eventid")
    private String eventId;

    @SerializedName(value = "eventname")
    private String eventName;

    @SerializedName(value = "startepochmicrosec")
    private Long startEpochMicroSec;

    @SerializedName(value = "lastepochmicrosec")
    private Long lastEpochMicroSec;

    @SerializedName(value = "sourceid")
    private String sourceId;

    @SerializedName(value = "sourcename")
    private String sourceName;

    @SerializedName(value = "sequence")
    private int sequence;

    @SerializedName(value = "alarmiscleared")
    private int alarmIsCleared;

    @SerializedName(value = "rootflag")
    private int rootFlag;
}
