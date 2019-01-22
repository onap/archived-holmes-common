/**
 * Copyright 2017 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.api.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CorrelationRule implements Cloneable {
    @SerializedName(value = "ruleid")
    private String rid;
    @SerializedName(value = "rulename")
    private String name;
    private String description;
    private int enabled;
    private long templateID;
    private String engineID;
    private String engineType;
    private String creator;
    private String modifier;
    private Properties params;
    private String content;
    private String vendor;
    @SerializedName(value = "createtime")
    private Date createTime;
    @SerializedName(value = "updatetime")
    private Date updateTime;
    @SerializedName(value = "package")
    private String packageName;
    @SerializedName(value = "controlloopname")
    private String closedControlLoopName;
    @SerializedName(value = "engineinstance")
    private String engineInstance;

    @Override
    public Object clone() {
        CorrelationRule r = null;
        try {
            r = (CorrelationRule) super.clone();
        } catch (CloneNotSupportedException e) {
            // This will never happen.
            throw new InternalError(e);
        }

        r.rid = rid == null ? null : rid;
        r.name = name == null ? null : name;
        r.description = description == null ? null : description;
        r.enabled = enabled;
        r.templateID = templateID;
        r.engineID = engineID == null ? null : engineID;
        r.engineType = engineType == null ? null : engineType;
        r.creator = creator == null ? null : creator;
        r.modifier = modifier  == null ? null : modifier;
        r.params = params == null ? null : (Properties) params.clone();
        r.content = content  == null ? null : content;
        r.vendor = vendor  == null ? null : vendor;
        r.createTime = createTime == null ? null : (Date) createTime.clone();
        r.updateTime = updateTime == null ? null : (Date) updateTime.clone();
        r.packageName = packageName == null ? null : packageName;
        r.closedControlLoopName = closedControlLoopName == null ? null : closedControlLoopName;
        r.engineInstance = engineInstance == null ? null : engineInstance;

        return r;
    }
}
