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
import lombok.NoArgsConstructor;

@Data
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
}
