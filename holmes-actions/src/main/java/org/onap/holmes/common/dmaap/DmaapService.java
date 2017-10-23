/*
 * Copyright 2017 ZTE Corporation.
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
package org.onap.holmes.common.dmaap;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.aai.AaiQuery;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.RelationshipList.RelationshipData;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.api.stat.VesAlarm;
import org.onap.holmes.common.dcae.DcaeConfigurationsCache;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.onap.holmes.common.dmaap.entity.PolicyMsg.EVENT_STATUS;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.JacksonUtil;

@Slf4j
@Service
public class DmaapService {

    public static final int POLICY_MESSAGE_ABATED = 1;

    @Inject
    private AaiQuery aaiQuery;

    public static ConcurrentHashMap<String, String> loopControlNames = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, String> alarmUniqueRequestID = new ConcurrentHashMap<>();

    public void publishPolicyMsg(PolicyMsg policyMsg, String dmaapConfigKey) {
        try {
            Publisher publisher = new Publisher();
            publisher.setUrl(DcaeConfigurationsCache.getPubSecInfo(dmaapConfigKey).getDmaapInfo().getTopicUrl());
            publisher.publish(policyMsg);
            log.info("send policyMsg: " + JacksonUtil.beanToJson(policyMsg));
        } catch (CorrelationException e) {
            log.error("Failed to publish policyMsg to dmaap", e.getMessage());
        } catch (JsonProcessingException e) {
            log.info("Failed to convert policyMsg to json");
        } catch (NullPointerException e) {
            log.error("DMaaP configurations does not exist!");
        }
    }

    public PolicyMsg getPolicyMsg(VesAlarm rootAlarm, VesAlarm childAlarm, String packgeName) {
        return Optional.ofNullable(getVmEntity(rootAlarm.getSourceId(), rootAlarm.getSourceName()))
                .map(vmEntity -> getEnrichedPolicyMsg(vmEntity, rootAlarm, childAlarm, packgeName))
                .orElse(getDefaultPolicyMsg(rootAlarm.getSourceName()));
    }

    private String getVserverInstanceId(VnfEntity vnfEntity) {
        String vserverInstanceId = "";
        if (vnfEntity != null) {
            List<Relationship> relationshipList = vnfEntity.getRelationshipList().getRelationships();
            Relationship relationship = null;
            for(int i = 0; i < relationshipList.size(); i++) {
                if ("service-instance".equals(relationshipList.get(i).getRelatedTo())) {
                    relationship = relationshipList.get(i);
                    break;
                }
            }
            if (relationship != null) {
                List<RelationshipData> relationshipDataList = relationship.getRelationshipDataList();
                for(int i = 0; i < relationshipDataList.size(); i++) {
                    if ("service-instance.service-instance-id"
                            .equals(relationshipDataList.get(i).getRelationshipKey())) {
                        vserverInstanceId = relationshipDataList.get(i).getRelationshipValue();
                        break;
                    }
                }
            }
        }
        return vserverInstanceId;
    }

    private PolicyMsg getEnrichedPolicyMsg(VmEntity vmEntity, VesAlarm rootAlarm, VesAlarm childAlarm,
            String packageName) {
        PolicyMsg policyMsg = new PolicyMsg();
        if (rootAlarm.getAlarmIsCleared() == POLICY_MESSAGE_ABATED) {
            policyMsg.setClosedLoopEventStatus(EVENT_STATUS.ABATED);
        } else {
            policyMsg.setClosedLoopEventStatus(EVENT_STATUS.ONSET);
            enrichVnfInfo(childAlarm, policyMsg);
        }
        if (alarmUniqueRequestID.containsKey(rootAlarm.getEventId())) {
            policyMsg.setRequestID(alarmUniqueRequestID.get(rootAlarm.getEventId()));
        } else {
            String requestID = UUID.randomUUID().toString();
            policyMsg.setRequestID(requestID);
            alarmUniqueRequestID.put(rootAlarm.getEventId(), requestID);
        }
        policyMsg.setClosedLoopControlName(loopControlNames.get(packageName));
        policyMsg.setTarget(vmEntity.getVserverName());
        policyMsg.getAAI().put("vserver.in-maint", String.valueOf(vmEntity.getInMaint()));
        policyMsg.getAAI().put("vserver.is-closed-loop-disabled",
                String.valueOf(vmEntity.getClosedLoopDisable()));
        policyMsg.getAAI().put("vserver.prov-status", vmEntity.getProvStatus());
        policyMsg.getAAI().put("vserver.resource-version", vmEntity.getResourceVersion());
        policyMsg.getAAI().put("vserver.vserver-id", vmEntity.getVserverId());
        policyMsg.getAAI().put("vserver.vserver-name", vmEntity.getVserverName());
        policyMsg.getAAI().put("vserver.vserver-name2", vmEntity.getVserverName2());
        policyMsg.getAAI().put("vserver.vserver-selflink", vmEntity.getVserverSelflink());
        return policyMsg;
    }

    private PolicyMsg getDefaultPolicyMsg(String sourceName) {
        PolicyMsg policyMsg = new PolicyMsg();
        policyMsg.setTarget("vserver.vserver-name");
        policyMsg.setTargetType("VM");
        policyMsg.getAAI().put("vserver.vserver-name", sourceName);
        return policyMsg;
    }

    private void enrichVnfInfo(VesAlarm childAlarm, PolicyMsg policyMsg) {
        VnfEntity vnfEntity = getVnfEntity(childAlarm.getSourceId(), childAlarm.getSourceName());
        String vserverInstatnceId = getVserverInstanceId(vnfEntity);
        policyMsg.getAAI().put("generic-vnf.vnf-id", childAlarm.getSourceId());
        policyMsg.getAAI().put("generic-vnf.vnf-name", childAlarm.getSourceName());
        policyMsg.getAAI().put("generic-vnf.service-instance-id", vserverInstatnceId);
    }

    private VnfEntity getVnfEntity(String vnfId, String vnfName) {
        VnfEntity vnfEntity = null;
        try {
            vnfEntity = aaiQuery.getAaiVnfData(vnfId, vnfName);
        } catch (CorrelationException e) {
            log.error("Failed to get vnf data", e.getMessage());
        }
        return vnfEntity;
    }

    private VmEntity getVmEntity(String sourceId, String sourceName) {
        VmEntity vmEntity = null;
        try {
            vmEntity = aaiQuery.getAaiVmData(sourceId, sourceName);
        } catch (CorrelationException e) {
            log.error("Failed to get vm data", e.getMessage());
        }
        return vmEntity;
    }
}
