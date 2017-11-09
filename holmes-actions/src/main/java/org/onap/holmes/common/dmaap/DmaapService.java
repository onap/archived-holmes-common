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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.aai.AaiQuery;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
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

    @Inject
    private AaiQuery aaiQuery;
    public static ConcurrentHashMap<String, String> loopControlNames = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> alarmUniqueRequestID = new ConcurrentHashMap<>();

    public void publishPolicyMsg(PolicyMsg policyMsg, String dmaapConfigKey) {
        try {
            Publisher publisher = new Publisher();
            publisher.setUrl(DcaeConfigurationsCache.getPubSecInfo(dmaapConfigKey).getDmaapInfo()
                    .getTopicUrl());
            publisher.publish(policyMsg);
            log.info("send policyMsg: " + JacksonUtil.beanToJson(policyMsg));
        } catch (CorrelationException e) {
            log.error("Failed to publish the control loop event to DMaaP", e);
        } catch (JsonProcessingException e) {
            log.info("Failed to convert the control loop event to json");
        } catch (NullPointerException e) {
            log.error("DMaaP configurations do not exist!");
        }
    }

    public PolicyMsg getPolicyMsg(VesAlarm rootAlarm, VesAlarm childAlarm, String packgeName) {
        return Optional.ofNullable(getVmEntity(rootAlarm.getSourceId(), rootAlarm.getSourceName()))
                .map(vmEntity -> getEnrichedPolicyMsg(vmEntity, rootAlarm, childAlarm, packgeName))
                .orElse(getDefaultPolicyMsg(rootAlarm.getSourceName()));
    }

    private PolicyMsg getEnrichedPolicyMsg(VmEntity vmEntity, VesAlarm rootAlarm, VesAlarm childAlarm,
            String packageName) {
        PolicyMsg policyMsg = new PolicyMsg();
        policyMsg.setRequestID(getUniqueRequestId(rootAlarm, policyMsg));
        if (rootAlarm.getAlarmIsCleared() == PolicyMassgeConstant.POLICY_MESSAGE_ONSET) {
            enrichVnfInfo(vmEntity, childAlarm, policyMsg);
            policyMsg.setClosedLoopEventStatus(EVENT_STATUS.ONSET);
            policyMsg.getAai().put("vserver.in-maint", String.valueOf(vmEntity.getInMaint()));
            policyMsg.getAai().put("vserver.is-closed-loop-disabled",
                    String.valueOf(vmEntity.getClosedLoopDisable()));
            policyMsg.getAai().put("vserver.prov-status", vmEntity.getProvStatus());
            policyMsg.getAai().put("vserver.resource-version", vmEntity.getResourceVersion());
        } else {
            policyMsg.setClosedLoopAlarmEnd(rootAlarm.getLastEpochMicrosec());
            policyMsg.setClosedLoopEventStatus(EVENT_STATUS.ABATED);
        }
        policyMsg.setClosedLoopControlName(loopControlNames.get(packageName));
        policyMsg.setClosedLoopAlarmStart(rootAlarm.getStartEpochMicrosec());
        policyMsg.getAai().put("vserver.vserver-id", vmEntity.getVserverId());
        policyMsg.getAai().put("vserver.vserver-name", vmEntity.getVserverName());
        policyMsg.getAai().put("vserver.vserver-name2", vmEntity.getVserverName2());
        policyMsg.getAai().put("vserver.vserver-selflink", vmEntity.getVserverSelflink());
        policyMsg.setTarget("vserver.vserver-name");
        return policyMsg;
    }

    private PolicyMsg getDefaultPolicyMsg(String sourceName) {
        PolicyMsg policyMsg = new PolicyMsg();
        policyMsg.setTarget("vserver.vserver-id");
        policyMsg.setTargetType("VM");
        policyMsg.getAai().put("vserver.vserver-name", sourceName);
        return policyMsg;
    }

    private String getUniqueRequestId(VesAlarm rootAlarm, PolicyMsg policyMsg) {
        String alarmUniqueKey = "";
        if (rootAlarm.getAlarmIsCleared() == PolicyMassgeConstant.POLICY_MESSAGE_ABATED) {
            alarmUniqueKey =
                    rootAlarm.getSourceId() + ":" + rootAlarm.getEventName().replace("Cleared", "");
        } else {
            alarmUniqueKey = rootAlarm.getSourceId() + ":" + rootAlarm.getEventName();
        }
        if (alarmUniqueRequestID.containsKey(alarmUniqueKey)) {
            return alarmUniqueRequestID.get(alarmUniqueKey);
        } else {
            String requestID = UUID.randomUUID().toString();
            alarmUniqueRequestID.put(alarmUniqueKey, requestID);
            return requestID;
        }
    }

    private void enrichVnfInfo(VmEntity vmEntity, VesAlarm childAlarm, PolicyMsg policyMsg) {
        String vnfId = "";
        String vnfName = "";
        if (null != childAlarm) {
            vnfId = childAlarm.getSourceId();
            vnfName = childAlarm.getSourceName();
        } else {
            Relationship relationship = vmEntity.getRelationshipList()
                    .getRelationship(PolicyMassgeConstant.GENERIC_VNF);
            if (null != relationship) {
                vnfId = relationship.getRelationshipDataValue(PolicyMassgeConstant.GENERIC_VNF_VNF_ID);
                vnfName = relationship.getRelatedToPropertyValue(PolicyMassgeConstant.GENERIC_VNF_VNF_NAME);
            }
        }
        VnfEntity vnfEntity = getVnfEntity(vnfId, vnfName);
        String vserverInstatnceId = getVserverInstanceId(vnfEntity);
        policyMsg.getAai().put("generic-vnf.vnf-id", vnfId);
        policyMsg.getAai().put("generic-vnf.service-instance-id", vserverInstatnceId);
    }


    private String getVserverInstanceId(VnfEntity vnfEntity) {
        String vserverInstanceId = "";
        if (vnfEntity != null) {
            Relationship relationship = vnfEntity.getRelationshipList()
                    .getRelationship(PolicyMassgeConstant.SERVICE_INSTANCE);
            if (relationship == null) {
                return vserverInstanceId;
            }
            vserverInstanceId = relationship
                    .getRelationshipDataValue(PolicyMassgeConstant.SERVICE_INSTANCE_ID);
        }
        return vserverInstanceId;
    }

    private VnfEntity getVnfEntity(String vnfId, String vnfName) {
        VnfEntity vnfEntity = null;
        try {
            vnfEntity = aaiQuery.getAaiVnfData(vnfId, vnfName);
        } catch (CorrelationException e) {
            log.error("Failed to get the VNF data.", e);
        }
        return vnfEntity;
    }

    private VmEntity getVmEntity(String sourceId, String sourceName) {
        VmEntity vmEntity = null;
        try {
            vmEntity = aaiQuery.getAaiVmData(sourceId, sourceName);
        } catch (CorrelationException e) {
            log.error("Failed to get the VM data.", e);
        }
        return vmEntity;
    }
}
