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

package org.openo.holmes.common.api.entity;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ServiceRegisterEntityTest {

    private ServiceRegisterEntity serviceRegisterEntity = new ServiceRegisterEntity();

    @Test
    public void getterAndSetter4protocol() {
        String protocol = "test";
        serviceRegisterEntity.setProtocol(protocol);
        assertThat(serviceRegisterEntity.getProtocol(), equalTo(protocol));
    }

    @Test
    public void getterAndSetter4serviceName() {
        String serviceName = "test";
        serviceRegisterEntity.setServiceName(serviceName);
        assertThat(serviceRegisterEntity.getServiceName(), equalTo(serviceName));
    }

    @Test
    public void getterAndSetter4url() {
        String url = "test";
        serviceRegisterEntity.setUrl(url);
        assertThat(serviceRegisterEntity.getUrl(), equalTo(url));
    }

    @Test
    public void getterAndSetter4version() {
        String version = "test";
        serviceRegisterEntity.setVersion(version);
        assertThat(serviceRegisterEntity.getVersion(), equalTo(version));
    }

    @Test
    public void getterAndSetter4visualRange() {
        String visualRange = "test";
        serviceRegisterEntity.setVisualRange(visualRange);
        assertThat(serviceRegisterEntity.getVisualRange(), equalTo(visualRange));
    }
}