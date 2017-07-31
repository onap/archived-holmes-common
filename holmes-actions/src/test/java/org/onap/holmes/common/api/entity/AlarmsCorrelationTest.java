/**
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

package org.onap.holmes.common.api.entity;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

public class AlarmsCorrelationTest {

    private AlarmsCorrelation alarmsCorrelation;

    @Before
    public void before() throws Exception {
        alarmsCorrelation = new AlarmsCorrelation();
    }

    @After
    public void after() throws Exception {

    }


    @Test
    public void getterAndSetter4RuleId() throws Exception {
        final String ruleId = "ruleId";
        alarmsCorrelation.setRuleId(ruleId);
        assertThat(alarmsCorrelation.getRuleId(), equalTo(ruleId));
    }


    @Test
    public void getterAndSetter4RuleInfo() throws Exception {
        final String ruleInfo = "ruleInfo";
        alarmsCorrelation.setRuleInfo(ruleInfo);
        assertThat(alarmsCorrelation.getRuleInfo(), equalTo(ruleInfo));
    }


    @Test
    public void getterAndSetter4ResultType() throws Exception {
        final byte resultType = 1;
        alarmsCorrelation.setResultType(resultType);
        assertThat(alarmsCorrelation.getResultType(), equalTo(resultType));
    }


    @Test
    public void getterAndSetter4CreateTime() throws Exception {
        final Date createTime = new Date();
        alarmsCorrelation.setCreateTime(createTime);
        assertThat(alarmsCorrelation.getCreateTime(), equalTo(createTime));
    }


    @Test
    public void getterAndSetter4ParentAlarmId() throws Exception {
        final long pad = 11L;
        alarmsCorrelation.setParentAlarmId(pad);
        assertThat(alarmsCorrelation.getParentAlarmId(), equalTo(pad));
    }


    @Test
    public void getterAndSetter4ChildAlarmId() throws Exception {
        final long childAlarmId = 11L;
        alarmsCorrelation.setChildAlarmId(childAlarmId);
        assertThat(alarmsCorrelation.getChildAlarmId(), equalTo(childAlarmId));
    }


    @Test
    public void getterAndSetter4Reserve1() throws Exception {
        final long reserve1 = 11L;
        alarmsCorrelation.setReserve1(reserve1);
        assertThat(alarmsCorrelation.getReserve1(), equalTo(reserve1));
    }


    @Test
    public void getterAndSetter4Reserve2() throws Exception {
        final long reserve2 = 11L;
        alarmsCorrelation.setReserve2(reserve2);
        assertThat(alarmsCorrelation.getReserve2(), equalTo(reserve2));
    }


    @Test
    public void getterAndSetter4Reserve3() throws Exception {
        final long reserve3 = 11L;
        alarmsCorrelation.setReserve3(reserve3);
        assertThat(alarmsCorrelation.getReserve3(), equalTo(reserve3));
    }


    @Test
    public void testToString() throws Exception {
        final AlarmsCorrelation alarmsCorrelationTemp = new AlarmsCorrelation();
        String ruleId = "ruleId";
        alarmsCorrelationTemp.setRuleId(ruleId);
        alarmsCorrelation.setRuleId(ruleId);
        assertThat(alarmsCorrelation.toString(), equalTo(alarmsCorrelationTemp.toString()));
    }


    @Test
    public void testEqualsAndHashCode() throws Exception {
        final AlarmsCorrelation alarmsCorrelationTemp = new AlarmsCorrelation();
        String ruleId = "ruleId";
        alarmsCorrelationTemp.setRuleId(ruleId);
        alarmsCorrelation.setRuleId(ruleId);
        assertThat(alarmsCorrelation, equalTo(alarmsCorrelationTemp));
    }
} 
