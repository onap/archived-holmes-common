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

import java.util.ArrayList;
import java.util.List;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.omg.CORBA.Any;
import org.onap.holmes.common.aai.AaiQuery;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.RelationshipList.RelationshipData;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.api.stat.VesAlarm;
import org.onap.holmes.common.exception.CorrelationException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@PrepareForTest({DmaapService.class, Publisher.class, AaiQuery.class})
@RunWith(PowerMockRunner.class)
public class DmaapServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AaiQuery aaiQuery;

    private DmaapService dmaapService;

    @Before
    public void setUp() {
        dmaapService = new DmaapService();
        aaiQuery = PowerMock.createMock(AaiQuery.class);
        Whitebox.setInternalState(dmaapService, "aaiQuery", aaiQuery);
    }

//    @Test
//    public void testDmaapService_publish_ok() throws Exception {
//        PowerMock.resetAll();
//        PolicyMsg policyMsg = new PolicyMsg();
//        Publisher publisher = PowerMockito.mock(Publisher.class);
//        PowerMockito.whenNew(Publisher.class).withNoArguments().thenReturn(publisher);
//        PowerMockito.when(publisher.publish(anyObject(PolicyMsg.class))).thenReturn(true);
//        PowerMock.replayAll();
//        Whitebox.invokeMethod(dmaapService, "publishPolicyMsg", policyMsg);
//        PowerMock.verifyAll();
//    }
//
//    @Test
//    public void testDmaapService_publish_exception() throws Exception {
//        PowerMock.resetAll();
//        final PolicyMsg policyMsg = new PolicyMsg();
//        PowerMock.expectPrivate(publisher, "publish", policyMsg)
//                .andThrow(new CorrelationException("")).anyTimes();
//        PowerMock.replayAll();
//        Whitebox.invokeMethod(dmaapService, "publishPolicyMsg", policyMsg);
//        PowerMock.verifyAll();
//    }

    @Test
    public void testDmaapService_getDefaultPolicyMsg_ok() throws Exception {
        PowerMock.resetAll();

        PowerMock.replayAll();
        PolicyMsg policyMsg = Whitebox
                .invokeMethod(dmaapService, "getDefaultPolicyMsg", "tetss");
        PowerMock.verifyAll();

        assertThat(policyMsg.getTarget(), equalTo("vserver.vserver-name"));
        assertThat(policyMsg.getTargetType(), equalTo("VM"));
        assertThat(policyMsg.getAai().get("vserver.vserver-name"), equalTo("tetss"));
    }

    @Test
    public void testDmaapService_getVnfEntity_ok() throws Exception {
        PowerMock.resetAll();
        VnfEntity expect = new VnfEntity();
        expect.setVnfName("test");
        PowerMock.expectPrivate(aaiQuery, "getAaiVnfData", anyObject(String.class),
                anyObject(String.class)).andReturn(expect).anyTimes();
        PowerMock.replayAll();
        VnfEntity actual = Whitebox
                .invokeMethod(dmaapService, "getVnfEntity", "tset", "test");
        PowerMock.verifyAll();

        assertThat(actual.getVnfName(), equalTo("test"));
    }

    @Test
    public void testDmaapService_getVnfEntity_exception() throws Exception {
        PowerMock.resetAll();
        PowerMock.expectPrivate(aaiQuery, "getAaiVnfData", anyObject(String.class),
                anyObject(String.class)).andThrow(new CorrelationException("")).anyTimes();
        PowerMock.replayAll();
        VnfEntity actual = Whitebox.invokeMethod(dmaapService, "getVnfEntity", "tset", "test");
        PowerMock.verifyAll();

        assertThat(actual == null, equalTo(true));
    }

    @Test
    public void testDmaapService_getVmEntity_ok() throws Exception {
        PowerMock.resetAll();
        VmEntity expect = new VmEntity();
        expect.setVserverId("11111");
        PowerMock.expectPrivate(aaiQuery, "getAaiVmData", anyObject(String.class),
                anyObject(String.class)).andReturn(expect).anyTimes();
        PowerMock.replayAll();
        VmEntity actual = Whitebox
                .invokeMethod(dmaapService, "getVmEntity", "tset", "test");
        PowerMock.verifyAll();

        assertThat(actual.getVserverId(), equalTo("11111"));
    }

    @Test
    public void testDmaapService_getVmEntity_exception() throws Exception {
        PowerMock.resetAll();
        PowerMock.expectPrivate(aaiQuery, "getAaiVmData", anyObject(String.class),
                anyObject(String.class)).andThrow(new CorrelationException("")).anyTimes();
        PowerMock.replayAll();
        VnfEntity actual = Whitebox.invokeMethod(dmaapService, "getVmEntity", "tset", "test");
        PowerMock.verifyAll();

        assertThat(actual == null, equalTo(true));
    }

    @Test
    public void testDmaapService_getVserverInstanceId_ok() throws Exception {
        PowerMock.resetAll();
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
        String actual = Whitebox.invokeMethod(dmaapService, "getVserverInstanceId", vnfEntity);
        PowerMock.verifyAll();

        assertThat(actual, equalTo("USUCP0PCOIL0110UJZZ01"));
    }

    @Test
    public void testDmaapService_getVserverInstanceId_input_null() throws Exception {
        PowerMock.resetAll();
        VnfEntity vnfEntity = null;

        PowerMock.replayAll();
        String actual = Whitebox.invokeMethod(dmaapService, "getVserverInstanceId", vnfEntity);
        PowerMock.verifyAll();

        assertThat(actual, equalTo(""));
    }

    @Test
    public void testDmaapService_getEnrichedPolicyMsg_ok() throws Exception {
        PowerMock.resetAll();
        VmEntity vmEntity = new VmEntity();
        vmEntity.setInMaint(false);
        vmEntity.setClosedLoopDisable(true);
        vmEntity.setProvStatus("prov");
        vmEntity.setResourceVersion("kkkk");
        VesAlarm vesAlarm = new VesAlarm();
        vesAlarm.setEventId("11111");
        vesAlarm.setEventName("3333");
        vesAlarm.setSourceId("111");

        PowerMock.expectPrivate(dmaapService, "getVnfEntity", anyObject(String.class),
                anyObject(String.class)).andReturn(null).anyTimes();

        PowerMock.replayAll();
        PolicyMsg actual = Whitebox
                .invokeMethod(dmaapService, "getEnrichedPolicyMsg", vmEntity, vesAlarm, vesAlarm, "loopName");
        PowerMock.verifyAll();

        assertThat(actual.getClosedLoopControlName(), equalTo(null));
        assertThat(actual.getAai().get("vserver.prov-status"), equalTo("prov"));
        assertThat(actual.getAai().get("vserver.vserver-name2") == null, equalTo(true));
        assertThat(actual.getAai().get("generic-vnf.service-instance-id"), equalTo(""));
    }
}