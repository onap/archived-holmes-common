/**
 * Copyright 2017-2020 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.aai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.onap.holmes.common.aai.entity.RelationshipList;
import org.onap.holmes.common.aai.entity.RelationshipList.RelatedToProperty;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.RelationshipList.RelationshipData;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VmResourceLink;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AaiResponseUtil {

    public static final String RELATIONSHIP_LIST = "relationship-list";

    public List<VmResourceLink> convertJsonToVmResourceLink(String responseJson)
            {
        List<VmResourceLink> vmResourceLinkList = new ArrayList<>();
        String resultDataKey = "result-data";
                JsonObject jsonNode = JsonParser.parseString(responseJson).getAsJsonObject();
        if (jsonNode != null && jsonNode.get(resultDataKey) != null) {
            JsonArray resultData = jsonNode.getAsJsonArray(resultDataKey);
            vmResourceLinkList = convertResultDataList(resultData);
        }
        return vmResourceLinkList;
    }

    public VmEntity convertJsonToVmEntity(String responseJson) {
        JsonObject jsonObject = JsonParser.parseString(responseJson).getAsJsonObject();
        if (jsonObject == null ||jsonObject.size() == 0) {
            return null;
        }
        VmEntity vmEntity = new VmEntity();
        vmEntity.setInMaint(getBooleanElementByNode(jsonObject, "in-maint"));
        vmEntity.setClosedLoopDisable(
                getBooleanElementByNode(jsonObject, "is-closed-loop-disabled"));
        vmEntity.setProvStatus(getTextElementByNode(jsonObject, "prov-status"));
        vmEntity.setResourceVersion(getTextElementByNode(jsonObject, "resource-version"));
        vmEntity.setVserverId(getTextElementByNode(jsonObject, "vserver-id"));
        vmEntity.setVserverName(getTextElementByNode(jsonObject, "vserver-name"));
        vmEntity.setVserverName2(getTextElementByNode(jsonObject, "vserver-name2"));
        vmEntity.setVserverSelflink(getTextElementByNode(jsonObject, "vserver-selflink"));

        setRelationShips(jsonObject, vmEntity.getRelationshipList());
        if (vmEntity.getRelationshipList().getRelationships() == null) {
            vmEntity.getRelationshipList().setRelationships(Collections.emptyList());
        }
        return vmEntity;
    }

    public VnfEntity convertJsonToVnfEntity(String responseJson) {
        JsonObject jsonObject = JsonParser.parseString(responseJson).getAsJsonObject();

        if (jsonObject.size() == 0) {
            return null;
        }

        VnfEntity vnfEntity = new VnfEntity();
        vnfEntity.setInMaint(getBooleanElementByNode(jsonObject, "in-maint"));
        vnfEntity.setClosedLoopDisabled(
                getBooleanElementByNode(jsonObject, "is-closed-loop-disabled"));
        vnfEntity.setOrchestrationStatus(getTextElementByNode(jsonObject, "orchestration-status"));
        vnfEntity.setProvStatus(getTextElementByNode(jsonObject, "prov-status"));
        vnfEntity.setResourceVersion(getTextElementByNode(jsonObject, "resource-version"));
        vnfEntity.setServiceId(getTextElementByNode(jsonObject, "service-id"));
        vnfEntity.setVnfId(getTextElementByNode(jsonObject, "vnf-id"));
        vnfEntity.setVnfName(getTextElementByNode(jsonObject, "vnf-name"));
        vnfEntity.setVnfType(getTextElementByNode(jsonObject, "vnf-type"));

        setRelationShips(jsonObject, vnfEntity.getRelationshipList());
        if (vnfEntity.getRelationshipList().getRelationships() == null) {
            vnfEntity.getRelationshipList().setRelationships(Collections.emptyList());
        }
        return vnfEntity;
    }

    private void setRelationShips(JsonObject jsonObject, RelationshipList relationshipList) {
        if (jsonObject.get(RELATIONSHIP_LIST) != null) {
            JsonObject relationshipListNode = jsonObject.getAsJsonObject(RELATIONSHIP_LIST);
            String relationship = "relationship";
            if (relationshipListNode.get(relationship) != null) {
                JsonArray relationshipNode = relationshipListNode.getAsJsonArray(relationship);
                relationshipList
                        .setRelationships(convertRelationships(relationshipNode));
            }
        }
    }

    private List<VmResourceLink> convertResultDataList(JsonArray resultData) {
        List<VmResourceLink> vmResourceLinkList = new ArrayList<>();
        String resourceLink = "resource-link";
        String resourceType = "resource-type";
        for (int i = 0; i < resultData.size(); i++) {
            JsonObject jsonObject = resultData.get(i).getAsJsonObject();
            if (jsonObject.get(resourceLink) != null
                    && jsonObject.get(resourceType) != null) {
                VmResourceLink vmResourceLink = new VmResourceLink();
                vmResourceLink.setResourceLink(getTextElementByNode(jsonObject, resourceLink));
                vmResourceLink.setResourceType(getTextElementByNode(jsonObject, resourceType));
                vmResourceLinkList.add(vmResourceLink);
            }
        }
        return vmResourceLinkList;
    }

    private List<Relationship> convertRelationships(JsonArray relationshipNode) {
        List<Relationship> relationshipList = new ArrayList<>();
        for (int i = 0; i < relationshipNode.size(); i++) {
            Relationship relationship = new Relationship();
            JsonObject jsonObject = relationshipNode.get(i).getAsJsonObject();

            relationship.setRelatedLink(getTextElementByNode(jsonObject, "related-link"));
            relationship.setRelatedTo(getTextElementByNode(jsonObject, "related-to"));
            if (jsonObject.get("related-to-property") != null) {
                JsonArray relatedToPropertyNode = jsonObject.getAsJsonArray("related-to-property");
                relationship.setRelatedToPropertyList(
                        convertRelatedToProperty(relatedToPropertyNode));
            } else {
                relationship.setRelatedToPropertyList(Collections.emptyList());
            }
            if (jsonObject.get("relationship-data") != null) {
                JsonArray relationshipDataNode = jsonObject.getAsJsonArray("relationship-data");
                relationship
                        .setRelationshipDataList(convertRelationshipDate(relationshipDataNode));
            } else {
                relationship.setRelationshipDataList(Collections.emptyList());
            }
            relationshipList.add(relationship);
        }

        return relationshipList;
    }

    private List<RelationshipData> convertRelationshipDate(JsonArray relationshipDataNode) {
        List<RelationshipData> relationshipDataList = new ArrayList<>();
        for (int i = 0; i < relationshipDataNode.size(); i++) {
            JsonObject jsonObject = relationshipDataNode.get(i).getAsJsonObject();
            RelationshipData relationshipData = new RelationshipData();
            relationshipData.setRelationshipKey(
                    getTextElementByNode(jsonObject, "relationship-key"));
            relationshipData.setRelationshipValue(
                    getTextElementByNode(jsonObject, "relationship-value"));
            relationshipDataList.add(relationshipData);
            relationshipDataList.add(relationshipData);

        }
        return relationshipDataList;
    }

    private List<RelatedToProperty> convertRelatedToProperty(JsonArray relatedToPropertyNode) {
        List<RelatedToProperty> relatedToPropertyList = new ArrayList<>();
        for (int i = 0; i < relatedToPropertyNode.size(); i++) {
            JsonObject jsonObject = relatedToPropertyNode.get(i).getAsJsonObject();
            RelatedToProperty relatedToProperty = new RelatedToProperty();
            relatedToProperty
                    .setPropertyKey(getTextElementByNode(jsonObject, "property-key"));
            relatedToProperty.setPropertyValue(
                    getTextElementByNode(jsonObject, "property-value"));
            relatedToPropertyList.add(relatedToProperty);
        }
        return relatedToPropertyList;
    }

    private String getTextElementByNode(JsonObject jsonNode, String name) {
        if (jsonNode.get(name) != null) {
            return jsonNode.get(name).getAsString();
        }
        return null;
    }

    private Boolean getBooleanElementByNode(JsonObject jsonNode, String name) {
        if (jsonNode.get(name) != null) {
            return jsonNode.get(name).getAsBoolean();
        }
        return null;
    }
}
