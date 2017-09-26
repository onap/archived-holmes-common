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

package org.onap.holmes.common.config;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.onap.holmes.common.config.MicroServiceConfig.*;

import org.easymock.EasyMock;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

@PrepareForTest(MicroServiceConfig.class)
@PowerMockIgnore({"javax.ws.*"})
public class MicroServiceConfigTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Test
    public void getMsbServerAddrTest() {
        System.setProperty(MSB_ADDR, "test:80");
        assertThat("http://test:80", equalTo(getMsbServerAddr()));
        System.clearProperty(MicroServiceConfig.MSB_ADDR);
    }

    @Test
    public void getMsbServerIpTest() {
        System.setProperty(MSB_ADDR, "10.54.23.79:80");
        assertThat("10.54.23.79", equalTo(getMsbAddrInfo()[0]));
        assertThat("80", equalTo(getMsbAddrInfo()[1]));
        System.clearProperty(MSB_ADDR);
    }

    @Test
    public void getServiceIpTest() {
        System.setProperty(HOSTNAME, "127.0.0.1");
        assertThat("127.0.0.1", equalTo(getServiceAddrInfo()[0]));
        assertThat("80", equalTo(getServiceAddrInfo()[1]));
        System.clearProperty(HOSTNAME);
    }

    @Test
    public void getConsulAddrInfoTest() {
        System.setProperty(CONSUL_HOST, "127.0.0.1");
        assertThat("127.0.0.1:8500/v1/catalog/service/", equalTo(getConsulAddrInfo()));
        System.clearProperty(CONSUL_HOST);
    }

    @Test
    public void getConfigBindingServiceAddrInfoTest_consul_not_exist() throws Exception {
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andThrow(new RuntimeException("Invalid URL."));

        PowerMock.replayAll();

        assertThat(getConfigBindingServiceAddrInfo(), is(nullValue()));

        PowerMock.verifyAll();
    }

    @Test
    public void getConfigBindingServiceAddrInfoTest_consul_exists() throws Exception {
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        System.setProperty(CONSUL_HOST, "127.0.0.1");

        PowerMock.replayAll();

        assertThat(getConfigBindingServiceAddrInfo(), equalTo("127.0.0.2:8080"));

        PowerMock.verifyAll();

        System.clearProperty(CONSUL_HOST);
    }

    @Test
    public void getConfigBindingServiceAddrInfoTest_consul_exists_propertie_not_exist() throws Exception {
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\"}]");
        System.setProperty(CONSUL_HOST, "127.0.0.1");

        PowerMock.replayAll();

        assertThat(getConfigBindingServiceAddrInfo(), is(nullValue()));

        PowerMock.verifyAll();

        System.clearProperty(CONSUL_HOST);
    }

    @Test
    public void getServiceAddrInfoFromCBS_consul_not_exist() throws Exception {
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andThrow(new RuntimeException("Invalid URL.")).times(2);

        PowerMock.replayAll();

        assertThat(getServiceAddrInfoFromCBS(HOSTNAME), is(nullValue()));

        PowerMock.verifyAll();
    }

    @Test
    public void getServiceAddrInfoFromCBS_consul_exists_service_not_exist() throws Exception {
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.replayAll();
        assertThat(getServiceAddrInfoFromCBS(HOSTNAME), is(nullValue()));
        PowerMock.verifyAll();
    }

    @Test
    public void getServiceAddrInfoFromCBS_normal() throws Exception {
        System.setProperty(HOSTNAME, "rule-mgmt");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{\"rule-mgmt\": \"[\\\"127.0.0.3:5432\\\"]\"}");

        PowerMock.replayAll();

        assertThat(getServiceAddrInfoFromCBS("rule-mgmt"), equalTo("127.0.0.3:5432"));

        PowerMock.verifyAll();

        System.clearProperty(HOSTNAME);
    }

    @Test
    public void getMsbAddrInfo_msb_registered() throws Exception {
        System.setProperty(MSB_ADDR, "10.74.5.8:1545");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{\"" + MSB_ADDR + "\": \"[\\\"127.0.0.3:5432\\\"]\"}");

        PowerMock.replayAll();
        String[] msbInfo = getMsbAddrInfo();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("127.0.0.3"));
        assertThat(msbInfo[1], equalTo("5432"));

        System.clearProperty(MSB_ADDR);
    }

    @Test
    public void getMsbAddrInfo_msb_not_registered() throws Exception {
        System.setProperty(MSB_ADDR, "10.74.5.8:1545");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.replayAll();
        String[] msbInfo = getMsbAddrInfo();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("10.74.5.8"));
        assertThat(msbInfo[1], equalTo("1545"));

        System.clearProperty(MSB_ADDR);
    }

    @Test
    public void getServiceAddrInfo_msb_registered() throws Exception {
        System.setProperty(HOSTNAME, "rule-mgmt");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{\"rule-mgmt\": \"[\\\"127.0.0.3:5432\\\"]\"}");

        PowerMock.replayAll();
        String[] msbInfo = getServiceAddrInfo();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("127.0.0.3"));
        assertThat(msbInfo[1], equalTo("5432"));

        System.clearProperty(HOSTNAME);
    }

    @Test
    public void getServiceAddrInfo_msb_not_registered() throws Exception {
        System.setProperty(HOSTNAME, "10.74.5.8:1545");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.replayAll();
        String[] msbInfo = getServiceAddrInfo();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("10.74.5.8"));
        assertThat(msbInfo[1], equalTo("1545"));

        System.clearProperty(HOSTNAME);
    }

    @Test
    public void getServiceAddrInfo_msb_not_registered_full_addr() throws Exception {
        System.setProperty(HOSTNAME, "http://10.74.5.8:1545");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.replayAll();
        String[] msbInfo = getServiceAddrInfo();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("10.74.5.8"));
        assertThat(msbInfo[1], equalTo("1545"));

        System.clearProperty(MSB_ADDR);
    }

    @Test
    public void getServiceAddrInfo_msb_not_registered_no_port() throws Exception {
        System.setProperty(HOSTNAME, "http://10.74.5.8");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.replayAll();
        String[] msbInfo = getServiceAddrInfo();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("10.74.5.8"));
        assertThat(msbInfo[1], equalTo("80"));

        System.clearProperty(MSB_ADDR);
    }

    @Test
    public void getServiceAddrInfo_msb_not_registered_only_ip() throws Exception {
        System.setProperty(HOSTNAME, "10.74.5.8");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.replayAll();
        String[] msbInfo = getServiceAddrInfo();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("10.74.5.8"));
        assertThat(msbInfo[1], equalTo("80"));

        System.clearProperty(MSB_ADDR);
    }

    @Test
    public void getServiceAddrInfo_msb_not_registered_full_addr_https() throws Exception {
        System.setProperty(HOSTNAME, "https://10.74.5.8:5432");
        PowerMock.mockStaticPartial(MicroServiceConfig.class, "execQuery", String.class);
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("[{\"ServiceAddress\": \"127.0.0.2\", \"ServicePort\": \"8080\"}]");
        PowerMock.expectPrivate(MicroServiceConfig.class, "execQuery", EasyMock.anyObject())
                .andReturn("{}");

        PowerMock.replayAll();
        String[] msbInfo = getServiceAddrInfo();
        PowerMock.verifyAll();

        assertThat(msbInfo[0], equalTo("10.74.5.8"));
        assertThat(msbInfo[1], equalTo("5432"));

        System.clearProperty(MSB_ADDR);
    }
}