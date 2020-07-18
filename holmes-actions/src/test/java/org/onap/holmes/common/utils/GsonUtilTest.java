package org.onap.holmes.common.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class GsonUtilTest {

    private final TestBean bean1 = new TestBean("onap1", 10, 10f, 10d);
    private final TestBean bean2 = new TestBean("onap2", 20, 20f, 20d);
    private final Gson gson = new Gson();

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
        List<Map<String, TestBean>> actual = GsonUtil.jsonToListMaps(
                "[{\"onap1\":{\"string\":\"onap1\",\"integer\":10,\"aFloat\":10.0,\"aDouble\":10.0}},"
                 + "{\"onap2\":{\"string\":\"onap2\",\"integer\":20,\"aFloat\":20.0,\"aDouble\":20.0}}]", TestBean.class);

        assertThat(actual.get(0).get("onap1"), equalTo(new TestBean("onap1", 10, 10f, 10d)));
        assertThat(actual.get(1).get("onap2"), equalTo(new TestBean("onap2", 20, 20f, 20d)));
    }

    @Test
    public void jsonToMap() {
        Map<String, TestBean> actual = GsonUtil
                .jsonToMap("{\"onap1\":{\"string\":\"onap1\",\"integer\":10,\"aFloat\":10.0,\"aDouble\":10.0}}", TestBean.class);
        assertThat(actual.get("onap1"), equalTo(new TestBean("onap1", 10, 10f, 10d)));
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
}

class TestBean {
    private String string;
    private int integer;
    private float aFloat;
    private double aDouble;

    public TestBean(String string, int integer, float aFloat, double aDouble) {
        this.string = string;
        this.integer = integer;
        this.aFloat = aFloat;
        this.aDouble = aDouble;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || ! (o instanceof TestBean)) {
            return false;
        }

        return  string.equals(((TestBean) o).string)
                && integer == ((TestBean) o).integer
                && aDouble == ((TestBean) o).aDouble
                && aFloat == ((TestBean) o).aFloat;
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }
}