/**
 * Copyright 2020 - 2021 ZTE Corporation.
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

package org.onap.holmes.common.utils;

import org.easymock.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.exception.HttpException;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.net.ssl.*", "javax.security.*"})
public class JerseyClientTest {

    private JerseyClient jerseyClient = JerseyClient.newInstance();
    private String url = "http://www.onap.org/holmes/test";

    @Test
    public void get_normal() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createMock(WebTarget.class);
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "get");
        Response mockedResponse = PowerMock.createMock(Response.class);

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.path("test")).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.queryParam("ut", true)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.header("Accept", MediaType.APPLICATION_JSON)).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.get()).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn("Normal");

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.path("test")
                .header("Accept", MediaType.APPLICATION_JSON)
                .queryParam("ut", true)
                .get(url), equalTo("Normal"));

        PowerMock.verifyAll();
    }

    @Test
    public void get_normal_with_class() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "get");
        Response mockedResponse = PowerMock.createMock(Response.class);
        Bean bean = new Bean("name", "holmes");

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.get()).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn(GsonUtil.beanToJson(bean));

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.get(url, Bean.class), equalTo(bean));

        PowerMock.verifyAll();
    }

    @Test
    public void get_normal_with_null_class_info() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "get");
        Response mockedResponse = PowerMock.createMock(Response.class);

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.get()).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn("Normal");

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.get(url, null), equalTo("Normal"));

        PowerMock.verifyAll();
    }

    @Test
    public void get_fail() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "get");
        Response mockedResponse = PowerMock.createMock(Response.class);

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.get()).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.NOT_FOUND);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn("Error");

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.get(url), nullValue());

        PowerMock.verifyAll();
    }

    @Test
    public void post_normal_no_body() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "post", Entity.class);
        Response mockedResponse = PowerMock.createMock(Response.class);

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.post(null)).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn("Normal");

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.post(url), equalTo("Normal"));

        PowerMock.verifyAll();
    }

    @Test
    public void post_nobody_fail() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "post", Entity.class);
        Response mockedResponse = PowerMock.createMock(Response.class);

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.post(null)).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.NOT_FOUND);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn("Error");

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.post(url), nullValue());

        PowerMock.verifyAll();
    }

    @Test
    public void post_normal_no_body_with_class() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "post", Entity.class);
        Response mockedResponse = PowerMock.createMock(Response.class);
        Bean bean = new Bean("name", "holmes");

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.post(null)).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn(GsonUtil.beanToJson(bean));

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.post(url, null, Bean.class), equalTo(bean));

        PowerMock.verifyAll();
    }


    @Test
    public void put_normal() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "put", Entity.class);
        Response mockedResponse = PowerMock.createMock(Response.class);

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.put(null)).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn("Normal");

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.put(url, null), equalTo("Normal"));

        PowerMock.verifyAll();
    }

    @Test
    public void put_normal_with_class_info() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "put", Entity.class);
        Response mockedResponse = PowerMock.createMock(Response.class);
        Bean bean = new Bean("name", "holmes");

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.put(null)).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn(GsonUtil.beanToJson(bean));

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.put(url, null, Bean.class), equalTo(bean));

        PowerMock.verifyAll();
    }

    @Test
    public void put_normal_with_null_class_info() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "put", Entity.class);
        Response mockedResponse = PowerMock.createMock(Response.class);
        Bean bean = new Bean("name", "holmes");

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.put(null)).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn(GsonUtil.beanToJson(bean));

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.put(url, null, null), equalTo(GsonUtil.beanToJson(bean)));

        PowerMock.verifyAll();
    }

    @Test
    public void put_fail() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "put", Entity.class);
        Response mockedResponse = PowerMock.createMock(Response.class);

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.put(null)).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.NOT_FOUND);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn("Error");

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.put(url, null, null), nullValue());

        PowerMock.verifyAll();
    }


    @Test
    public void delete_normal() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "delete");
        Response mockedResponse = PowerMock.createMock(Response.class);

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.delete()).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn("Normal");

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.delete(url), equalTo("Normal"));

        PowerMock.verifyAll();
    }

    @Test
    public void delete_normal_with_class_info() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "get");
        Response mockedResponse = PowerMock.createMock(Response.class);
        Bean bean = new Bean("name", "holmes");

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.delete()).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn(GsonUtil.beanToJson(bean));

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.delete(url, Bean.class), equalTo(bean));

        PowerMock.verifyAll();
    }

    @Test
    public void delete_with_null_class_info() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "delete");
        Response mockedResponse = PowerMock.createMock(Response.class);

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.delete()).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.OK);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn("Normal");

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.delete(url, null), equalTo("Normal"));

        PowerMock.verifyAll();
    }

    @Test
    public void delete_fail() {
        Client mockedClient = PowerMock.createMock(Client.class);
        WebTarget mockedTarget = PowerMock.createPartialMock(WebTarget.class, "request");
        Invocation.Builder mockedBuilder = PowerMock.createPartialMock(Invocation.Builder.class, "delete");
        Response mockedResponse = PowerMock.createMock(Response.class);

        EasyMock.expect(mockedClient.target(url)).andReturn(mockedTarget);
        EasyMock.expect(mockedTarget.request()).andReturn(mockedBuilder);
        EasyMock.expect(mockedBuilder.delete()).andReturn(mockedResponse);
        EasyMock.expect(mockedResponse.getStatusInfo()).andReturn(Response.Status.NOT_FOUND);
        EasyMock.expect(mockedResponse.readEntity(String.class)).andReturn("Error");

        WhiteboxImpl.setInternalState(jerseyClient, "client", mockedClient);

        PowerMock.replayAll();

        assertThat(jerseyClient.delete(url), nullValue());

        PowerMock.verifyAll();
    }

    @Test
    public void headers() {
        Map<String, Object> hds = new HashMap<>();
        jerseyClient.headers(hds);
    }

    @Test
    public void queryParams() {
        Map<String, Object> params = new HashMap<>();
        jerseyClient.queryParams(params);
    }
}

class Bean {
    private String name;
    private String value;

    public Bean(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hName = name == null ? 0 : name.hashCode();
        int hValue = value == null ? 0 : value.hashCode();
        return hName << 1 + hValue >> 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Bean)) {
            return false;
        }

        Bean b = (Bean) o;
        return name.equals(b.name) && value.equals(b.value);
    }
}