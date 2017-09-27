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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.aai.entity.VmEntity;
import org.onap.holmes.common.aai.entity.VnfEntity;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.HttpsUtils;

@Service
@Slf4j
public class AaiQuery {

    @Inject
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
        String url = "";
        String resourceLinkUrl = getVmResourceLinks(vserverId, vserverName);
        String baseUrl = getBaseUrl("");
        if (baseUrl.startsWith("http")) {
            url = baseUrl + getMsbSuffixAddr(resourceLinkUrl);
        } else {
            url = baseUrl + resourceLinkUrl;
        }
        return url;
    }

    private String getVmResourceLinks(String vserverId, String vserverName) throws CorrelationException {
        String response = getResourceLinksResponse(vserverId, vserverName);
        try {
            return aaiResponseUtil.convertJsonToVmResourceLink(response).get(0).getResourceLink();
        } catch (Exception e) {
            throw new CorrelationException("Failed to get aai resource link", e);
        }
    }

    private String getResourceLinksResponse(String vserverId, String vserverName) throws CorrelationException {
        String url = getBaseUrl(getMsbSuffixAddr(AaiConfig.AAI_VNF_ADDR) + "vserver-id:EQUALS:" + vserverId);
        String response = getResponse(url);
        if (response.equals("")) {
            url = getBaseUrl(AaiConfig.AAI_VM_ADDR + "vserver-name:EQUALS:" + vserverName);
            response = getResponse(url);
        }
        return response;
    }

    private String getVnfDataResponse(String vnfId, String vnfName) throws CorrelationException {
        String url = getBaseUrl(getMsbSuffixAddr(AaiConfig.AAI_VM_ADDR)+  "vnf-id=" + vnfId);
        String response = getResponse(url);
        if (response.equals("")) {
            url = getBaseUrl(AaiConfig.AAI_VNF_ADDR + "vnf-name=" + vnfName);
            response = getResponse(url);
        }
        return response;
    }

    private String getBaseUrl(String suffixUrl) {
        String url = "";
        try {
            url = MicroServiceConfig.getMsbServerAddr() + suffixUrl;
        } catch (Exception e) {
            log.info("Failed to get msb address");
        }
        if (url.equals("")) {
            try {
                url = "https:\\\\" + MicroServiceConfig.getServiceAddrInfoFromCBS("aai_config")
                        + suffixUrl;
            } catch (Exception e) {
                log.info("Failed to get aai address");
            }
        }
        return url;
    }

    private String getMsbSuffixAddr(String suffixUrl) {
        String[] addrSplits = suffixUrl.substring(1).split("/");
        String ret = addrSplits[1];
        addrSplits[1] = addrSplits[2];
        addrSplits[2] = ret;
        StringBuffer stringBuffer = new StringBuffer();
        for (String split : addrSplits) {
            stringBuffer.append("/" + split);
        }
        return stringBuffer.toString();
    }

    private String getResponse(String url) throws CorrelationException {
        String response = "";
        try {
            response = HttpsUtils.get(url, getHeaders());
        } catch (Exception e) {
            throw new CorrelationException("Failed to get data from aai", e);
        }
        return response;
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
