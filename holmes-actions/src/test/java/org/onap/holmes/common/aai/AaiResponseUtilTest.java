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


import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VmResourceLink;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest(AaiResponseUtil.class)
public class AaiResponseUtilTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AaiResponseUtil aaiResponseUtil;

    @Before
    public void setUp() {
        aaiResponseUtil = new AaiResponseUtil();
    }

    @Test
    public void testAaiResponseUtil_convert_resouce_link_success() throws IOException {
        String json = "{"
                + "\"result-data\": ["
                + "{"
                + "\"resource-link\": \"/aai/example-vserver-id-val-2\","
                + "\"resource-type\": \"vserver\""
                + "},"
                + "{"
                + "\"resource-link\": \"/111aai/example-vserver-id-val-2\","
                + "\"resource-type\": \"111vserver\""
                + "}"
                + "]"
                + "}";

        List<VmResourceLink> actual = aaiResponseUtil.convertJsonToVmResourceLink(json);

        assertThat(actual.get(0).getResourceLink(), equalTo("/aai/example-vserver-id-val-2"));
        assertThat(actual.get(0).getResourceType(), equalTo("vserver"));
        assertThat(actual.get(1).getResourceLink(), equalTo("/111aai/example-vserver-id-val-2"));
        assertThat(actual.get(1).getResourceType(), equalTo("111vserver"));
    }

    @Test
    public void testAaiResponseUtil_convert_resource_link_input_empty_array() throws IOException {
        String json = "{"
                + "\"result-data\": ["
                + "]"
                + "}";
        List<VmResourceLink> actual = aaiResponseUtil.convertJsonToVmResourceLink(json);
        assertThat(actual.isEmpty(), equalTo(true));
    }

    @Test
    public void testAaiResponseUtil_convert_resource_link_input_empty() throws IOException {
        String json = "{}";
        List<VmResourceLink> actual = aaiResponseUtil.convertJsonToVmResourceLink(json);
        assertThat(actual.isEmpty(), equalTo(true));
    }

    @Test
    public void testAaiResponseUtil_convert_resource_link_input_error() throws IOException {
        String json = "{"
                + "\"result-data\": ["
                + "{"
                + "\"resource-link1\": \"/aai/example-vserver-id-val-2\","
                + "\"resource-type\": \"vserver\""
                + "},"
                + "{"
                + "\"resource-link\": \"/111aai/example-vserver-id-val-2\","
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
        assertThat(actual.isEmpty(), equalTo(true));
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
        VmEntity actual = aaiResponseUtil.convertJsonToVmEntity(json);
        assertThat(actual.getInMaint(), equalTo(true));
        assertThat(actual.getClosedLoopDisable(), equalTo(true));
        assertThat(actual.getProvStatus(), equalTo("example-prov-status-val-2"));
        assertThat(actual.getResourceVersion(), equalTo("1504912891060"));
        assertThat(actual.getVserverId(), equalTo("example-vserver-id-val-2"));
        assertThat(actual.getVserverName(), equalTo("example-vserver-name-val-2"));
        assertThat(actual.getVserverName2(), equalTo("example-vserver-name2-val-2"));
        assertThat(actual.getVserverSelflink(), equalTo("example-vserver-selflink-val-2"));
    }


    @Test
    public void testAaiResponseUtil_convert_VmEntity_input_empty() throws IOException {
        String json = "{}";
        VmEntity actual = aaiResponseUtil.convertJsonToVmEntity(json);
        assertThat(actual == null, equalTo(true));
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
        VmEntity actual = aaiResponseUtil.convertJsonToVmEntity(json);
        assertThat(actual.getInMaint() == null, equalTo(true));
        assertThat(actual.getClosedLoopDisable(), equalTo(true));
        assertThat(actual.getProvStatus(), equalTo("example-prov-status-val-2"));
        assertThat(actual.getResourceVersion(), equalTo("1504912891060"));
        assertThat(actual.getVserverId(), equalTo("example-vserver-id-val-2"));
        assertThat(actual.getVserverName(), equalTo("example-vserver-name-val-2"));
        assertThat(actual.getVserverName2(), equalTo("example-vserver-name2-val-2"));
        assertThat(actual.getVserverSelflink(), equalTo("example-vserver-selflink-val-2"));
    }

    @Test
    public void testAaiResponseUtil_convert_success() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"relationship-list\":{"
                + "\"relationship\":["
                + "{"
                + "\"related-link\":\"/aai/v11/e8fe\","
                + "\"related-to\":\"service-instance\","
                + "\"related-to-property\":["
                + "{"
                + "\"property-key\":\"service-i\","
                + "\"property-value\":\"vCPEInfraSI13\""
                + "}"
                + "],"
                + "\"relationship-data\":["
                + "{"
                + "\"relationship-key\":\"custome\","
                + "\"relationship-value\":\"Demonstration3\""
                + "}"
                + "]"
                + "}"
                + "]"
                + "}"
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);

        assertThat(actual.getInMaint(), equalTo(false));
        assertThat(actual.getRelationshipList().getRelationships().get(0).getRelatedLink(),
                equalTo("/aai/v11/e8fe"));
        assertThat(actual.getRelationshipList().getRelationships().get(0).getRelatedTo(),
                equalTo("service-instance"));
        assertThat(actual.getRelationshipList().getRelationships().get(0).getRelatedToPropertyList()
                .get(0).getPropertyKey(), equalTo("service-i"));
        assertThat(actual.getRelationshipList().getRelationships().get(0).getRelatedToPropertyList()
                .get(0).getPropertyValue(), equalTo("vCPEInfraSI13"));
        assertThat(actual.getRelationshipList().getRelationships().get(0).getRelationshipDataList().get(0)
                        .getRelationshipKey(), equalTo("custome"));
        assertThat(actual.getRelationshipList().getRelationships().get(0).getRelationshipDataList().get(0)
                        .getRelationshipValue(), equalTo("Demonstration3"));
    }

    @Test
    public void testAaiResponseUtil_convert_VnfEntity_input_empty() throws IOException {
        String json = "{}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertThat(actual == null, equalTo(true));
    }

    @Test
    public void testAaiResponseUtil_convert_input_not_include_relationship_list() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"vnf-type\":\"vCPEInfraService10/vCPEInfraService10 0\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertThat(actual.getRelationshipList().getRelationships().isEmpty(), equalTo(true));
    }

    @Test
    public void testAaiResponseUtil_convert_input_not_include_relationship() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"is-closed-loop-disabled\":false,"
                + "\"relationship-list\":{"
                + "},"
                + "\"service-id\":\"e8cb8968-5411-478b-906a-f28747de72cd\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertThat(actual.getRelationshipList().getRelationships().isEmpty(), equalTo(true));
    }

    @Test
    public void testAaiResponseUtil_convert_input_relationship_empty() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"relationship-list\":{"
                + "\"relationship\":["
                + "]"
                + "},"
                + "\"vnf-type\":\"vCPEInfraService10/vCPEInfraService10 0\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertThat(actual.getRelationshipList().getRelationships().isEmpty(), equalTo(true));
    }

    @Test
    public void testAaiResponseUtil_convert_input_not_include_related_to_property() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"relationship-list\":{"
                + "\"relationship\":["
                + "{"
                + "\"related-link\":\"/aai/6\","
                + "\"related-to\":\"service-instance\","
                + "\"relationship-data\":["
                + "{"
                + "\"relationship-key\":\"service-instance.service-instance-id\","
                + "\"relationship-value\":\"e8feceb6-28ae-480a-bfbc-1985ce333526\""
                + "}"
                + "]"
                + "}"
                + "]"
                + "},"
                + "\"vnf-type\":\"vCPEInfraSe0\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertThat(actual.getRelationshipList().getRelationships().get(0).getRelatedToPropertyList()
                .isEmpty(), equalTo(true));
    }

    @Test
    public void testAaiResponseUtil_convert_input_related_to_property_empty() throws IOException {
        String json = "{"
                + "\"in-maint\":false,"
                + "\"relationship-list\":{"
                + "\"relationship\":["
                + "{"
                + "\"related-link\":\"/aai/3526\","
                + "\"related-to\":\"service-instance\","
                + "\"related-to-property\":["
                + "],"
                + "\"relationship-data\":["
                + "{"
                + "\"relationship-key\":\"servicnce-id\","
                + "\"relationship-value\":\"e8feceb6-28a6\""
                + "}"
                + "]"
                + "}"
                + "]"
                + "},"
                + "\"vnf-type\":\"vCPEce10\""
                + "}";
        VnfEntity actual = aaiResponseUtil.convertJsonToVnfEntity(json);
        assertThat(actual.getRelationshipList().getRelationships().get(0).getRelatedToPropertyList()
                .isEmpty(), equalTo(true));
    }
}