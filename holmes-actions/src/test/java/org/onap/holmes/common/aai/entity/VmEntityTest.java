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

public class VmEntityTest {

    private VmEntity vmEntity;

    @Before
    public void setUp() {
        vmEntity = new VmEntity();
    }

    @Test
    public void testVmEntityTest_getter_setter() {
        boolean inMaint = false;
        boolean closedLoopDisabled = true;
        String provStatus = "world";
        String resourceVersion = "seldsw";
        String vserverId = "versio";
        String vserverName = "12345";
        String vserverName2 = "lllss";
        String vserverSelflink = "qwertt";
        vmEntity.setInMaint(inMaint);
        vmEntity.setClosedLoopDisable(closedLoopDisabled);
        vmEntity.setProvStatus(provStatus);
        vmEntity.setResourceVersion(resourceVersion);
        vmEntity.setVserverId(vserverId);
        vmEntity.setVserverName(vserverName);
        vmEntity.setVserverName2(vserverName2);
        vmEntity.setVserverSelflink(vserverSelflink);
        assertThat(inMaint, equalTo(vmEntity.getInMaint()));
        assertThat(closedLoopDisabled, equalTo(vmEntity.getClosedLoopDisable()));
        assertThat(provStatus, equalTo(vmEntity.getProvStatus()));
        assertThat(resourceVersion, equalTo(vmEntity.getResourceVersion()));
        assertThat(vserverId, equalTo(vmEntity.getVserverId()));
        assertThat(vserverName, equalTo(vmEntity.getVserverName()));
        assertThat(vserverName2, equalTo(vmEntity.getVserverName2()));
        assertThat(vserverSelflink, equalTo(vmEntity.getVserverSelflink()));
        assertThat(true, equalTo(vmEntity.getRelationshipList() != null));
    }
}