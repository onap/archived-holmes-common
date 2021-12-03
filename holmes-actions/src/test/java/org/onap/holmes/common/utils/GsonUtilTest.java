/**
 * Copyright 2020 ZTE Corporation.
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

import com.google.gson.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class GsonUtilTest {

    private final TestBean bean1;
    private final TestBean bean2;
    private final Gson gson = buildGson();
    private Date date;

    public GsonUtilTest() {
        date = new Date();
        bean1 = new TestBean("onap1", 10, 10f, 10d, date);
        bean2 = new TestBean("onap2", 20, 20f, 20d, date);
    }

    @Test
    public void beanToJson() {
        String expected = gson.toJson(bean1);
        String actual = GsonUtil.beanToJson(bean1);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void jsonToBean() {
        TestBean expected = bean1;
        TestBean actual = GsonUtil.jsonToBean(gson.toJson(expected), TestBean.class);
        assertThat(expected.getString(), equalTo(actual.getString()));
        assertThat(expected.getInteger(), equalTo(actual.getInteger()));
        assertThat(expected.getaDouble(), equalTo(actual.getaDouble()));
        assertThat(expected.getaFloat(), equalTo(actual.getaFloat()));
        assertThat(expected.getaDate(), equalTo(actual.getaDate()));
    }

    @Test
    public void jsonToList() {
        List<TestBean> expected = Arrays.asList( bean1, bean2);
        List<TestBean> actual = GsonUtil.jsonToList(gson.toJson(expected), TestBean.class);

        assertThat(expected.size(), equalTo(actual.size()));
        for (TestBean tb : expected) {
            assertThat(actual.contains(tb), is(true));
        }
    }

    @Test
    public void jsonToListMaps() {
        long timestamp = date.getTime();
        List<Map<String, TestBean>> actual = GsonUtil.jsonToListMaps(
                String.format("[{\"onap1\":{\"string\":\"onap1\",\"integer\":10,\"aFloat\":10.0,\"aDouble\":10.0,\"aDate\": %d}},", timestamp)
                 + String.format("{\"onap2\":{\"string\":\"onap2\",\"integer\":20,\"aFloat\":20.0,\"aDouble\":20.0,\"aDate\": \"%s\"}}]", timestamp), TestBean.class);

        assertThat(actual.get(0).get("onap1"), equalTo(new TestBean("onap1", 10, 10f, 10d, date)));
        assertThat(actual.get(1).get("onap2"), equalTo(new TestBean("onap2", 20, 20f, 20d, date)));
    }

    @Test
    public void jsonToMap() {
        Map<String, TestBean> actual = GsonUtil
                .jsonToMap(String.format("{\"onap1\":{\"string\":\"onap1\",\"integer\":10,\"aFloat\":10.0,\"aDouble\":10.0,\"aDate\":%d}}",date.getTime()), TestBean.class);
        assertThat(actual.get("onap1"), equalTo(new TestBean("onap1", 10, 10f, 10d, date)));
    }

    @Test
    public void getAsString() {
        assertThat("onap1",
                equalTo(GsonUtil.getAsString(JsonParser.parseString(GsonUtil.beanToJson(bean1)).getAsJsonObject(),"string")));
    }

    @Test
    public void getAsLong() {
        assertThat(10L,
                is(GsonUtil.getAsLong(JsonParser.parseString(GsonUtil.beanToJson(bean1)).getAsJsonObject(),"integer")));
    }

    @Test
    public void getAsInt() {
        assertThat(10,
                is(GsonUtil.getAsInt(JsonParser.parseString(GsonUtil.beanToJson(bean1)).getAsJsonObject(),"integer")));
    }

    private Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Integer.class, (JsonDeserializer<Integer>) (json, typeOfT, context) -> {
                    try {
                        return json.getAsInt();
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (jsonElement, type, jsonDeserializationContext) -> {
                    try {
                        return jsonElement == null ? null : new Date(jsonElement.getAsLong());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, jsonSerializationContext)
                        -> date == null ? null : new JsonPrimitive(date.getTime()))
                .create();
    }
}

class TestBean {
    private String string;
    private int integer;
    private float aFloat;
    private double aDouble;
    private Date aDate;

    public TestBean(String string, int integer, float aFloat, double aDouble, Date aDate) {
        this.string = string;
        this.integer = integer;
        this.aFloat = aFloat;
        this.aDouble = aDouble;
        this.aDate = aDate;
    }

    public String getString() {
        return string;
    }

    public int getInteger() {
        return integer;
    }

    public float getaFloat() {
        return aFloat;
    }

    public double getaDouble() {
        return aDouble;
    }

    public Date getaDate(){ return aDate;}

    @Override
    public boolean equals(Object o) {
        if (o == null || ! (o instanceof TestBean)) {
            return false;
        }

        return  string.equals(((TestBean) o).string)
                && integer == ((TestBean) o).integer
                && aDouble == ((TestBean) o).aDouble
                && aFloat == ((TestBean) o).aFloat
                && ((aDate == null && ((TestBean)o).aDate == null) || aDate.equals(((TestBean)o).aDate));
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }
}