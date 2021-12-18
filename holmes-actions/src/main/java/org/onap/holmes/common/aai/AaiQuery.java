/**
 * Copyright 2017 - 2021 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.onap.holmes.common.aai;

import lombok.extern.slf4j.Slf4j;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.JerseyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AaiQuery {

    @Autowired
    private AaiResponseUtil aaiResponseUtil;

    public VnfEntity getAaiVnfData(String vnfId, String vnfName) throws CorrelationException {
        String response = getVnfDataResponse(vnfId, vnfName);
        try {
            return aaiResponseUtil.convertJsonToVnfEntity(response);
        } catch (Exception e) {
            throw new CorrelationException("Failed to convert aai vnf response data to vnf entity", e);
        }
    }

    public VmEntity getAaiVmData(String vserverId, String vserverName) throws CorrelationException {
        String url = getVmUrl(vserverId, vserverName);
        String response = getResponse(url);
        try {
            return aaiResponseUtil.convertJsonToVmEntity(response);
        } catch (Exception e) {
            throw new CorrelationException("Failed to convert aai vm response data to vm entity", e);
        }
    }

    private String getVmUrl(String vserverId, String vserverName) throws CorrelationException {
        String resourceLinkUrl = getVmResourceLinks(vserverId, vserverName);
        return getBaseUrl("") + resourceLinkUrl;
    }

    private String getVmResourceLinks(String vserverId, String vserverName) throws CorrelationException {
        String response = getResourceLinksResponse(vserverId, vserverName);
        List linkList = aaiResponseUtil.convertJsonToVmResourceLink(response);
        if (!linkList.isEmpty()) {
            return aaiResponseUtil.convertJsonToVmResourceLink(response).get(0).getResourceLink();
        }
        return "";
    }

    private String getResourceLinksResponse(String vserverId, String vserverName) throws CorrelationException {
        String url = getBaseUrl(AaiConfig.AaiConsts.AAI_VM_ADDR + "vserver-id:EQUALS:" + vserverId);
        String response = getResponse(url);
        if ("".equals(response) || "{}".equals(response)) {
            url = getBaseUrl(AaiConfig.AaiConsts.AAI_VM_ADDR + "vserver-name:EQUALS:" + vserverName);
            response = getResponse(url);
        }
        return response;
    }

    private String getVnfDataResponse(String vnfId, String vnfName) throws CorrelationException {
        String url = getBaseUrl(AaiConfig.AaiConsts.AAI_VNF_ADDR + "/" + vnfId);
        String response = getResponse(url);
        if ("".equals(response) || "{}".equals(response)) {
            url = getBaseUrl(AaiConfig.AaiConsts.AAI_VNF_ADDR + "?vnf-name=" + vnfName);
            response = getResponse(url);
        }
        return response;
    }

    private String getBaseUrl(String suffixUrl) {
        return "https://aai.onap:8443" + suffixUrl;
    }

    private String getResponse(String url) throws CorrelationException {
        try {
            return JerseyClient.newInstance().headers(getHeaders()).get(url);
        } catch (Exception e) {
            throw new CorrelationException("Failed to get data from aai", e);
        }
    }

    private Map getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        return headers;
    }
}
