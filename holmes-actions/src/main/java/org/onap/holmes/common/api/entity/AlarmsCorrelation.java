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
import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlarmsCorrelation {

    @SerializedName(value = "ruleid")
    private String ruleId;

    @SerializedName(value = "ruleinfo")
    private String ruleInfo;

    @SerializedName(value = "resulttype")
    private byte resultType;

    @SerializedName(value = "createtime")
    private Date createTime;

    @SerializedName(value = "parentalarmid")
    private long parentAlarmId;

    @SerializedName(value = "childalarmid")
    private long childAlarmId;

    private long reserve1 = -1;

    private long reserve2 = -1;

    private long reserve3 = -1;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append("ruleId=").append(ruleId).append(",");
        sb.append("ruleInfo=").append(ruleInfo).append(",");
        sb.append("createTime=").append(createTime == null ? null : createTime.toString()).append(",");
        sb.append("parentAlarmId=").append(parentAlarmId).append(",");
        sb.append("childAlarmId=").append(childAlarmId).append(",");
        sb.append("reserve1=").append(reserve1).append(",");
        sb.append("reserve2=").append(reserve2).append(",");
        sb.append("reserve3=").append(reserve3).append("]");
        return sb.toString();
    }
}
