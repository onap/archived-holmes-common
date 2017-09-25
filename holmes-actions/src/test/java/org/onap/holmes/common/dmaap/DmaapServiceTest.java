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
package org.onap.holmes.common.dmaap;

import static org.easymock.EasyMock.anyObject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.runner.RunWith;
import org.onap.holmes.common.aai.AaiQuery;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.RelationshipList.RelationshipData;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.api.stat.VesAlarm;
import org.onap.holmes.common.exception.CorrelationException;
import org.powermock.core.classloader.annotations.PrepareForTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@PrepareForTest(DmaapService.class)
@RunWith(PowerMockRunner.class)
public class DmaapServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Publisher publisher;

    private DmaapService dmaapService;

    private AaiQuery aaiQuery;

    @Before
    public void setUp() {
        PowerMock.replayAll();
    }

    @Test
    public void testDmaapService_publish_ok() throws Exception {
        PolicyMsg policyMsg = new PolicyMsg();
        PowerMock.mockStatic(DmaapService.class);
        publisher = PowerMock.createMock(Publisher.class);
        Whitebox.setInternalState(DmaapService.class, "publisher", publisher);
        PowerMock.expectPrivate(publisher, "publish", anyObject(PolicyMsg.class)).andReturn(true)
                .anyTimes();
        Whitebox.invokeMethod(DmaapService.class, "publishPolicyMsg", policyMsg);
    }

    @Test
    public void testDmaapService_publish_exception() throws Exception {
        PolicyMsg policyMsg = new PolicyMsg();
        PowerMock.mockStatic(DmaapService.class);
        publisher = PowerMock.createMock(Publisher.class);
        Whitebox.setInternalState(DmaapService.class, "publisher", publisher);
        PowerMock.expectPrivate(publisher, "publish", anyObject(PolicyMsg.class))
                .andThrow(new CorrelationException("")).anyTimes();
        Whitebox.invokeMethod(DmaapService.class, "publishPolicyMsg", policyMsg);
    }

    @Test
    public void testDmaapService_getDefaultPolicyMsg_ok() throws Exception {
        PolicyMsg policyMsg = Whitebox
                .invokeMethod(DmaapService.class, "getDefaultPolicyMsg", "tetss");
        PowerMock.replayAll();
        assertThat(policyMsg.getTarget(), equalTo("vserver.vserver-name"));
        assertThat(policyMsg.getTargetType(), equalTo("VM"));
        assertThat(policyMsg.getAai().get("vserver.vserver-name"), equalTo("tetss"));
    }

    @Test
    public void testDmaapService_getVnfEntity_ok() throws Exception {
        VnfEntity expect = new VnfEntity();
        expect.setVnfName("test");
        aaiQuery = PowerMock.createMock(AaiQuery.class);
        Whitebox.setInternalState(DmaapService.class, "aaiQuery", aaiQuery);
        PowerMock.expectPrivate(aaiQuery, "getAaiVnfData", anyObject(String.class),
                anyObject(String.class)).andReturn(expect).anyTimes();
        PowerMock.replayAll();
        VnfEntity actual = Whitebox
                .invokeMethod(DmaapService.class, "getVnfEntity", "tset", "test");
        assertThat(actual.getVnfName(), equalTo("test"));
    }

    @Test
    public void testDmaapService_getVnfEntity_exception() throws Exception {
        aaiQuery = PowerMock.createMock(AaiQuery.class);
        Whitebox.setInternalState(DmaapService.class, "aaiQuery", aaiQuery);
        PowerMock.expectPrivate(aaiQuery, "getAaiVnfData", anyObject(String.class),
                anyObject(String.class)).andThrow(new CorrelationException("")).anyTimes();
        PowerMock.replayAll();
       VnfEntity actual = Whitebox.invokeMethod(DmaapService.class, "getVnfEntity", "tset", "test");
        assertTrue(actual == null);
    }

    @Test
    public void testDmaapService_getVmEntity_ok() throws Exception {
        VmEntity expect = new VmEntity();
        expect.setVserverId("11111");
        aaiQuery = PowerMock.createMock(AaiQuery.class);
        Whitebox.setInternalState(DmaapService.class, "aaiQuery", aaiQuery);
        PowerMock.expectPrivate(aaiQuery, "getAaiVmData", anyObject(String.class),
                anyObject(String.class)).andReturn(expect).anyTimes();
        PowerMock.replayAll();
        VmEntity actual = Whitebox
                .invokeMethod(DmaapService.class, "getVmEntity", "tset", "test");
        assertThat(actual.getVserverId(), equalTo("11111"));
    }

    @Test
    public void testDmaapService_getVmEntity_exception() throws Exception {
        aaiQuery = PowerMock.createMock(AaiQuery.class);
        Whitebox.setInternalState(DmaapService.class, "aaiQuery", aaiQuery);
        PowerMock.expectPrivate(aaiQuery, "getAaiVmData", anyObject(String.class),
                anyObject(String.class)).andThrow(new CorrelationException("")).anyTimes();
        PowerMock.replayAll();
        VnfEntity actual = Whitebox.invokeMethod(DmaapService.class, "getVmEntity", "tset", "test");
        assertTrue(actual == null);
    }

    @Test
    public void testDmaapService_getVserverInstanceId_ok() throws Exception {
        VnfEntity vnfEntity = new VnfEntity();
        Relationship relationship = new Relationship();
        relationship.setRelatedTo("service-instance");

        List<RelationshipData> relationshipDataList = new ArrayList<>();

        RelationshipData relationshipData = new RelationshipData();
        relationshipData.setRelationshipKey("service-instance.service-instance-id");
        relationshipData.setRelationshipValue("USUCP0PCOIL0110UJZZ01");
        relationshipDataList.add(relationshipData);
        relationship.setRelationshipDataList(relationshipDataList);

        List<Relationship> relationships = new ArrayList<>();
        relationships.add(relationship);
        vnfEntity.getRelationshipList().setRelationships(relationships);

        PowerMock.replayAll();
        String actual = Whitebox.invokeMethod(DmaapService.class, "getVserverInstanceId", vnfEntity);
        assertTrue(actual.equals("USUCP0PCOIL0110UJZZ01"));
    }

    @Test
    public void testDmaapService_getVserverInstanceId_input_null() throws Exception {
        VnfEntity vnfEntity = null;

        PowerMock.replayAll();
        String actual = Whitebox.invokeMethod(DmaapService.class, "getVserverInstanceId", vnfEntity);
        assertTrue(actual.equals(""));
    }

    @Test
    public void testDmaapService_getEnrichedPolicyMsg_ok() throws Exception {
        VmEntity vmEntity = new VmEntity();
        vmEntity.setInMaint(false);
        vmEntity.setClosedLoopDisable(true);
        vmEntity.setProvStatus("prov");
        vmEntity.setResourceVersion("kkkk");
        VesAlarm vesAlarm = new VesAlarm();
        vesAlarm.setEventId("11111");
        vesAlarm.setEventName("3333");

        VnfEntity vnfEntity = new VnfEntity();

        PowerMock.createMock(DmaapService.class);
        aaiQuery = PowerMock.createMock(AaiQuery.class);
        Whitebox.setInternalState(DmaapService.class, "aaiQuery", aaiQuery);
        PowerMock.expectPrivate(DmaapService.class, "getVnfEntity", anyObject(String.class),
                anyObject(String.class)).andReturn(null).anyTimes();

        PowerMock.replayAll();
        PolicyMsg actual = Whitebox
                .invokeMethod(DmaapService.class, "getEnrichedPolicyMsg", vmEntity, vesAlarm);

        assertTrue(actual.getPolicyName().equals("vLoadBalancer"));
        assertTrue(actual.getAai().get("vserver.prov-status").equals("prov"));
        assertTrue(actual.getAai().get("vserver.vserver-name2") == null);
        assertTrue(actual.getAai().get("generic-vnf.service-instance-id").equals(""));
    }
}