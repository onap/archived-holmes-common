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


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ServiceNode4QueryTest {
    private ServiceNode4Query serviceNode4Query;

    @Before
    public void before()throws Exception{
        serviceNode4Query = new ServiceNode4Query();
    }
    @After
    public void after()throws Exception{}

    @Test
    public void getterAndSetter4Ip() throws Exception{
        final String ip = "ip";
        serviceNode4Query.setIp(ip);
        assertThat(serviceNode4Query.getIp(), equalTo(ip));
    }

    @Test
    public void getterAndSetter4Port() throws Exception{
        final String port = "port";
        serviceNode4Query.setPort(port);
        assertThat(serviceNode4Query.getPort(), equalTo(port));
    }

    @Test
    public void getterAndSetter4CheckType() throws Exception{
        final String checkType = "checkType";
        serviceNode4Query.setCheckType(checkType);
        assertThat(serviceNode4Query.getCheckType(), equalTo(checkType));
    }

    @Test
    public void getterAndSetter4checkUrl() throws Exception{
        final String checkUrl = "checkUrl";
        serviceNode4Query.setCheckUrl(checkUrl);
        assertThat(serviceNode4Query.getCheckUrl(), equalTo(checkUrl));
    }

    @Test
    public void getterAndSetter4hHa_role() throws Exception{
        final String ha_role = "ha_role";
        serviceNode4Query.setHa_role(ha_role);
        assertThat(serviceNode4Query.getHa_role(), equalTo(ha_role));
    }

    @Test
    public void getterAndSetter4NodeId() throws Exception{
        final String nodeId = "nodeId";
        serviceNode4Query.setNodeId(nodeId);
        assertThat(serviceNode4Query.getNodeId(), equalTo(nodeId));
    }

    @Test
    public void getterAndSetter4Status() throws Exception{
        final String status = "status";
        serviceNode4Query.setStatus(status);
        assertThat(serviceNode4Query.getStatus(), equalTo(status));
    }


}
