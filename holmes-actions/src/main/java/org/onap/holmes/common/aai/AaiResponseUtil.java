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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.aai.entity.RelationshipList.RelatedToProperty;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.RelationshipList.RelationshipData;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VmResourceLink;
import org.onap.holmes.common.aai.entity.VnfEntity;

@Service
public class AaiResponseUtil {

    public static final String RELATIONSHIP_LIST = "relationship-list";
    public List<VmResourceLink> convertJsonToVmResourceLink(String responseJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseJson);

        List<VmResourceLink> vmResourceLinkList = new ArrayList<>();
        if (jsonNode.has("result-data")) {
            JsonNode resultData = jsonNode.get("result-data");
            vmResourceLinkList = convertResultDataList(resultData);
        }
        return vmResourceLinkList;
    }

    public VmEntity convertJsonToVmEntity(String responseJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseJson);
        if (!jsonNode.iterator().hasNext()) {
            return null;
        }
        VmEntity vmEntity = new VmEntity();
        vmEntity.setInMaint(getBooleanElementByNode(jsonNode, "in-maint"));
        vmEntity.setClosedLoopDisable(getBooleanElementByNode(jsonNode,"is-closed-loop-disabled"));
        vmEntity.setProvStatus(getTextElementByNode(jsonNode, "prov-status"));
        vmEntity.setResourceVersion(getTextElementByNode(jsonNode,"resource-version"));
        vmEntity.setVserverId(getTextElementByNode(jsonNode,"vserver-id"));
        vmEntity.setVserverName(getTextElementByNode(jsonNode,"vserver-name"));
        vmEntity.setVserverName2(getTextElementByNode(jsonNode,"vserver-name2"));
        vmEntity.setVserverSelflink(getTextElementByNode(jsonNode,"vserver-selflink"));

        if (jsonNode.has(RELATIONSHIP_LIST)) {
            JsonNode relationshipListNode = jsonNode.get(RELATIONSHIP_LIST);
            if (relationshipListNode.has("relationship")) {
                JsonNode relationshipNode = relationshipListNode.get("relationship");
                vmEntity.getRelationshipList().setRelationships(convertRelationships(relationshipNode));
            }
        }
        if (vmEntity.getRelationshipList().getRelationships() == null) {
            vmEntity.getRelationshipList().setRelationships(Collections.emptyList());
        }
        return vmEntity;
    }

    public VnfEntity convertJsonToVnfEntity(String responseJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseJson);

        if(!jsonNode.elements().hasNext())
            return null;

        VnfEntity vnfEntity = new VnfEntity();
        vnfEntity.setInMaint(getBooleanElementByNode(jsonNode, "in-maint"));
        vnfEntity.setClosedLoopDisabled(getBooleanElementByNode(jsonNode, "is-closed-loop-disabled"));
        vnfEntity.setOrchestrationStatus(getTextElementByNode(jsonNode, "orchestration-status"));
        vnfEntity.setProvStatus(getTextElementByNode(jsonNode, "prov-status"));
        vnfEntity.setResourceVersion(getTextElementByNode(jsonNode,"resource-version"));
        vnfEntity.setServiceId(getTextElementByNode(jsonNode,"service-id"));
        vnfEntity.setVnfId(getTextElementByNode(jsonNode,"vnf-id"));
        vnfEntity.setVnfName(getTextElementByNode(jsonNode,"vnf-name"));
        vnfEntity.setVnfType(getTextElementByNode(jsonNode,"vnf-type"));

        if (jsonNode.has(RELATIONSHIP_LIST)) {
            JsonNode relationshipListNode = jsonNode.get(RELATIONSHIP_LIST);
            if (relationshipListNode.has("relationship")) {
                JsonNode relationshipNode = relationshipListNode.get("relationship");
                vnfEntity.getRelationshipList().setRelationships(convertRelationships(relationshipNode));
            }
        }
        if (vnfEntity.getRelationshipList().getRelationships() == null) {
            vnfEntity.getRelationshipList().setRelationships(Collections.emptyList());
        }
        return vnfEntity;
    }

    private List<VmResourceLink> convertResultDataList(JsonNode resultData) {
        List<VmResourceLink> vmResourceLinkList = new ArrayList<>();
        resultData.forEach(node ->{
            if (node.has("resource-link") && node.has("resource-type")) {
                VmResourceLink vmResourceLink = new VmResourceLink();
                vmResourceLink.setResourceLink(getTextElementByNode(node, "resource-link"));
                vmResourceLink.setResourceType(getTextElementByNode(node, "resource-type"));
                vmResourceLinkList.add(vmResourceLink);
            }
        });
        return vmResourceLinkList;
    }

    private List<Relationship> convertRelationships(JsonNode relationshipNode) {
        List<Relationship> relationshipList = new ArrayList<>();
        relationshipNode.forEach(node ->{
            Relationship relationship = new Relationship();
            relationship.setRelatedLink(getTextElementByNode(node, "related-link"));
            relationship.setRelatedTo(getTextElementByNode(node, "related-to"));
            if (node.has("related-to-property")) {
                JsonNode relatedToPropertyNode = node.get("related-to-property");
                relationship.setRelatedToPropertyList(
                        convertRelatedToProperty(relatedToPropertyNode));
            } else {
                relationship.setRelatedToPropertyList(Collections.emptyList());
            }
            if (node.has("relationship-data")) {
                JsonNode relationshipDataNode = node.get("relationship-data");
                relationship
                        .setRelationshipDataList(convertRelationshipDate(relationshipDataNode));
            } else {
                relationship.setRelationshipDataList(Collections.emptyList());
            }
            relationshipList.add(relationship);
        });
        return relationshipList;
    }

    private List<RelationshipData> convertRelationshipDate(JsonNode relationshipDataNode) {
        List<RelationshipData> relationshipDataList = new ArrayList<>();
        relationshipDataNode.forEach(node ->{
            RelationshipData relationshipData = new RelationshipData();
            relationshipData.setRelationshipKey(
                    getTextElementByNode(node,"relationship-key"));
            relationshipData.setRelationshipValue(
                    getTextElementByNode(node,"relationship-value"));
            relationshipDataList.add(relationshipData);
        });
        return relationshipDataList;
    }

    private List<RelatedToProperty> convertRelatedToProperty(JsonNode relatedToPropertyNode) {
        List<RelatedToProperty> relatedToPropertyList = new ArrayList<>();
        relatedToPropertyNode.forEach(node ->{
            RelatedToProperty relatedToProperty = new RelatedToProperty();
            relatedToProperty
                    .setPropertyKey(getTextElementByNode(node, "property-key"));
            relatedToProperty.setPropertyValue(
                    getTextElementByNode(node, "property-value"));
            relatedToPropertyList.add(relatedToProperty);
        });
        return relatedToPropertyList;
    }

    private String getTextElementByNode(JsonNode jsonNode,String name){
        if(jsonNode.has(name)){
            return jsonNode.get(name).asText();
        }
        return null;
    }

    private Boolean getBooleanElementByNode(JsonNode jsonNode,String name){
        if(jsonNode.has(name)){
            return jsonNode.get(name).asBoolean();
        }
        return null;
    }
}
