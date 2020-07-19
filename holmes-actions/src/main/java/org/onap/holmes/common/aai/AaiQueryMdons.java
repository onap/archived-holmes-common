/**
 * Copyright 2020 Fujitsu Limited.
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.HttpsUtils;

import static org.onap.holmes.common.aai.AaiJsonParserUtil.getInfo;
import static org.onap.holmes.common.aai.AaiJsonParserUtil.getPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AaiQueryMdons {

    private final Logger log = LoggerFactory.getLogger(AaiQueryMdons.class);
    private static final String RELATIONSHIP_VALUE = "relationship-value";
    private static final String RELATIONSHIP_LIST = "relationship-list";
    private static final String RELATIONSHIP_DATA = "relationship-data";
    private MultivaluedMap<String, Object> headers;

    public static AaiQueryMdons newInstance() {
        return new AaiQueryMdons();
    }

    private AaiQueryMdons() {
        headers = new MultivaluedHashMap<>();
        headers.add("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.add("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.add("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
    }

    private String getCompletePath(String urlTemplate, Map<String, String> pathParams) {
        String url = urlTemplate;
        for (Map.Entry<String, String> entry : pathParams.entrySet()) {
            url = url.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
        }
        return url;
    }
    
    private String getResponse(String url) throws CorrelationException {
        String response;
        CloseableHttpClient httpClient = null;
        HttpGet httpGet = new HttpGet(url);
        try {
            httpClient = HttpsUtils.getHttpsClient(HttpsUtils.DEFUALT_TIMEOUT);
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

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.put("Accept", "application/json");
        return headers;
    }

    public Map<String, String> processPnf(String pnfId) throws CorrelationException {
        Map<String, String> accessServiceMap = new HashMap<>();
        String url = MicroServiceConfig.getAaiAddr() + getPath(AaiConfig.AaiConsts.AAI_PNF_VALUE, "pnfName", pnfId);
        String pnf = getResponse(url);
        JsonObject jObject = JsonParser.parseString(pnf).getAsJsonObject();
        JsonObject pInterfaces = AaiJsonParserUtil.extractJsonObject(jObject, "p-interfaces");
        JsonArray pInterface = AaiJsonParserUtil.extractJsonArray(pInterfaces, "p-interface");
        for (int i = 0; i < pInterface.size(); i++) {
            JsonObject relationshiplist =
                    AaiJsonParserUtil.extractJsonObject(pInterface.get(i).getAsJsonObject(), RELATIONSHIP_LIST);
            JsonArray relationship = AaiJsonParserUtil.extractJsonArray(relationshiplist, "relationship");
            if (relationship != null) {
                for (int j = 0; j < relationship.size(); j++) {
                    JsonObject object = relationship.get(j).getAsJsonObject();
                    if (object.get("related-to").getAsString().equals("service-instance")) {
                        String domain = object.get(RELATIONSHIP_DATA).getAsJsonArray().get(2).getAsJsonObject()
                                .get(RELATIONSHIP_VALUE).getAsString();

                        String access = getAccessServiceForDomain(domain);
                        String[] accessSplit = access.split("__");
                        accessServiceMap.put(accessSplit[0], accessSplit[1]);

                    }

                }

            }
        }
        return accessServiceMap;
    }

    private String getServiceInstanceAai(String serviceInstanceId) throws CorrelationException {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("global-customer-id", "Orange");
        paramMap.put("service-type", "MDONS_OTN");
        paramMap.put("instance-id", serviceInstanceId);
        String url = MicroServiceConfig.getAaiAddr() + getCompletePath(AaiConfig.AaiConsts.AAI_SERVICE, paramMap);
        return getResponse(url);
    }

    private String getAccessServiceForDomain(String serviceInstanceId) throws CorrelationException {
        String domainInstance = getServiceInstanceAai(serviceInstanceId);
        JsonObject matchedObject = getInfo(domainInstance, "service-instance");
        String accessInstanceId = matchedObject.get(RELATIONSHIP_DATA).getAsJsonArray().get(2).getAsJsonObject()
                .get(RELATIONSHIP_VALUE).getAsString();
        String accessName = matchedObject.get("related-to-property").getAsJsonArray().get(0).getAsJsonObject()
                .get("property-value").getAsString();
        return accessInstanceId + "__" + accessName;
    }

    public void updateLinksForAccessService(Map<String, String> accessInstanceList) throws CorrelationException {
        for (String access : accessInstanceList.keySet()) {
            String response = getServiceInstanceAai(access);
            JsonObject matchedObject = getInfo(response, "logical-link");
            if (matchedObject != null) {
                String linkName = matchedObject.get(RELATIONSHIP_DATA).getAsJsonArray().get(0).getAsJsonObject()
                        .get(RELATIONSHIP_VALUE).getAsString();
                updateLogicLinkStatus(linkName, "down");
            }

        }

    }

    public String getPnfNameFromPnfId(String pnfId) throws CorrelationException {
        String url = MicroServiceConfig.getAaiAddr() + getPath(AaiConfig.AaiConsts.AAI_PNF_ID, "pnfId", pnfId);
        String pnf = getResponse(url);
        JsonObject jsonObject = JsonParser.parseString(pnf).getAsJsonObject();
        JsonArray pnfList = AaiJsonParserUtil.extractJsonArray(jsonObject, "pnf");
        return pnfList.get(0).getAsJsonObject().get("pnf-name").getAsString();

    }

    public void updatePnfOperationalStatus(String pnfName, String status) throws CorrelationException {
        String url = MicroServiceConfig.getAaiAddr() + getPath(AaiConfig.AaiConsts.AAI_PNF, "pnfName", pnfName);
        String pnf = getResponse(url);
        JsonObject jsonObject = JsonParser.parseString(pnf).getAsJsonObject();
        jsonObject.addProperty("operational-status", status);
        put(url, jsonObject.toString());

    }

    public void updateLogicLinkStatus(String linkName, String status) throws CorrelationException {
        String url =
                MicroServiceConfig.getAaiAddr() + getPath(AaiConfig.AaiConsts.AAI_LINK_UPDATE, "linkName", linkName);
        String response = getResponse(url);
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        jsonObject.addProperty("operational-status", status);
        put(url, jsonObject.toString());
    }

    private HttpResponse put(String url, String content) throws CorrelationException {
        CloseableHttpClient httpClient = null;
        HttpPut httpPut = new HttpPut(url);
        try {
            httpClient = HttpsUtils.getConditionalHttpsClient(HttpsUtils.DEFUALT_TIMEOUT);
            return HttpsUtils.put(httpPut, getHeaders(), new HashMap<>(), new StringEntity(content), httpClient);
        } catch (Exception e) {
            throw new CorrelationException("Failed to put data in AAI", e);
        } finally {
            closeHttpClient(httpClient);
        }
    }

    private void closeHttpClient(CloseableHttpClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.warn("Failed to close http client!");
            }
        }
    }

}
