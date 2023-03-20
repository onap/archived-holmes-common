/*-
 * ============LICENSE_START=======================================================
 * org.onap.holmes.common.aai
 * ================================================================================
 * Copyright (C) 2018-2021 Huawei, ZTE. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.holmes.common.aai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.utils.JerseyClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.onap.holmes.common.aai.AaiJsonParserUtil.*;

@Slf4j
@Service
public class AaiQuery4Ccvpn2 {

    private Map<String, Object> headers;

    static public AaiQuery4Ccvpn2 newInstance() {
        return new AaiQuery4Ccvpn2();
    }

    private AaiQuery4Ccvpn2() {
        headers = new HashMap<>();
        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
    }

    private String getSiteVNFId(String siteService) {
        String response = JerseyClient.newInstance()
                .headers(headers)
                .path(AaiConfig.AaiConsts.AAI_SITE_RESOURCES_QUERY)
                .get(MicroServiceConfig.getAaiAddr());
        JsonObject resObj = JsonParser.parseString(response).getAsJsonObject();
        JsonArray siteResources = extractJsonArray(resObj, "site-resource");
        if (siteResources != null) {
            for (int i = 0; i < siteResources.size(); i++) {
                final JsonObject object = siteResources.get(i).getAsJsonObject();
                if (siteService.equals(object.get("site-resource-name").getAsString())) {
                    JsonObject vnfInfo = getInfo(object.toString(), "generic-vnf");
                    String vnfPath = vnfInfo.get("related-link").getAsString();

                    String vnfId = null;
                    Pattern pattern = Pattern.compile("/aai/v\\d+/network/generic-vnfs/generic-vnf/(.+)");
                    Matcher matcher = pattern.matcher(vnfPath);
                    if (matcher.find()) {
                        vnfId = matcher.group(1);
                    }

                    return vnfId;
                }
            }
        }
        return null;
    }

    private JsonObject getServiceInstanceByVnfId(String vnfId) {
        String resStr = JerseyClient.newInstance()
                .headers(headers)
                .path(getPath(AaiConfig.AaiConsts.AAI_SITE_VNF_QUERY,
                "vnfId", vnfId))
                .get(MicroServiceConfig.getAaiAddr());
        return getInfo(resStr, "service-instance");
    }

    public JsonObject getSiteServiceInstance(String siteService) {
        String vnfId = getSiteVNFId(siteService);
        if (vnfId == null) {
            return null;
        }
        JsonObject serviceInstanceInfo = getServiceInstanceByVnfId(vnfId);
        String serviceInstancePath = serviceInstanceInfo.get("related-link").getAsString();
        String res = JerseyClient.newInstance()
                .headers(headers)
                .path(getPath(serviceInstancePath))
                .get(MicroServiceConfig.getAaiAddr());
        JsonObject instance = JsonParser.parseString(res).getAsJsonObject();
        String[] params = new String[2];
        Pattern pattern = Pattern.compile("/aai/v\\d+/business/customers/customer/(.+)" +
                "/service-subscriptions/service-subscription/(.+)" +
                "/service-instances/service-instance/(.+)");
        Matcher matcher = pattern.matcher(serviceInstancePath);
        if (matcher.find()) {
            params[0] = matcher.group(1);
            params[1] = matcher.group(2);
        }
        instance.addProperty("globalSubscriberId", params[0]);
        instance.addProperty("serviceType", params[1]);
        instance.addProperty("vnfId", vnfId);
        return instance;
    }
}
