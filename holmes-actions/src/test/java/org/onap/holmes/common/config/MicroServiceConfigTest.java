/**
 * Copyright 2017-2020 ZTE Corporation.
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

package org.onap.holmes.common.config;

import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.onap.holmes.common.config.MicroServiceConfig.*;

@PrepareForTest(MicroServiceConfig.class)
@PowerMockIgnore({"javax.ws.*"})
@RunWith(PowerMockRunner.class)
public class MicroServiceConfigTest {

    private static String ACTUAL_HOSTNAME = System.getenv(HOSTNAME);

    @Test
    public void getMsbServerAddrTest() {
        System.setProperty(MSB_IAG_SERVICE_HOST, "test");
        System.setProperty(MSB_IAG_SERVICE_PORT, "443");
        assertThat("http://test:443", equalTo(getMsbServerAddrWithHttpPrefix()));
        System.clearProperty(MicroServiceConfig.MSB_IAG_SERVICE_PORT);
        System.clearProperty(MicroServiceConfig.MSB_IAG_SERVICE_HOST);
    }

    @Test
    public void getMsbServerIpTest() {
        System.setProperty(MSB_IAG_SERVICE_HOST, "10.54.23.79");
        System.setProperty(MSB_IAG_SERVICE_PORT, "443");
        System.setProperty(HOSTNAME, "rule-mgmt");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "getServiceConfigInfoFromCBS", String.class);
        EasyMock.expect(MicroServiceConfig.getServiceConfigInfoFromCBS(System.getProperty(HOSTNAME)))
                .andReturn("{\"msb.hostname\": \"10.54.23.79:443\"}").times(2);
        PowerMock.replayAll();
        assertThat("10.54.23.79", equalTo(getMsbIpAndPort()[0]));
        assertThat("443", equalTo(getMsbIpAndPort()[1]));
        System.clearProperty(MicroServiceConfig.HOSTNAME);
        System.clearProperty(MicroServiceConfig.MSB_IAG_SERVICE_PORT);
        System.clearProperty(MicroServiceConfig.MSB_IAG_SERVICE_HOST);
    }

    @Test
    public void getServiceIpTest() {
        String ip = StringUtils.isEmpty(ACTUAL_HOSTNAME) ? "10.74.5.8" : ACTUAL_HOSTNAME;
        String hostname = String.format("http://%s", ip);
        System.setProperty(HOSTNAME, hostname);
        assertThat(ip, equalTo(getMicroServiceIpAndPort()[0]));
        assertThat("80", equalTo(getMicroServiceIpAndPort()[1]));
        System.clearProperty(HOSTNAME);
    }

    @Test
    public void getConsulAddrInfoTest() {
        System.setProperty(CONSUL_HOST, "127.0.0.1");
        assertThat("http://127.0.0.1:8500/v1/catalog/service/", equalTo(getConsulAddrInfo()));
        System.clearProperty(CONSUL_HOST);
    }

    @Test
    public void getConfigBindingServiceAddrInfoTest_consul_not_exist() throws Exception {
        System.setProperty(CONFIG_BINDING_SERVICE, "config_binding_service");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andThrow(new RuntimeException("Invalid URL."));

        PowerMock.replayAll();

        assertThat(getServiceAddrInfoFromDcaeConsulByHostName(System.getProperty(CONFIG_BINDING_SERVICE))
                , is(nullValue()));

        PowerMock.verifyAll();
        System.clearProperty(CONFIG_BINDING_SERVICE);
    }

    @Test
    public void getServiceAddrInfoFromDcaeConsulByHostName_consul_exists() throws Exception {
        System.setProperty(CONFIG_BINDING_SERVICE, "config_binding_service");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        System.setProperty(CONSUL_HOST, "127.0.0.1");

        PowerMock.replayAll();

        assertThat(getServiceAddrInfoFromDcaeConsulByHostName(System.getProperty(CONFIG_BINDING_SERVICE)),
                equalTo("http://127.0.0.2:8080"));

        PowerMock.verifyAll();

        System.clearProperty(CONSUL_HOST);
        System.clearProperty(CONFIG_BINDING_SERVICE);
    }

    @Test
    public void getConfigBindingServiceAddrInfoTest_consul_return_empty_array() throws Exception {
        System.setProperty(CONFIG_BINDING_SERVICE, "config_binding_service");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[]");
        System.setProperty(CONSUL_HOST, "127.0.0.1");

        PowerMock.replayAll();

        assertThat(getServiceAddrInfoFromDcaeConsulByHostName(System.getProperty(CONFIG_BINDING_SERVICE)),
                is(nullValue()));

        PowerMock.verifyAll();

        System.clearProperty(CONSUL_HOST);
        System.clearProperty(CONFIG_BINDING_SERVICE);
    }

    @Test
    public void getConfigBindingServiceAddrInfoTest_consul_exists_property_not_exist() throws Exception {
        System.setProperty(CONFIG_BINDING_SERVICE, "config_binding_service");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\"}]");
        System.setProperty(CONSUL_HOST, "127.0.0.1");

        PowerMock.replayAll();

        assertThat(getServiceAddrInfoFromDcaeConsulByHostName(System.getProperty(CONFIG_BINDING_SERVICE)),
                is(nullValue()));

        PowerMock.verifyAll();

        System.clearProperty(CONSUL_HOST);
        System.clearProperty(CONFIG_BINDING_SERVICE);
    }

    @Test
    public void getServiceAddrInfoFromCBS_consul_not_exist() throws Exception {
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andThrow(new RuntimeException("Invalid URL.")).times(2);

        PowerMock.replayAll();

        assertThat(getServiceConfigInfoFromCBS(HOSTNAME), is(nullValue()));

        PowerMock.verifyAll();
    }

    @Test
    public void getServiceAddrInfoFromDcaeConsulByHostName_consul_exists_service_not_exist() throws Exception {
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[]");

        PowerMock.replayAll();
        assertThat(getServiceAddrInfoFromDcaeConsulByHostName(HOSTNAME), is(nullValue()));
        PowerMock.verifyAll();
    }

    @Ignore
    public void getMsbAddrInfo_msb_registered() throws Exception {
        System.setProperty(MSB_IAG_SERVICE_HOST, "10.74.5.8");
        System.setProperty(MSB_IAG_SERVICE_PORT, "1545");
        System.setProperty(HOSTNAME, "rule-mgmt");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "getServiceConfigInfoFromCBS", String.class);
        EasyMock.expect(MicroServiceConfig.getServiceConfigInfoFromCBS(System.getProperty(HOSTNAME)))
                .andReturn("{\"msb.hostname\": \"127.0.0.3:5432\"}");

        PowerMock.replayAll();
        String[] msbInfo = getMsbIpAndPort();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("127.0.0.3"));
        assertThat(msbInfo[1], equalTo("5432"));

        System.clearProperty(HOSTNAME);
        System.clearProperty(MSB_IAG_SERVICE_PORT);
        System.clearProperty(MSB_IAG_SERVICE_HOST);
    }

    @Ignore
    public void getMsbAddrInfo_msb_not_registered() throws Exception {
        System.setProperty(MSB_IAG_SERVICE_HOST, "10.74.5.8");
        System.setProperty(MSB_IAG_SERVICE_PORT, "1545");
        System.setProperty(HOSTNAME, "rule-mgmt");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "getServiceConfigInfoFromCBS", String.class);
        EasyMock.expect(MicroServiceConfig.getServiceConfigInfoFromCBS(System.getProperty(HOSTNAME)))
                .andReturn("{}");

        PowerMock.replayAll();
        String[] msbInfo = getMsbIpAndPort();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("10.74.5.8"));
        assertThat(msbInfo[1], equalTo("1545"));

        System.clearProperty(HOSTNAME);
        System.clearProperty(MSB_IAG_SERVICE_PORT);
        System.clearProperty(MSB_IAG_SERVICE_HOST);
    }

    @Test
    public void getMicroServiceIpAndPort_service_registered_to_consul() throws Exception {
        System.setProperty(HOSTNAME, "rule-mgmt");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.3\", \"ServicePort\": \"5432\"}]");

        PowerMock.replayAll();
        String[] msbInfo = getMicroServiceIpAndPort();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("127.0.0.3"));
        assertThat(msbInfo[1], equalTo("5432"));

        System.clearProperty(HOSTNAME);
    }

    @Test
    public void getMicroServiceIpAndPort_service_not_registered_to_consul() throws Exception {
        String ip = StringUtils.isEmpty(ACTUAL_HOSTNAME) ? "10.74.5.8" : ACTUAL_HOSTNAME;
        String port = StringUtils.isEmpty(ACTUAL_HOSTNAME) ? "1545" : "80";
        String hostname = String.format("http://%s:%s", ip, port);
        System.setProperty(HOSTNAME, hostname);
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[]");

        PowerMock.replayAll();
        String[] msbInfo = getMicroServiceIpAndPort();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo(ip));
        assertThat(msbInfo[1], equalTo(port));

        System.clearProperty(HOSTNAME);
    }

    @Test
    public void getMicroServiceIpAndPort_service_not_registered_full_addr() throws Exception {
        String ip = StringUtils.isEmpty(ACTUAL_HOSTNAME) ? "10.74.5.8" : ACTUAL_HOSTNAME;
        String port = StringUtils.isEmpty(ACTUAL_HOSTNAME) ? "1545" : "80";
        String hostname = String.format("http://%s:%s", ip, port);
        System.setProperty(HOSTNAME, hostname);
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.replayAll();
        String[] msbInfo = getMicroServiceIpAndPort();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo(ip));
        assertThat(msbInfo[1], equalTo(port));

        System.clearProperty(HOSTNAME);
    }

    @Test
    public void getMicroServiceIpAndPort_service_not_registered_no_port() throws Exception {
        String ip = StringUtils.isEmpty(ACTUAL_HOSTNAME) ? "10.74.5.8" : ACTUAL_HOSTNAME;
        String hostname = String.format("http://%s", ip);
        System.setProperty(HOSTNAME, hostname);
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.replayAll();
        String[] msbInfo = getMicroServiceIpAndPort();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo(ip));
        assertThat(msbInfo[1], equalTo("80"));

        System.clearProperty(HOSTNAME);
    }

    @Test
    public void getMicroServiceIpAndPort_service_not_registered_only_ip() throws Exception {
        String ip = StringUtils.isEmpty(ACTUAL_HOSTNAME) ? "10.74.5.8" : ACTUAL_HOSTNAME;
        String hostname = String.format("http://%s", ip);
        System.setProperty(HOSTNAME, hostname);
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.replayAll();
        String[] msbInfo = getMicroServiceIpAndPort();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo(ip));
        assertThat(msbInfo[1], equalTo("80"));

        System.clearProperty(HOSTNAME);
    }

    @Test
    public void getMicroServiceIpAndPort_service_not_registered_full_addr_https() throws Exception {
        String ip = StringUtils.isEmpty(ACTUAL_HOSTNAME) ? "10.74.5.8" : ACTUAL_HOSTNAME;
        String port = StringUtils.isEmpty(ACTUAL_HOSTNAME) ? "1545" : "80";
        String hostname = String.format("http://%s:%s", ip, port);
        System.setProperty(HOSTNAME, hostname);
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[]");

        PowerMock.replayAll();
        String[] msbInfo = getMicroServiceIpAndPort();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo(ip));
        assertThat(msbInfo[1], equalTo(port));

        System.clearProperty(HOSTNAME);
    }
}