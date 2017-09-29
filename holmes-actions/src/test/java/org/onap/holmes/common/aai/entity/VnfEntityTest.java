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
package org.onap.holmes.common.aai.entity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class VnfEntityTest {

    private VnfEntity vnfEntity;

    @Before
    public void setUp() {
        vnfEntity = new VnfEntity();
    }

    @Test
    public void testVnfEntity_getter_setter() {
        boolean inMaint = false;
        boolean closedLoopDisabled = true;
        String orchestrationStatus = "hello";
        String provStatus = "world";
        String resourceVersion = "seldsw";
        String serviceId = "versio";
        String vnfId = "12345";
        String vnfName = "lllss";
        String vnfType = "qwertt";
        vnfEntity.setInMaint(inMaint);
        vnfEntity.setClosedLoopDisabled(closedLoopDisabled);
        vnfEntity.setOrchestrationStatus(orchestrationStatus);
        vnfEntity.setProvStatus(provStatus);
        vnfEntity.setResourceVersion(resourceVersion);
        vnfEntity.setServiceId(serviceId);
        vnfEntity.setVnfId(vnfId);
        vnfEntity.setVnfName(vnfName);
        vnfEntity.setVnfType(vnfType);
        assertThat(inMaint, equalTo(vnfEntity.getInMaint()));
        assertThat(closedLoopDisabled, equalTo(vnfEntity.getClosedLoopDisabled()));
        assertThat(orchestrationStatus, equalTo(vnfEntity.getOrchestrationStatus()));
        assertThat(provStatus, equalTo(vnfEntity.getProvStatus()));
        assertThat(resourceVersion, equalTo(vnfEntity.getResourceVersion()));
        assertThat(serviceId, equalTo(vnfEntity.getServiceId()));
        assertThat(vnfId, equalTo(vnfEntity.getVnfId()));
        assertThat(vnfName, equalTo(vnfEntity.getVnfName()));
        assertThat(vnfType, equalTo(vnfEntity.getVnfType()));
        assertThat(true, equalTo(vnfEntity.getRelationshipList() != null));
    }
}