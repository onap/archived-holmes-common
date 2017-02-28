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

package org.openo.holmes.common.api.entity;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openo.holmes.common.api.stat.Alarm;

public class CorrelationResultTest {

    private CorrelationResult correlationResult;

    @Before
    public void before() throws Exception {
        correlationResult = new CorrelationResult();
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void getterAndSetter4RuleId() throws Exception {
        final String ruleId = "ruleId";
        correlationResult.setRuleId(ruleId);
        assertThat(correlationResult.getRuleId(), equalTo(ruleId));
    }

    @Test
    public void getterAndSetter4CreateTimeL() throws Exception {
        final long createTimeL = new Date().getTime();
        correlationResult.setCreateTimeL(
                createTimeL);
        assertThat(correlationResult.getCreateTimeL(), equalTo(createTimeL));
    }

    @Test
    public void getterAndSetter4GetResultType() throws Exception {
        final byte resultType = 2;
        correlationResult.setResultType(resultType);
        assertThat(correlationResult.getResultType(), equalTo(resultType));
    }

    @Test
    public void getterAndSetter4AffectedAlarms() throws Exception {
        final Alarm alarm[] = new Alarm[2];
        correlationResult.setAffectedAlarms(alarm);
        assertThat(correlationResult.getAffectedAlarms(), equalTo(alarm));
    }

    @Test
    public void testToString() throws Exception {
        CorrelationResult resultTemp = new CorrelationResult();
        final String tempStr = "aa";
        resultTemp.setRuleId(tempStr);
        correlationResult.setRuleId(tempStr);
        assertThat(correlationResult.toString(), equalTo(resultTemp.toString()));
    }
}
