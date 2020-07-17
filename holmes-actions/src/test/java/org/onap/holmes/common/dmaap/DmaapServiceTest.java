/**
 * Copyright 2017-2020 ZTE Corporation.
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

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.aai.AaiQuery;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.RelationshipList.RelationshipData;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.api.stat.VesAlarm;
import org.onap.holmes.common.dcae.DcaeConfigurationsCache;
import org.onap.holmes.common.dcae.utils.DcaeConfigurationParser;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.onap.holmes.common.dmaap.entity.PolicyMsg.EVENT_STATUS;
import org.onap.holmes.common.dmaap.store.ClosedLoopControlNameCache;
import org.onap.holmes.common.dmaap.store.UniqueRequestIdCache;
import org.onap.holmes.common.exception.CorrelationException;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@PrepareForTest({DmaapService.class, Publisher.class, AaiQuery.class})
@RunWith(PowerMockRunner.class)
public class DmaapServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AaiQuery aaiQuery;

    private DmaapService dmaapService;

    private ClosedLoopControlNameCache closedLoopControlNameCache = new ClosedLoopControlNameCache();

    private UniqueRequestIdCache uniqueRequestIdCache = new UniqueRequestIdCache();

    @Before
    public void setUp() {
        aaiQuery = PowerMock.createMock(AaiQuery.class);

        dmaapService = new DmaapService();
        dmaapService.setClosedLoopControlNameCache(closedLoopControlNameCache);
        dmaapService.setUniqueRequestIdCache(uniqueRequestIdCache);
        dmaapService.setAaiQuery(aaiQuery);
    }

    @Test
    public void testDmaapService_getDefaultPolicyMsg_ok() throws Exception {
        String packageName = "org.onap.holmes.rule";
        closedLoopControlNameCache.put(packageName, "Control-loop-VoLTE");
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis() + 1000000;
        VesAlarm vesAlarm = new VesAlarm();
        vesAlarm.setStartEpochMicrosec(startTime);
        vesAlarm.setLastEpochMicrosec(endTime);
        vesAlarm.setAlarmIsCleared(1);
        vesAlarm.setSourceName("test");
        vesAlarm.setSourceId("782d-4dfa-88ef");
        vesAlarm.setEventName("alarmCleared");
        PowerMock.resetAll();

        PowerMock.replayAll();
        PolicyMsg policyMsg = Whitebox
                .invokeMethod(dmaapService, "getDefaultPolicyMsg", vesAlarm, packageName);
        PowerMock.verifyAll();

        assertThat(policyMsg.getTarget(), equalTo("vserver.vserver-name"));
        assertThat(policyMsg.getTargetType(), equalTo("VM"));
        assertThat(policyMsg.getAai().get("vserver.vserver-name"), equalTo("test"));
        assertThat(policyMsg.getClosedLoopEventStatus(), is(EVENT_STATUS.ABATED));
        assertThat(policyMsg.getClosedLoopControlName(), equalTo("Control-loop-VoLTE"));
        assertThat(policyMsg.getClosedLoopAlarmStart(), is(startTime));
        assertThat(policyMsg.getClosedLoopAlarmEnd(), is(endTime));
        assertThat(policyMsg.getRequestID(), notNullValue());
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
    public void testDmaapService_getEnrichedPolicyMsg_onset() throws Exception {
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
        vesAlarm.setStartEpochMicrosec(11111L);

        PowerMock.expectPrivate(dmaapService, "getVnfEntity", anyObject(String.class),
                anyObject(String.class)).andReturn(null).anyTimes();

        PowerMock.replayAll();
        PolicyMsg actual = Whitebox
                .invokeMethod(dmaapService, "getEnrichedPolicyMsg", vmEntity, vesAlarm, vesAlarm, "loopName");
        PowerMock.verifyAll();

        assertThat(actual.getClosedLoopControlName(), nullValue());
        assertThat(actual.getAai().get("vserver.prov-status"), equalTo("prov"));
        assertThat(actual.getAai().get("vserver.vserver-name2"), nullValue());
        assertThat(actual.getAai().get("generic-vnf.service-instance-id"), equalTo(""));
    }

    @Test
    public void testDmaapService_getEnrichedPolicyMsg_abated() throws Exception {
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
        vesAlarm.setStartEpochMicrosec(11111L);
        vesAlarm.setLastEpochMicrosec(21111L);
        vesAlarm.setAlarmIsCleared(PolicyMassgeConstant.POLICY_MESSAGE_ABATED);

        PowerMock.expectPrivate(dmaapService, "getVnfEntity", anyObject(String.class),
                anyObject(String.class)).andReturn(null).anyTimes();

        PowerMock.replayAll();
        PolicyMsg actual = Whitebox
                .invokeMethod(dmaapService, "getEnrichedPolicyMsg", vmEntity, vesAlarm, vesAlarm, "loopName");
        PowerMock.verifyAll();

        assertThat(actual.getClosedLoopControlName(), nullValue());
        assertThat(actual.getAai().get("vserver.prov-status"), nullValue());
        assertThat(actual.getAai().get("vserver.vserver-name2"), nullValue());
        assertThat(actual.getAai().get("generic-vnf.service-instance-id"), nullValue());
    }

    @Test
    public void testPublishPolicyMsg_onset() throws Exception {
        PowerMock.resetAll();
        Publisher publisher = PowerMock.createPartialMock(Publisher.class, "publish", PolicyMsg.class);
        PolicyMsg policyMsg = new PolicyMsg();
        policyMsg.setClosedLoopEventStatus(EVENT_STATUS.ONSET);
        PowerMock.expectNew(Publisher.class).andReturn(publisher);
        EasyMock.expect(publisher.publish(policyMsg)).andReturn(true);

        DcaeConfigurationsCache.setDcaeConfigurations(
                DcaeConfigurationParser.parse(readConfigurationsFromFile("dcae.config.json")));

        PowerMock.replayAll();
        dmaapService.publishPolicyMsg(policyMsg, "sec_fault_unsecure");
        PowerMock.verifyAll();

    }

    @Test
    public void testPublishPolicyMsg_abated() throws Exception {
        PowerMock.resetAll();
        Publisher publisher = PowerMock.createPartialMock(Publisher.class, "publish", PolicyMsg.class);
        PolicyMsg policyMsg = new PolicyMsg();
        policyMsg.setClosedLoopEventStatus(EVENT_STATUS.ABATED);
        policyMsg.setRequestID("testRequestid");
        uniqueRequestIdCache.put("testAlarmId", "testRequestid");
        PowerMock.expectNew(Publisher.class).andReturn(publisher);
        EasyMock.expect(publisher.publish(policyMsg)).andReturn(true);

        DcaeConfigurationsCache.setDcaeConfigurations(
                DcaeConfigurationParser.parse(readConfigurationsFromFile("dcae.config.json")));

        PowerMock.replayAll();
        dmaapService.publishPolicyMsg(policyMsg, "sec_fault_unsecure");
        PowerMock.verifyAll();

    }

    private String readConfigurationsFromFile(String fileName) throws URISyntaxException, FileNotFoundException {
        URL url = DmaapServiceTest.class.getClassLoader().getResource(fileName);
        File configFile = new File(new URI(url.toString()).getPath());
        BufferedReader br = new BufferedReader(new FileReader(configFile));

        final StringBuilder sb = new StringBuilder();
        br.lines().forEach(line -> {
            sb.append(line);
        });
        try {
            br.close();
        } catch (IOException e) {
            // Do nothing
        }
        return sb.toString();
    }
}