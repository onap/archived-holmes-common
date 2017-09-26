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
package org.onap.holmes.common.aai;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.RelationshipList.RelationshipData;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.exception.CorrelationException;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@PrepareForTest({CorrelationUtil.class, AaiQuery.class})
@RunWith(PowerMockRunner.class)
public class CorrelationUtilTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private CorrelationUtil correlationUtil;
    private AaiQuery aaiQuery;

    @Before
    public void testCorrelationUtil() {
        correlationUtil = CorrelationUtil.getInstance();
        aaiQuery = PowerMock.createMock(AaiQuery.class);
        Whitebox.setInternalState(correlationUtil, "aaiQuery", aaiQuery);
    }

    @Test
    public void testCorrelationUtil_isTopologicallyRelated_true() throws Exception {
        PowerMock.resetAll();
        VmEntity vmEntity = new VmEntity();
        List<Relationship> relationships = new ArrayList<>();

        List<RelationshipData> relationshipDataList = new ArrayList<>();
        RelationshipData relationshipData = new RelationshipData();
        relationshipData.setRelationshipKey("vnf-id");
        relationshipData.setRelationshipValue("123");
        relationshipDataList.add(relationshipData);

        Relationship relationship = new Relationship();
        relationship.setRelationshipDataList(relationshipDataList);
        relationships.add(relationship);
        vmEntity.getRelationshipList().setRelationships(relationships);

        PowerMock.expectPrivate(aaiQuery, "getAaiVmData", "test1", "test2").andReturn(vmEntity).anyTimes();

        PowerMock.replayAll();

        boolean actual = Whitebox
                .invokeMethod(correlationUtil, "isTopologicallyRelated", "123", "test1", "test2");
        assertTrue(actual);

        PowerMock.verifyAll();
    }

    @Test
    public void testCorrelationUtil_isTopologicalRelated_false() throws Exception {
        PowerMock.resetAll();
        VmEntity vmEntity = new VmEntity();
        List<Relationship> relationships = new ArrayList<>();

        List<RelationshipData> relationshipDataList = new ArrayList<>();
        RelationshipData relationshipData = new RelationshipData();
        relationshipData.setRelationshipKey("vnf-id");
        relationshipData.setRelationshipValue("1231");
        relationshipDataList.add(relationshipData);

        Relationship relationship = new Relationship();
        relationship.setRelationshipDataList(relationshipDataList);
        relationships.add(relationship);
        vmEntity.getRelationshipList().setRelationships(relationships);

        PowerMock.expectPrivate(aaiQuery, "getAaiVmData", "test1", "test2").andReturn(vmEntity)
                .anyTimes();

        PowerMock.replayAll();

        boolean actual = Whitebox
                .invokeMethod(correlationUtil, "isTopologicallyRelated", "123", "test1", "test2");
        assertFalse(actual);
        PowerMock.verifyAll();
    }

    @Test
    public void testCorrelationUtil_isTopologicalRelated_exception_false() throws Exception {
        PowerMock.resetAll();
        PowerMock.expectPrivate(aaiQuery, "getAaiVmData", "test1", "test2")
                .andThrow(new CorrelationException("")).anyTimes();

        PowerMock.replayAll();

        boolean actual = Whitebox
                .invokeMethod(correlationUtil, "isTopologicallyRelated", "123", "test1", "test2");
        assertFalse(actual);
        PowerMock.verifyAll();
    }

}