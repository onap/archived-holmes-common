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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.holmes.common.aai.entity.RelationshipList;
import org.onap.holmes.common.aai.entity.RelationshipList.RelatedToProperty;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.RelationshipList.RelationshipData;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VmResourceLink;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest(AaiResponseUtil.class)
public class AaiResponseUtilTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AaiResponseUtil aaiResponseUtil;

    @Before
    public void setUp() {
        aaiResponseUtil = new AaiResponseUtil();
        PowerMock.replayAll();
    }

    @Test
    public void testAaiResponseUtil_convert_resouce_link_success() throws IOException {
        String json = "{"
                + "\"result-data\": ["
                + "{"
                + "\"resource-link\": \"/aai/v11/cloud-infrastructure/cloud-regions/cloud-region/Rackspace/DFW/tenants/tenant/1031120/vservers/vserver/example-vserver-id-val-2\","
                + "\"resource-type\": \"vserver\""
                + "},"
                + "{"
                + "\"resource-link\": \"/111aai/v11/cloud-infrastructure/cloud-regions/cloud-region/Rackspace/DFW/tenants/tenant/1031120/vservers/vserver/example-vserver-id-val-2\","
                + "\"resource-type\": \"111vserver\""
                + "}"
                + "]"
                + "}";
        List<VmResourceLink> expected = new ArrayList<>();
        VmResourceLink vmResourceLink = new VmResourceLink();
        vmResourceLink.setResourceLink(
                "/aai/v11/cloud-infrastructure/cloud-regions/cloud-region/Rackspace/DFW/tenants/tenant/1031120/vservers/vserver/example-vserver-id-val-2");
        vmResourceLink.setResourceType("vserver");
        expected.add(vmResourceLink);
        VmResourceLink vmResourceLink1 = new VmResourceLink();
        vmResourceLink1.setResourceLink(
                "/111aai/v11/cloud-infrastructure/cloud-regions/cloud-region/Rackspace/DFW/tenants/tenant/1031120/vservers/vserver/example-vserver-id-val-2");
        vmResourceLink1.setResourceType("111vserver");
        expected.add(vmResourceLink1);

        List<VmResourceLink> actual = aaiResponseUtil.convertJsonToVmResourceLink(json);

        assertThat(expected.get(0).getResourceLink(), equalTo(actual.get(0).getResourceLink()));
        assertThat(expected.get(0).getResourceType(), equalTo(actual.get(0).getResourceType()));

        assertThat(expected.get(1).getResourceLink(), equalTo(actual.get(1).getResourceLink()));
        assertThat(expected.get(1).getResourceType(), equalTo(actual.get(1).getResourceType()));
    }

    @Test
    public void testAaiResponseUtil_convert_resource_link_input_empty_array() throws IOException {
        String json = "{"
                + "\"result-data\": ["
                + "]"
                + "}";
        List<VmResourceLink> actual = aaiResponseUtil.convertJsonToVmResourceLink(json);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testAaiResponseUtil_convert_resource_link_input_empty() throws IOException {
        String json = "{}";
        List<VmResourceLink> actual = aaiResponseUtil.convertJsonToVmResourceLink(json);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testAaiResponseUtil_convert_resource_link_input_error() throws IOException {
        String json = "{"
                + "\"result-data\": ["
                + "{"
                + "\"resource-link1\": \"/aai/v11/cloud-infrastructure/cloud-regions/cloud-region/Rackspace/DFW/tenants/tenant/1031120/vservers/vserver/example-vserver-id-val-2\","
                + "\"resource-type\": \"vserver\""
                + "},"
                + "{"
                + "\"resource-link\": \"/111aai/v11/cloud-infrastructure/cloud-regions/cloud-region/Rackspace/DFW/tenants/tenant/1031120/vservers/vserver/example-vserver-id-val-2\","
                + "\"resource-type\": \"111vserver\""
                + "}"
                + "]"
                + "}";
        List<VmResourceLink> actual = aaiResponseUtil.convertJsonToVmResourceLink(json);
        assertThat(actual.size(), equalTo(1));
        assertThat(actual.get(0).getResourceType(), equalTo("111vserver"));
    }

    @Test
    public void testAaiResponseUtil_convert_resource_link_input_error1() throws IOException {
        String json = "{"
                + "\"result-data1\": ["
                + "]"
                + "}";
        List<VmResourceLink> actual = aaiResponseUtil.convertJsonToVmResourceLink(json);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testAaiResponseUtil_convert_resource_link_throw_IOException() throws IOException {
        thrown.expect(IOException.class);
        String json = "{**}";
        List<VmResourceLink> actual = aaiResponseUtil.convertJsonToVmResourceLink(json);
    }

    @Test
    public void testAaiResponseUtil_convert_VmEntity_success() throws IOException {
        String json = "{"
                + "\"in-maint\": true,"
                + "\"is-closed-loop-disabled\": true,"
                + "\"prov-status\": \"example-prov-status-val-2\","
                + "\"resource-version\": \"1504912891060\","
                + "\"vserver-id\": \"example-vserver-id-val-2\","
                + "\"vserver-name\": \"example-vserver-name-val-2\","
                + "\"vserver-name2\": \"example-vserver-name2-val-2\","
                + "\"vserver-selflink\": \"example-vserver-selflink-val-2\""
                + "}";

        VmEntity expected = new VmEntity();
        expected.setInMaint(true);
        expected.setClosedLoopDisable(true);
        expected.setProvStatus("example-prov-status-val-2");
        expected.setResourceVersion("1504912891060");
        expected.setVserverId("example-vserver-id-val-2");
        expected.setVserverName("example-vserver-name-val-2");
        expected.setVserverName2("example-vserver-name2-val-2");
        expected.setVserverSelflink("example-vserver-selflink-val-2");

        VmEntity actual = aaiResponseUtil.convertJsonToVmEntity(json);

        assertTrue(actual.getInMaint());
        assertTrue(actual.getClosedLoopDisable());
        assertThat(expected.getProvStatus(), equalTo(actual.getProvStatus()));
        assertThat(expected.getResourceVersion(), equalTo(actual.getResourceVersion()));
        assertThat(expected.getVserverId(), equalTo(actual.getVserverId()));
        assertThat(expected.getVserverName(), equalTo(actual.getVserverName()));
        assertThat(expected.getVserverName2(), equalTo(actual.getVserverName2()));
        assertThat(expected.getVserverSelflink(), equalTo(actual.getVserverSelflink()));
    }

    @Test
    public void testAaiResponseUtil_convert_VmEntity_throw_IOException() throws IOException {
        thrown.expect(IOException.class);
        String json = "{**}";
        VmEntity actual = aaiResponseUtil.convertJsonToVmEntity(json);
    }

    @Test
    public void testAaiResponseUtil_convert_VmEntity_input_empty() throws IOException {
        String json = "{}";
        VmEntity actual = aaiResponseUtil.convertJsonToVmEntity(json);
        assertTrue(actual == null);
    }

    @Test
    public void testAaiResponseUtil_convert_VmEntity_input_error() throws IOException {
        String json = "{"
                + "\"in-maint1\": true,"
                + "\"is-closed-loop-disabled\": true,"
                + "\"prov-status\": \"example-prov-status-val-2\","
                + "\"resource-version\": \"1504912891060\","
                + "\"vserver-id\": \"example-vserver-id-val-2\","
                + "\"vserver-name\": \"example-vserver-name-val-2\","
                + "\"vserver-name2\": \"example-vserver-name2-val-2\","
                + "\"vserver-selflink\": \"example-vserver-selflink-val-2\""
                + "}";

        VmEntity expected = new VmEntity();
        expected.setInMaint(true);
        expected.setClosedLoopDisable(true);
        expected.setProvStatus("example-prov-status-val-2");
        expected.setResourceVersion("1504912891060");
        expected.setVserverId("example-vserver-id-val-2");
        expected.setVserverName("example-vserver-name-val-2");
        expected.setVserverName2("example-vserver-name2-val-2");
        expected.setVserverSelflink("example-vserver-selflink-val-2");

        VmEntity actual = aaiResponseUtil.convertJsonToVmEntity(json);

        assertTrue(actual.getInMaint() == null);
        assertTrue(actual.getClosedLoopDisable());
        assertThat(expected.getProvStatus(), equalTo(actual.getProvStatus()));
        assertThat(expected.getResourceVersion(), equalTo(actual.getResourceVersion()));
        assertThat(expected.getVserverId(), equalTo(actual.getVserverId()));
        assertThat(expected.getVserverName(), equalTo(actual.getVserverName()));
        assertThat(expected.getVserverName2(), equalTo(actual.getVserverName2()));
        assertThat(expected.getVserverSelflink(), equalTo(actual.getVserverSelflink()));
    }

    @Test
    public void testAaiResponseUtil_convert_success() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"is-closed-loop-disabled\":false,"
                + "\"orchestration-status\":\"Created\","
                + "\"prov-status\":\"PREPROV\","
                + "\"relationship-list\":{"
                + "\"relationship\":["
                + "{"
                + "\"related-link\":\"/aai/v11/business/customers/customer/Demonstration3/service-subscriptions/service-subscription/vCPE/service-instances/service-instance/e8feceb6-28ae-480a-bfbc-1985ce333526\","
                + "\"related-to\":\"service-instance\","
                + "\"related-to-property\":["
                + "{"
                + "\"property-key\":\"service-instance.service-instance-name\","
                + "\"property-value\":\"vCPEInfraSI13\""
                + "}"
                + "],"
                + "\"relationship-data\":["
                + "{"
                + "\"relationship-key\":\"customer.global-customer-id\","
                + "\"relationship-value\":\"Demonstration3\""
                + "},"
                + "{"
                + "\"relationship-key\":\"service-subscription.service-type\","
                + "\"relationship-value\":\"vCPE\""
                + "},"
                + "{"
                + "\"relationship-key\":\"service-instance.service-instance-id\","
                + "\"relationship-value\":\"e8feceb6-28ae-480a-bfbc-1985ce333526\""
                + "}"
                + "]"
                + "}"
                + "]"
                + "},"
                + "\"resource-version\":\"1504896046185\","
                + "\"service-id\":\"e8cb8968-5411-478b-906a-f28747de72cd\","
                + "\"vnf-id\":\"63b31229-9a3a-444f-9159-04ce2dca3be9\","
                + "\"vnf-name\":\"vCPEInfraVNF13\","
                + "\"vnf-type\":\"vCPEInfraService10/vCPEInfraService10 0\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);

        VnfEntity expected = new VnfEntity();
        expected.setInMaint(false);
        expected.setClosedLoopDisabled(false);
        expected.setOrchestrationStatus("Created");
        expected.setProvStatus("PREPROV");
        expected.setResourceVersion("1504896046185");
        expected.setServiceId("e8cb8968-5411-478b-906a-f28747de72cd");
        expected.setVnfId("63b31229-9a3a-444f-9159-04ce2dca3be9");
        expected.setVnfName("vCPEInfraVNF13");
        expected.setVnfType("vCPEInfraService10/vCPEInfraService10 0");

        List<Relationship> relationshipList = new ArrayList<>();
        Relationship relationship = new Relationship();
        relationship.setRelatedLink("/aai/v11/business/customers/customer/Demonstration3/service-subscriptions/service-subscription/vCPE/service-instances/service-instance/e8feceb6-28ae-480a-bfbc-1985ce333526");
        relationship.setRelatedTo("service-instance");

        List<RelatedToProperty> relatedToPropertyList = new ArrayList<>();
        RelatedToProperty relatedToProperty = new RelatedToProperty();
        relatedToProperty.setPropertyKey("service-instance.service-instance-name");
        relatedToProperty.setPropertyValue("vCPEInfraSI13");
        relatedToPropertyList.add(relatedToProperty);
        relationship.setRelatedToPropertyList(relatedToPropertyList);

        List<RelationshipData> relationshipDataList = new ArrayList<>();
        RelationshipData relationshipData = new RelationshipData();
        relationshipData.setRelationshipKey("customer.global-customer-id");
        relationshipData.setRelationshipValue("Demonstration3");
        relationshipDataList.add(relationshipData);
        RelationshipData relationshipData1 = new RelationshipData();
        relationshipData1.setRelationshipKey("service-subscription.service-type");
        relationshipData1.setRelationshipValue("vCPE");
        relationshipDataList.add(relationshipData1);
        RelationshipData relationshipData2 = new RelationshipData();
        relationshipData2.setRelationshipKey("service-instance.service-instance-id");
        relationshipData2.setRelationshipValue("e8feceb6-28ae-480a-bfbc-1985ce333526");
        relationshipDataList.add(relationshipData2);
        relationship.setRelationshipDataList(relationshipDataList);

        relationshipList.add(relationship);

        expected.getRelationshipList().setRelationships(relationshipList);

        assertFalse(actual.getInMaint());
        assertFalse(actual.getClosedLoopDisabled());
        assertThat(expected.getProvStatus(), equalTo(actual.getProvStatus()));
        assertThat(expected.getResourceVersion(), equalTo(actual.getResourceVersion()));
        assertThat(expected.getServiceId(), equalTo(actual.getServiceId()));
        assertThat(expected.getVnfId(), equalTo(actual.getVnfId()));
        assertThat(expected.getVnfName(), equalTo(actual.getVnfName()));
        assertThat(expected.getVnfType(), equalTo(actual.getVnfType()));
        assertThat(expected.getOrchestrationStatus(), equalTo(actual.getOrchestrationStatus()));

        assertThat(expected.getRelationshipList().getRelationships().get(0).getRelatedLink(),
                equalTo(actual.getRelationshipList().getRelationships().get(0).getRelatedLink()));
        assertThat(expected.getRelationshipList().getRelationships().get(0).getRelatedTo(),
                equalTo(actual.getRelationshipList().getRelationships().get(0).getRelatedTo()));

        assertThat(expected.getRelationshipList().getRelationships().get(0).getRelatedToPropertyList().get(0)
                        .getPropertyKey(),
                equalTo(actual.getRelationshipList().getRelationships().get(0).getRelatedToPropertyList().get(0)
                        .getPropertyKey()));
        assertThat(expected.getRelationshipList().getRelationships().get(0).getRelatedToPropertyList().get(0)
                        .getPropertyValue(),
                equalTo(actual.getRelationshipList().getRelationships().get(0).getRelatedToPropertyList().get(0)
                        .getPropertyValue()));

        assertThat(expected.getRelationshipList().getRelationships().get(0).getRelationshipDataList().get(0)
                        .getRelationshipKey(),
                equalTo(actual.getRelationshipList().getRelationships().get(0).getRelationshipDataList().get(0)
                        .getRelationshipKey()));

        assertThat(expected.getRelationshipList().getRelationships().get(0).getRelationshipDataList().get(0)
                        .getRelationshipValue(),
                equalTo(actual.getRelationshipList().getRelationships().get(0).getRelationshipDataList().get(0)
                        .getRelationshipValue()));
    }

    @Test
    public void testAaiResponseUtil_throw_IOException() throws IOException {
        thrown.expect(IOException.class);
        String json = "{**}";
        aaiResponseUtil.convertJsonToVnfEntity(json);
    }

    @Test
    public void testAaiResponseUtil_convert_VnfEntity_input_empty() throws IOException {
        String json = "{}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertTrue(actual == null);
    }

    @Test
    public void testAaiResponseUtil_convert_input_not_include_relationship_list() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"is-closed-loop-disabled\":false,"
                + "\"orchestration-status\":\"Created\","
                + "\"prov-status\":\"PREPROV\","
                + "\"resource-version\":\"1504896046185\","
                + "\"service-id\":\"e8cb8968-5411-478b-906a-f28747de72cd\","
                + "\"vnf-id\":\"63b31229-9a3a-444f-9159-04ce2dca3be9\","
                + "\"vnf-name\":\"vCPEInfraVNF13\","
                + "\"vnf-type\":\"vCPEInfraService10/vCPEInfraService10 0\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertTrue(actual.getRelationshipList().getRelationships().isEmpty());
    }

    @Test
    public void testAaiResponseUtil_convert_input_not_include_relationship() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"is-closed-loop-disabled\":false,"
                + "\"orchestration-status\":\"Created\","
                + "\"prov-status\":\"PREPROV\","
                + "\"resource-version\":\"1504896046185\","
                + "\"relationship-list\":{"
                + "},"
                + "\"service-id\":\"e8cb8968-5411-478b-906a-f28747de72cd\","
                + "\"vnf-id\":\"63b31229-9a3a-444f-9159-04ce2dca3be9\","
                + "\"vnf-name\":\"vCPEInfraVNF13\","
                + "\"vnf-type\":\"vCPEInfraService10/vCPEInfraService10 0\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertTrue(actual.getRelationshipList().getRelationships().isEmpty());
    }

    @Test
    public void testAaiResponseUtil_convert_input_relationship_empty() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"is-closed-loop-disabled\":false,"
                + "\"orchestration-status\":\"Created\","
                + "\"prov-status\":\"PREPROV\","
                + "\"resource-version\":\"1504896046185\","
                + "\"relationship-list\":{"
                + "\"relationship\":["
                + "]"
                + "},"
                + "\"service-id\":\"e8cb8968-5411-478b-906a-f28747de72cd\","
                + "\"vnf-id\":\"63b31229-9a3a-444f-9159-04ce2dca3be9\","
                + "\"vnf-name\":\"vCPEInfraVNF13\","
                + "\"vnf-type\":\"vCPEInfraService10/vCPEInfraService10 0\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertTrue(actual.getRelationshipList().getRelationships().isEmpty());
    }

    @Test
    public void testAaiResponseUtil_convert_input_not_include_related_to_property() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"is-closed-loop-disabled\":false,"
                + "\"orchestration-status\":\"Created\","
                + "\"prov-status\":\"PREPROV\","
                + "\"resource-version\":\"1504896046185\","
                + "\"relationship-list\":{"
                + "\"relationship\":["
                + "{"
                + "\"related-link\":\"/aai/v11/business/customers/customer/Demonstration3/service-subscriptions/service-subscription/vCPE/service-instances/service-instance/e8feceb6-28ae-480a-bfbc-1985ce333526\","
                + "\"related-to\":\"service-instance\","
                + "\"relationship-data\":["
                + "{"
                + "\"relationship-key\":\"customer.global-customer-id\","
                + "\"relationship-value\":\"Demonstration3\""
                + "},"
                + "{"
                + "\"relationship-key\":\"service-subscription.service-type\","
                + "\"relationship-value\":\"vCPE\""
                + "},"
                + "{"
                + "\"relationship-key\":\"service-instance.service-instance-id\","
                + "\"relationship-value\":\"e8feceb6-28ae-480a-bfbc-1985ce333526\""
                + "}"
                + "]"
                + "}"
                + "]"
                + "},"
                + "\"service-id\":\"e8cb8968-5411-478b-906a-f28747de72cd\","
                + "\"vnf-id\":\"63b31229-9a3a-444f-9159-04ce2dca3be9\","
                + "\"vnf-name\":\"vCPEInfraVNF13\","
                + "\"vnf-type\":\"vCPEInfraService10/vCPEInfraService10 0\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertTrue(actual.getRelationshipList().getRelationships().get(0).getRelatedToPropertyList().isEmpty());
    }

    @Test
    public void testAaiResponseUtil_convert_input_related_to_property_empty() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"is-closed-loop-disabled\":false,"
                + "\"orchestration-status\":\"Created\","
                + "\"prov-status\":\"PREPROV\","
                + "\"resource-version\":\"1504896046185\","
                + "\"relationship-list\":{"
                + "\"relationship\":["
                + "{"
                + "\"related-link\":\"/aai/v11/business/customers/customer/Demonstration3/service-subscriptions/service-subscription/vCPE/service-instances/service-instance/e8feceb6-28ae-480a-bfbc-1985ce333526\","
                + "\"related-to\":\"service-instance\","
                + "\"related-to-property\":["
                + "],"
                + "\"relationship-data\":["
                + "{"
                + "\"relationship-key\":\"customer.global-customer-id\","
                + "\"relationship-value\":\"Demonstration3\""
                + "},"
                + "{"
                + "\"relationship-key\":\"service-subscription.service-type\","
                + "\"relationship-value\":\"vCPE\""
                + "},"
                + "{"
                + "\"relationship-key\":\"service-instance.service-instance-id\","
                + "\"relationship-value\":\"e8feceb6-28ae-480a-bfbc-1985ce333526\""
                + "}"
                + "]"
                + "}"
                + "]"
                + "},"
                + "\"service-id\":\"e8cb8968-5411-478b-906a-f28747de72cd\","
                + "\"vnf-id\":\"63b31229-9a3a-444f-9159-04ce2dca3be9\","
                + "\"vnf-name\":\"vCPEInfraVNF13\","
                + "\"vnf-type\":\"vCPEInfraService10/vCPEInfraService10 0\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertTrue(actual.getRelationshipList().getRelationships().get(0).getRelatedToPropertyList().isEmpty());
    }
}