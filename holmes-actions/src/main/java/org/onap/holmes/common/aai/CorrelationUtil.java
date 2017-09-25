/**
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.onap.holmes.common.aai;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.exception.CorrelationException;

@Slf4j
public class CorrelationUtil {

    private AaiQuery aaiQuery;

    private static class LazyHolder {
        private static final CorrelationUtil INSTANCE = new CorrelationUtil();
    }
    private CorrelationUtil (){}

    public static final CorrelationUtil getInstance() {
        return LazyHolder.INSTANCE;
    }

    public boolean isTopologicallyRelated(String childId, String rootId) {

        return Optional.ofNullable(getVmEntity(rootId)).map(vmEntity ->
                getIsRelated(childId, vmEntity)).orElse(false);
    }

    private boolean getIsRelated(String childId, VmEntity vmEntity) {
        List<Relationship> relationships = vmEntity.getRelationshipList().getRelationships();
        for (Relationship relationship : relationships) {
            boolean isRelated = relationship.getRelationshipDataList().stream().anyMatch(
                    relationshipData -> relationshipData.getRelationshipValue().equals(childId));
            if (isRelated) {
                return true;
            }
        }
        return false;
    }

    private VmEntity getVmEntity(String rootId) {
        VmEntity vmEntity = null;
        try {
            vmEntity = aaiQuery.getAaiVmData("", rootId);
        } catch (CorrelationException e) {
            log.error("Failed to get vm data", e.getMessage());
        }
        return vmEntity;
    }
}
