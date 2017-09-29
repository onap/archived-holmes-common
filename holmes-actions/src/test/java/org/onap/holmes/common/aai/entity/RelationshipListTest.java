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

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.onap.holmes.common.aai.entity.RelationshipList.RelatedToProperty;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.RelationshipList.RelationshipData;

public class RelationshipListTest {

    private RelationshipList relationshipList;

    @Before
    public void serUp() {
        relationshipList = new RelationshipList();
    }

    @Test
    public void testRelationShip_getter_setter() {
        List<RelatedToProperty> relatedToPropertyList = new ArrayList<>();
        RelatedToProperty relatedToProperty = new RelatedToProperty();
        String propertyKey = "properKey";
        String propertyValue = "propertyValue";
        relatedToProperty.setPropertyKey(propertyKey);
        relatedToProperty.setPropertyValue(propertyValue);
        relatedToPropertyList.add(relatedToProperty);

        List<RelationshipData> relationshipDataList = new ArrayList<>();
        RelationshipData relationshipData = new RelationshipData();
        String relationshipKey = "relationshipKey";
        String relationshipValue = "relationshipValue";
        relationshipData.setRelationshipKey(relationshipKey);
        relationshipData.setRelationshipValue(relationshipValue);
        relationshipDataList.add(relationshipData);

        List<Relationship> relationships = new ArrayList<>();
        Relationship relationship = new Relationship();
        String relatedLink = "relatedLink";
        String relatedTo = "relatedTo";
        relationship.setRelatedLink(relatedLink);
        relationship.setRelatedTo(relatedTo);
        relationship.setRelatedToPropertyList(relatedToPropertyList);
        relationship.setRelationshipDataList(relationshipDataList);
        relationships.add(relationship);

        relationshipList.setRelationships(relationships);
        assertThat(1, equalTo(relationshipList.getRelationships().size()));
        assertThat(relatedLink,
                equalTo(relationshipList.getRelationships().get(0).getRelatedLink()));
        assertThat(relatedTo, equalTo(relationshipList.getRelationships().get(0).getRelatedTo()));
        assertThat(1, equalTo(relationshipList.getRelationships().get(0).getRelatedToPropertyList()
                .size()));
        assertThat(propertyKey,
                equalTo(relationshipList.getRelationships().get(0).getRelatedToPropertyList().get(0)
                        .getPropertyKey()));
        assertThat(propertyValue,
                equalTo(relationshipList.getRelationships().get(0).getRelatedToPropertyList().get(0)
                        .getPropertyValue()));
        assertThat(1, equalTo(relationshipList.getRelationships().get(0).getRelationshipDataList()
                .size()));
        assertThat(relationshipKey,
                equalTo(relationshipList.getRelationships().get(0).getRelationshipDataList().get(0)
                        .getRelationshipKey()));
        assertThat(relationshipValue,
                equalTo(relationshipList.getRelationships().get(0).getRelationshipDataList().get(0)
                        .getRelationshipValue()));
    }
}