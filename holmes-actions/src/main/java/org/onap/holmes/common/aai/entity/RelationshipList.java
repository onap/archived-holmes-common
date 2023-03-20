/**
 * Copyright 2017-2023 ZTE Corporation.
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

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RelationshipList {

    private List<Relationship> relationships;

    public Relationship getRelationship(String relatedTo) {
        Relationship relationship = null;
        if(null == relationships || relationships.isEmpty())
            return  relationship;
        for(int i = 0; i < relationships.size(); i++) {
            if (relatedTo.equals(relationships.get(i).getRelatedTo())) {
                relationship = relationships.get(i);
                break;
            }
        }
        return relationship;
    }

    @Setter
    @Getter
    public static class Relationship {
        private String relatedLink;
        private String relatedTo;
        private List<RelatedToProperty> relatedToPropertyList;
        private List<RelationshipData> relationshipDataList;

        public String getRelatedToPropertyValue(String key) {
            String value = "";
            if (null == relatedToPropertyList || relatedToPropertyList.isEmpty()) {
                return "";
            }
            for(int i = 0; i < relatedToPropertyList.size(); i++) {
                if (key.equals(relatedToPropertyList.get(i).getPropertyKey())) {
                    value = relatedToPropertyList.get(i).getPropertyValue();
                    break;
                }
            }
            return value;
        }

        public String getRelationshipDataValue(String key) {
            String value = "";
            if (null == relationshipDataList || relationshipDataList.isEmpty()) {
                return "";
            }
            for(int i = 0; i < relationshipDataList.size(); i++) {
                if (key.equals(relationshipDataList.get(i).getRelationshipKey())) {
                    value = relationshipDataList.get(i).getRelationshipValue();
                    break;
                }
            }
            return value;
        }
    }

    @Setter
    @Getter
    public static class RelationshipData {
        private String relationshipKey;
        private String relationshipValue;
    }

    @Getter
    @Setter
    public static class RelatedToProperty {
        private String propertyKey;
        private String propertyValue;
    }

}
