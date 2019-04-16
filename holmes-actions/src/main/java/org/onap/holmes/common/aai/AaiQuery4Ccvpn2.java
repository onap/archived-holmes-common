/*-
 * ============LICENSE_START=======================================================
 * org.onap.holmes.common.aai
 * ================================================================================
 * Copyright (C) 2018-2019 Huawei. All rights reserved.
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.exception.CorrelationException;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.onap.holmes.common.aai.AaiJsonParserUtil.extractJsonArray;
import static org.onap.holmes.common.aai.AaiJsonParserUtil.get;
import static org.onap.holmes.common.aai.AaiJsonParserUtil.getHostAddr;
import static org.onap.holmes.common.aai.AaiJsonParserUtil.getInfo;
import static org.onap.holmes.common.aai.AaiJsonParserUtil.getPath;

@Service
@Slf4j
public class AaiQuery4Ccvpn2 {

    private MultivaluedMap<String, Object> headers;

    static public AaiQuery4Ccvpn2 newInstance() {
        return new AaiQuery4Ccvpn2();
    }

    private AaiQuery4Ccvpn2() {
        headers = new MultivaluedHashMap<>();
        headers.add("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.add("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.add("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
    }

    private String getSiteVNFId(String siteService) throws CorrelationException {
        Response response = get(getHostAddr(), AaiConfig.MsbConsts.AAI_SITE_RESOURCES_QUERY);
        String resStr = response.readEntity(String.class);
        JSONObject resObj = JSON.parseObject(resStr);
        JSONArray siteResources = extractJsonArray(resObj, "site-resource");
        if (siteResources != null) {
            for (int i = 0; i < siteResources.size(); i++) {
                final JSONObject object = siteResources.getJSONObject(i);
                if (siteService.equals(object.getString("site-resource-name"))) {
                    JSONObject vnfInfo = getInfo(object.toJSONString(), "vnf-instance");
                    String vnfPath = vnfInfo.getString("related-link");

                    String vnfId = null;
                    Pattern pattern = Pattern.compile("/aai/v\\d+/business/customers/customer/(.+)" +
                                                              "/service-subscriptions/service-subscription/(.+)" +
                                                              "/vnf-instances/vnf-instance/(.+)");
                    Matcher matcher = pattern.matcher(vnfPath);
                    if (matcher.find()) {
                        vnfId = matcher.group(3);
                    }

                    return vnfId;
                }
            }
        }
        return null;
    }

    private JSONObject getServiceInstanceByVnfId(String vnfId) throws CorrelationException {
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_SITE_VNF_QUERY,
                                                       "vnfId", vnfId));
        String resStr = response.readEntity(String.class);
        return getInfo(resStr, "service-instance");
    }

    public JSONObject getSiteServiceInstance(String siteService) throws CorrelationException {
        String vnfId = getSiteVNFId(siteService);
        if (vnfId == null) {
            return null;
        }
        JSONObject serviceInstanceInfo = getServiceInstanceByVnfId(vnfId);
        String serviceInstancePath = serviceInstanceInfo.getString("related-link");
        Response response = get(getHostAddr(), getPath(serviceInstancePath));
        String res = response.readEntity(String.class);
        JSONObject instance = JSON.parseObject(res);
        String[] params = new String[2];
        Pattern pattern = Pattern.compile("/aai/v\\d+/business/customers/customer/(.+)" +
                                                  "/service-subscriptions/service-subscription/(.+)" +
                                                  "/service-instances/service-instance/(.+)");
        Matcher matcher = pattern.matcher(serviceInstancePath);
        if (matcher.find()) {
            params[0] = matcher.group(1);
            params[1] = matcher.group(2);
        }
        instance.put("globalSubscriberId", params[0]);
        instance.put("serviceType", params[1]);
        instance.put("vnfId", vnfId);
        return instance;
    }
}
