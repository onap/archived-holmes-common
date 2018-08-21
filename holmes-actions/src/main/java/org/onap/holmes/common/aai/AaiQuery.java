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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
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
        List linkList = aaiResponseUtil.convertJsonToVmResourceLink(response);
        if (linkList.size() != 0) {
            return aaiResponseUtil.convertJsonToVmResourceLink(response).get(0).getResourceLink();
        }
        return  "";
    }

    private String getResourceLinksResponse(String vserverId, String vserverName) throws CorrelationException {
        String url = getBaseUrl(getMsbSuffixAddr(AaiConfig.AaiConsts.AAI_VM_ADDR) + "vserver-id:EQUALS:" + vserverId);
        String response = getResponse(url);
        if ("".equals(response) || "{}".equals(response)) {
            url = getBaseUrl(getMsbSuffixAddr(AaiConfig.AaiConsts.AAI_VM_ADDR) + "vserver-name:EQUALS:" + vserverName);
            response = getResponse(url);
        }
        return response;
    }

    private String getVnfDataResponse(String vnfId, String vnfName) throws CorrelationException {
        String url = getBaseUrl(getMsbSuffixAddr(AaiConfig.AaiConsts.AAI_VNF_ADDR)+  "/" + vnfId);
        String response = getResponse(url);
        if ("".equals(response) || "{}".equals(response)) {
            url = getBaseUrl(getMsbSuffixAddr(AaiConfig.AaiConsts.AAI_VNF_ADDR) + "vnf-name=" + vnfName);
            response = getResponse(url);
        }
        return response;
    }

    private String getBaseUrl(String suffixUrl) {
        String url = "";
        try {
            String[] msbUrl = MicroServiceConfig.getMsbServerAddrWithHttpPrefix().split(":");
            url = msbUrl[0] + ":" + msbUrl[1] + suffixUrl;
        } catch (Exception e) {
            log.info("Failed to get msb address");
        }
        if ("".equals(url)) {
            try {
                url = "https://" + MicroServiceConfig.getServiceConfigInfoFromCBS("aai_config").replace("http://", "")
                        + suffixUrl;
            } catch (Exception e) {
                log.info("Failed to get the address of A&AI.", e);
            }
        }
        return url;
    }

    private String getMsbSuffixAddr(String suffixUrl) {
        if (suffixUrl.length() <= 0) {
            return "";
        }
        String[] addrSplits = suffixUrl.substring(1).split("/");
        String[] conv = addrSplits[2].split("-");
        addrSplits[2] = conv[0];
        if (conv.length > 1) {
            for(int i = 1; i < conv.length; i++) {
                addrSplits[2] = addrSplits[2] + conv[i].substring(0, 1).toUpperCase() + conv[i]
                        .substring(1);
            }
        }
        String ret = addrSplits[1];
        addrSplits[1] = addrSplits[0] + "-" + addrSplits[2];
        addrSplits[2] = ret;
        addrSplits[0] = "api";
        StringBuffer stringBuffer = new StringBuffer();
        for (String split : addrSplits) {
            stringBuffer.append("/" + split);
        }
        return stringBuffer.toString();
    }

    private String getResponse(String url) throws CorrelationException {
        String response;
        CloseableHttpClient httpClient = null;
        HttpGet httpGet = new HttpGet(url);
        try {
            httpClient = HttpsUtils.getHttpClient(HttpsUtils.DEFUALT_TIMEOUT);
            HttpResponse httpResponse = HttpsUtils.get(httpGet, getHeaders(), httpClient);
            response = HttpsUtils.extractResponseEntity(httpResponse);
        } catch (Exception e) {
            throw new CorrelationException("Failed to get data from aai", e);
        } finally {
            httpGet.releaseConnection();
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.warn("Failed to close http client!");
                }
            }
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
