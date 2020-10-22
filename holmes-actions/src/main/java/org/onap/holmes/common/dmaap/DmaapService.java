/*
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
package org.onap.holmes.common.dmaap;

import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.aai.AaiQuery;
import org.onap.holmes.common.aai.entity.RelationshipList.Relationship;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.api.stat.VesAlarm;
import org.onap.holmes.common.dcae.DcaeConfigurationsCache;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.onap.holmes.common.dmaap.entity.PolicyMsg.EVENT_STATUS;
import org.onap.holmes.common.dmaap.store.ClosedLoopControlNameCache;
import org.onap.holmes.common.dmaap.store.UniqueRequestIdCache;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

@Service
public class DmaapService {

    private static final Logger log = LoggerFactory.getLogger(DmaapService.class);

    private AaiQuery aaiQuery;
    private ClosedLoopControlNameCache closedLoopControlNameCache;
    private UniqueRequestIdCache uniqueRequestIdCache;

    @Inject
    public void setAaiQuery(AaiQuery aaiQuery) {
        this.aaiQuery = aaiQuery;
    }

    @Inject
    public void setClosedLoopControlNameCache(ClosedLoopControlNameCache closedLoopControlNameCache) {
        this.closedLoopControlNameCache = closedLoopControlNameCache;
    }

    @Inject
    public void setUniqueRequestIdCache(UniqueRequestIdCache uniqueRequestIdCache) {
        this.uniqueRequestIdCache = uniqueRequestIdCache;
    }

    public void publishPolicyMsg(PolicyMsg policyMsg, String dmaapConfigKey) {
        try {
            Publisher publisher = new Publisher();
            publisher.setUrl(DcaeConfigurationsCache.getPubSecInfo(dmaapConfigKey).getDmaapInfo()
                    .getTopicUrl());
            publisher.publish(policyMsg);
            deleteRequestIdIfNecessary(policyMsg);
            log.info("send policyMsg: " + GsonUtil.beanToJson(policyMsg));
        } catch (CorrelationException e) {
            log.error("Failed to publish the control loop event to DMaaP", e);
        } catch (NullPointerException e) {
            log.error(String.format("DMaaP configurations do not exist!\n DCAE Configurations: \n %s",
                    DcaeConfigurationsCache.getDcaeConfigurations()), e);
        }
    }

    public PolicyMsg getPolicyMsg(VesAlarm rootAlarm, VesAlarm childAlarm, String packgeName) {
        return Optional.ofNullable(getVmEntity(rootAlarm.getSourceId(), rootAlarm.getSourceName()))
                .map(vmEntity -> getEnrichedPolicyMsg(vmEntity, rootAlarm, childAlarm, packgeName))
                .orElse(getDefaultPolicyMsg(rootAlarm, packgeName));
    }

    private PolicyMsg getEnrichedPolicyMsg(VmEntity vmEntity, VesAlarm rootAlarm, VesAlarm childAlarm,
            String packageName) {
        PolicyMsg policyMsg = new PolicyMsg();
        policyMsg.setRequestID(getUniqueRequestId(rootAlarm));
        if (rootAlarm.getAlarmIsCleared() == PolicyMassgeConstant.POLICY_MESSAGE_ONSET) {
            enrichVnfInfo(vmEntity, childAlarm, policyMsg);
            policyMsg.setClosedLoopEventStatus(EVENT_STATUS.ONSET);
            policyMsg.getAai().put("vserver.in-maint", vmEntity.getInMaint());
            policyMsg.getAai().put("vserver.is-closed-loop-disabled",
                        vmEntity.getClosedLoopDisable());
            policyMsg.getAai().put("vserver.prov-status", vmEntity.getProvStatus());
            policyMsg.getAai().put("vserver.resource-version", vmEntity.getResourceVersion());
        } else {
            policyMsg.setClosedLoopAlarmEnd(rootAlarm.getLastEpochMicrosec());
            policyMsg.setClosedLoopEventStatus(EVENT_STATUS.ABATED);
        }
        policyMsg.setClosedLoopControlName(closedLoopControlNameCache.get(packageName));
        policyMsg.setClosedLoopAlarmStart(rootAlarm.getStartEpochMicrosec());
        policyMsg.getAai().put("vserver.vserver-id", vmEntity.getVserverId());
        policyMsg.getAai().put("vserver.vserver-name", vmEntity.getVserverName());
        policyMsg.getAai().put("vserver.vserver-name2", vmEntity.getVserverName2());
        policyMsg.getAai().put("vserver.vserver-selflink", vmEntity.getVserverSelflink());
        policyMsg.setTarget("vserver.vserver-name");
        return policyMsg;
    }

    private PolicyMsg getDefaultPolicyMsg(VesAlarm rootAlarm, String packageName) {
        PolicyMsg policyMsg = new PolicyMsg();
        policyMsg.setRequestID(getUniqueRequestId(rootAlarm));
        policyMsg.setClosedLoopControlName(closedLoopControlNameCache.get(packageName));
        policyMsg.setClosedLoopAlarmStart(rootAlarm.getStartEpochMicrosec());
        policyMsg.setTarget("vserver.vserver-name");
        policyMsg.setTargetType("VM");
        policyMsg.getAai().put("vserver.vserver-name", rootAlarm.getSourceName());
        if (rootAlarm.getAlarmIsCleared() == PolicyMassgeConstant.POLICY_MESSAGE_ABATED) {
            policyMsg.setClosedLoopAlarmEnd(rootAlarm.getLastEpochMicrosec());
            policyMsg.setClosedLoopEventStatus(EVENT_STATUS.ABATED);
        }
        return policyMsg;
    }

    private String getUniqueRequestId(VesAlarm rootAlarm) {
        String alarmUniqueKey = "";
        if (rootAlarm.getAlarmIsCleared() == PolicyMassgeConstant.POLICY_MESSAGE_ABATED) {
            alarmUniqueKey =
                    rootAlarm.getSourceId() + ":" + rootAlarm.getEventName().replace("Cleared", "");
        } else {
            alarmUniqueKey = rootAlarm.getSourceId() + ":" + rootAlarm.getEventName();
        }
        if (uniqueRequestIdCache.containsKey(alarmUniqueKey)) {
            return uniqueRequestIdCache.get(alarmUniqueKey);
        } else {
            String requestID = UUID.randomUUID().toString();
            uniqueRequestIdCache.put(alarmUniqueKey, requestID);
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

    private void deleteRequestIdIfNecessary(PolicyMsg policyMsg){
    	EVENT_STATUS status = policyMsg.getClosedLoopEventStatus();
        if(EVENT_STATUS.ABATED.equals(status)) {
            String requestId = policyMsg.getRequestID();
            for(Entry<String, String> kv: uniqueRequestIdCache.entrySet()) {
                if(kv.getValue().equals(requestId)) {
                    uniqueRequestIdCache.remove(kv.getKey());
                    break;
                }
            }
            log.info("An alarm is cleared and the corresponding requestId is deleted successfully");
        }
    }
}
