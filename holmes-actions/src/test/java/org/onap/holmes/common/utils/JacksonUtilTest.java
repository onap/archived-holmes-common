/**
 * Copyright 2016 ZTE Corporation.
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


package org.onap.holmes.common.utils;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.junit.Test;
import org.onap.holmes.common.utils.bean.TestBean;

public class JacksonUtilTest {

    @Test
    public void testBeanToJson() throws Exception {
        TestBean o = new TestBean();
        o.setId("id");
        String result = GsonUtil.beanToJson(o);
        assertThat("{\"id\":\"id\"}", equalTo(result));
    }

    @Test
    public void jsonToBean_json_null() throws Exception {
        String jsonNull = null;
        TestBean testBean = GsonUtil.jsonToBean(jsonNull, TestBean.class);
        assertThat(testBean, equalTo(null));
    }

    @Test
    public void jsonToBean_json_normal() throws Exception {
        String json = "{\"id\":\"id\"}";
        TestBean testBean = GsonUtil.jsonToBean(json, TestBean.class);
        assertThat(testBean.getId(), equalTo("id"));
    }

}