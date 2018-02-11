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

package org.onap.holmes.common.dmaap.entity;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PolicyMsg {

    private String version = "1.0.2";
    private String closedLoopControlName;
    private String requestID;
    private EVENT_STATUS closedLoopEventStatus = EVENT_STATUS.ONSET;
    private long closedLoopAlarmStart;
    private long closedLoopAlarmEnd;
    private String closedLoopEventClient = "DCAE.HolmesInstance";
    private String policyVersion;
    private String policyName;
    private String policyScope;
    private String from = "DCAE";
    @SerializedName(value = "target_type")
    private String targetType = "VM";
    private String target;
    @SerializedName(value = "AAI")
    private Map<String, Object> aai = new HashMap<>();

    public static enum EVENT_STATUS {
        ONSET, ABATED;

        @Override
        public String toString(){
            return this.name();
        }
    }
}
