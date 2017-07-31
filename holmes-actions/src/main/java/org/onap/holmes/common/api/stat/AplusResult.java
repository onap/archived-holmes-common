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
package org.onap.holmes.common.api.stat;

import java.io.Serializable;

public interface AplusResult extends Serializable {

    /**
     * Derived new alarm type - 0
     */
    byte APLUS_RAISED = 0;

    /**
     * Correlation alarm, root-child alarm - 1
     */
    byte APLUS_CORRELATION = 1;

    /**
     * cleared
     */
    byte APLUS_CLEAR = 2;


    int getId();

    void setId(int id);

    String getRuleInfo();

    void setRuleInfo(String ruleInfo);

    String getRuleType();

    void setRuleType(String ruleType);

    void setRuleId(String ruleId);

    String getRuleId();

    long getCreateTime();

    void setCreateTime(long createTime);

    byte getResultType();

    void setResultType(byte resultType);

    Alarm[] getAffectedAlarms();

    void setAffectedAlarms(Alarm[] affectedAlarms);

}
