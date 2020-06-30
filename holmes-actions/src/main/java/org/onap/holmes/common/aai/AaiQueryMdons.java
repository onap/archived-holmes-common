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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.utils.HttpsUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static org.onap.holmes.common.aai.AaiJsonParserUtil.get;
import static org.onap.holmes.common.aai.AaiJsonParserUtil.getHostAddr;
import static org.onap.holmes.common.aai.AaiJsonParserUtil.getInfo;
import static org.onap.holmes.common.aai.AaiJsonParserUtil.getPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jvnet.hk2.annotations.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AaiQueryMdons {

	private MultivaluedMap<String, Object> headers;

	private AaiQueryMdons() {
		headers = new MultivaluedHashMap<>();
		headers.add("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
		headers.add("X-FromAppId", AaiConfig.X_FROMAPP_ID);
		headers.add("Authorization", AaiConfig.getAuthenticationCredentials());
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
	}

	public static AaiQueryMdons newInstance() {
		return new AaiQueryMdons();
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

	private Map getHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
		headers.put("X-FromAppId", AaiConfig.X_FROMAPP_ID);
		headers.put("Authorization", AaiConfig.getAuthenticationCredentials());
		headers.put("Accept", "application/json");
		return headers;
	}

	public Map<String, String> processPnf(String pnfId) throws Exception {
		log.debug("Pnf id from alarm {}", pnfId);
		Map<String, String> accessServiceMap = new HashMap<>();
		String url = MicroServiceConfig.getAaiAddr() + getPath(AaiConfig.AaiConsts.AAI_PNF_VALUE, "pnfName", pnfId);
		String pnf = getResponse(url);
		JSONObject jObject = JSONObject.parseObject(pnf);
		JSONObject pInterfaces = AaiJsonParserUtil.extractJsonObject(jObject, "p-interfaces");
		JSONArray pInterface = AaiJsonParserUtil.extractJsonArray(pInterfaces, "p-interface");
		for (int i = 0; i < pInterface.size(); i++) {
			JSONObject relationshiplist = AaiJsonParserUtil.extractJsonObject(pInterface.getJSONObject(i),
					"relationship-list");
			JSONArray relationship = AaiJsonParserUtil.extractJsonArray(relationshiplist, "relationship");
			if (relationship != null) {
				for (int j = 0; j < relationship.size(); j++) {
					JSONObject object = relationship.getJSONObject(j);
					System.out.println(object);
					if (object.getString("related-to").equals("service-instance")) {
						String domain = object.getJSONArray("relationship-data").getJSONObject(2)
								.getString("relationship-value");
						String access = getAccessServiceForDomain(domain);
						if (access != null) {
							String[] accessSplit = access.split("__");
							accessServiceMap.put(accessSplit[0], accessSplit[1]);
						}

					}

				}

			}
		}
		return accessServiceMap;
	}

	private String getServiceInstanceAai(String serviceInstanceId) throws Exception {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("global-customer-id", "Orange");
		paramMap.put("service-type", "MDONS_OTN");
		paramMap.put("instance-id", serviceInstanceId);
		String url = MicroServiceConfig.getAaiAddr() + getCompletePath(AaiConfig.AaiConsts.AAI_SERVICE, paramMap);
		return getResponse(url);
	}

	private String getAccessServiceForDomain(String serviceInstanceId) throws Exception {
		log.debug("Domain service {}", serviceInstanceId);
		String domainInstance = getServiceInstanceAai(serviceInstanceId);
		JSONObject matchedObject = getInfo(domainInstance, "service-instance");
		String accessInstanceId = matchedObject.getJSONArray("relationship-data").getJSONObject(2)
				.getString("relationship-value");
		String accessName = matchedObject.getJSONArray("related-to-property").getJSONObject(0)
				.getString("property-value");
		return accessInstanceId + "__" + accessName;
	}

	public void updateLinksForAccessService(Map<String, String> accessInstanceList) throws Exception {
		for (String access : accessInstanceList.keySet()) {
			String response = getServiceInstanceAai(access);
			JSONObject matchedObject = getInfo(response, "logical-link");
			if (matchedObject != null) {
				log.debug("Links found for the Access Service ");
				String linkName = matchedObject.getJSONArray("relationship-data").getJSONObject(0)
						.getString("relationship-value");
				updateLogicLinkStatus(linkName, "down");
			} else {
				log.debug("No links found for the Access Service ");

			}

		}

	}

	public String getPnfNameFromPnfId(String pnfId) throws Exception {
		log.debug("Retrieve the Pnf Name");
		String url = MicroServiceConfig.getAaiAddr() + getPath(AaiConfig.AaiConsts.AAI_PNF_ID, "pnfId", pnfId);
		String pnf = getResponse(url);
		JSONObject jsonObject = JSONObject.parseObject(pnf);
		JSONArray pnfList = AaiJsonParserUtil.extractJsonArray(jsonObject, "pnf");
		return pnfList.getJSONObject(0).getString("pnf-name");

	}

	public void updatePnfOperationalStatus(String pnfName, String status) throws Exception {
		String url = MicroServiceConfig.getAaiAddr() + getPath(AaiConfig.AaiConsts.AAI_PNF, "pnfName", pnfName);
		String pnf = getResponse(url);
		JSONObject jsonObject = JSONObject.parseObject(pnf);
		jsonObject.put("operational-status", status);
		put(url, jsonObject.toString());

	}

	public void updateLogicLinkStatus(String linkName, String status) throws Exception {
		String url = MicroServiceConfig.getAaiAddr()
				+ getPath(AaiConfig.AaiConsts.AAI_LINK_UPDATE, "linkName", linkName);
		String response = getResponse(url);
		JSONObject jsonObject = JSONObject.parseObject(response);
		jsonObject.put("operational-status", status);
		put(url, jsonObject.toString());
	}

	private HttpResponse put(String url, String content) throws Exception {
		CloseableHttpClient httpClient = null;
		HttpPut httpPut = new HttpPut(url);
		try {
			httpClient = HttpsUtils.getConditionalHttpsClient(HttpsUtils.DEFUALT_TIMEOUT);
			return HttpsUtils.put(httpPut, getHeaders(), new HashMap<>(), new StringEntity(content), httpClient);
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
