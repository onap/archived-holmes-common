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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CorrelationRuleTest {

    private CorrelationRule correlationRule;

    @Before
    public void before() throws Exception {
        correlationRule = new CorrelationRule();
    }

    @After
    public void after() throws Exception {

    }

    @Test
    public void getterAndSetter4Rid() throws Exception {
        final String rid = "rid";
        correlationRule.setRid(rid);
        assertThat(correlationRule.getRid(), equalTo(rid));
    }

    @Test
    public void getterAndSetter4Name() throws Exception {
        final String name = "name";
        correlationRule.setName(name);
        assertThat(correlationRule.getName(), equalTo(name));
    }

    @Test
    public void getterAndSetter4Description() throws Exception {
        final String description = "description";
        correlationRule.setDescription(description);
        assertThat(correlationRule.getDescription(), equalTo(description));
    }

    @Test
    public void getterAndSetter4Enabled() throws Exception {
        final int enabled = 1;
        correlationRule.setEnabled(enabled);
        assertThat(correlationRule.getEnabled(), equalTo(enabled));
    }

    @Test
    public void getterAndSetter4TemplateID() throws Exception {
        final long templateId = 1L;
        correlationRule.setTemplateID(templateId);
        assertThat(correlationRule.getTemplateID(), equalTo(templateId));
    }

    @Test
    public void getterAndSetter4EngineId() throws Exception {
        final String engineId = "engineId";
        correlationRule.setEngineID(engineId);
        assertThat(correlationRule.getEngineID(), equalTo(engineId));
    }

    @Test
    public void getterAndSetter4EngineType() throws Exception {
        final String engineType = "engineType";
        correlationRule.setEngineType(engineType);
        assertThat(correlationRule.getEngineType(), equalTo(engineType));
    }

    @Test
    public void getterAndSetter4Creator() throws Exception {
        final String creator = "creator";
        correlationRule.setCreator(creator);
        assertThat(correlationRule.getCreator(), equalTo(creator));
    }

    @Test
    public void getterAndSetter4Modifier() throws Exception {
        final String modifier = "modifier";
        correlationRule.setModifier(modifier);
        assertThat(correlationRule.getModifier(), equalTo(modifier));
    }

    @Test
    public void getterAndSetter4Params() throws Exception {
        final Properties params = new Properties();
        correlationRule.setParams(params);
        assertThat(correlationRule.getParams(), equalTo(params));
    }

    @Test
    public void getterAndSetter4Content() throws Exception {
        final String content = "content";
        correlationRule.setContent(content);
        assertThat(correlationRule.getContent(), equalTo(content));
    }

    @Test
    public void getterAndSetter4Vendor() throws Exception {
        final String vendor = "vendor";
        correlationRule.setVendor(vendor);
        assertThat(correlationRule.getVendor(), equalTo(vendor));
    }

    @Test
    public void getterAndSetter4CreateTime() throws Exception {
        Date createTime = new Date();
        correlationRule.setCreateTime(createTime);
        assertThat(correlationRule.getCreateTime(), equalTo(createTime));
    }

    @Test
    public void getterAndSetter4UpdateTime() throws Exception {
        final Date updateTime = new Date();
        correlationRule.setUpdateTime(updateTime);
        assertThat(correlationRule.getUpdateTime(), equalTo(updateTime));
    }

    @Test
    public void getterAndSetter4PackageName() throws Exception {
        final String packageName = "packageName";
        correlationRule.setPackageName(packageName);
        assertThat(correlationRule.getPackageName(), equalTo(packageName));
    }
} 
