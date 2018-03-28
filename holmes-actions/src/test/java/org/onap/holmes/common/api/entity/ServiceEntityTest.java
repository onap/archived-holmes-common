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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ServiceEntityTest {
    private ServiceEntity serviceEntity;

    @Before
    public void before() throws Exception{
        serviceEntity = new ServiceEntity();
    }
    @After
    public void after() throws Exception{};

    @Test
    public void getterAndSetter4ServiceName() throws Exception{
        final String serviceName = "serviceName";
        serviceEntity.setServiceName(serviceName);
        assertThat(serviceEntity.getServiceName(), equalTo(serviceName));
    }

    @Test
    public void getterAndSetter4Version() throws Exception{
        final String version = "version";
        serviceEntity.setVersion(version);
        assertThat(serviceEntity.getVersion(), equalTo(version));
    }

    @Test
    public void getterAndSetter4Url() throws Exception{
        final String url = "url";
        serviceEntity.setUrl(url);
        assertThat(serviceEntity.getUrl(), equalTo(url));
    }

    @Test
    public void getterAndSetter4Protocol() throws Exception{
        final String protocol = "protocol";
        serviceEntity.setProtocol(protocol);
        assertThat(serviceEntity.getProtocol(), equalTo(protocol));
    }

    @Test
    public void getterAndSetter4VisualRange() throws Exception{
        final String visualRange = "visualRange";
        serviceEntity.setVisualRange(visualRange);
        assertThat(serviceEntity.getVisualRange(), equalTo(visualRange));
    }




    @Test
    public void getterAndSetter4Lb_policy() throws Exception{
        final String lb_policy = "lb_policy";
        serviceEntity.setLb_policy(lb_policy);
        assertThat(serviceEntity.getLb_policy(), equalTo(lb_policy));
    }

    @Test
    public void getterAndSetter4Publish_port() throws Exception{
        final String publish_port = "publish_port";
        serviceEntity.setPublish_port(publish_port);
        assertThat(serviceEntity.getPublish_port(), equalTo(publish_port));
    }

    @Test
    public void getterAndSetter4Namespace() throws Exception{
        final String namespace = "namespace";
        serviceEntity.setNamespace(namespace);
        assertThat(serviceEntity.getNamespace(), equalTo(namespace));
    }

    @Test
    public void getterAndSetter4Network_plane_type() throws Exception{
        final String network_plane_type = "network_plane_type";
        serviceEntity.setNetwork_plane_type(network_plane_type);
        assertThat(serviceEntity.getNetwork_plane_type(), equalTo(network_plane_type));
    }

    @Test
    public void getterAndSetter4Host() throws Exception{
        final String host = "host";
        serviceEntity.setHost(host);
        assertThat(serviceEntity.getHost(), equalTo(host));
    }

    @Test
    public void getterAndSetter4Path() throws Exception{
        final String path = "path";
        serviceEntity.setPath(path);
        assertThat(serviceEntity.getPath(), equalTo(path));
    }

    @Test
    public void getterAndSetterEnable_ssl() throws Exception{
        final Boolean enable_ssl = true;
        serviceEntity.setEnable_ssl(enable_ssl);
        assertThat(serviceEntity.getEnable_ssl(), equalTo(enable_ssl));
    }

    @Test
    public void getterAndSetter4Metadata() throws Exception{
        final List metadata = new ArrayList();
        metadata.add("hello");
        serviceEntity.setMetadata(metadata);
        assertThat(serviceEntity.getMetadata(), equalTo(metadata));
    }


    @Test
    public void getterAndSetter4Labels() throws Exception{
        final List lables = new ArrayList();
        lables.add("aha");
        serviceEntity.setLabels(lables);
        assertThat(serviceEntity.getLabels(), equalTo(lables));
    }

    @Test
    public void getterAndSetter4Status() throws Exception{
        final String status = "status";
        serviceEntity.setStatus(status);
        assertThat(serviceEntity.getStatus(), equalTo(status));
    }


    @Test
    public void getterAndSetter4Is_manual() throws Exception{
        final Boolean status = true;
        serviceEntity.setIs_manual(status);
        assertThat(serviceEntity.getIs_manual(), equalTo(status));
    }




}
