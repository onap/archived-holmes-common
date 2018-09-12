/**
 * Copyright 2018 ZTE Corporation.
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
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AaiQuery4Ccvpn {

    private MultivaluedMap<String, Object> headers;

    static public AaiQuery4Ccvpn newInstance() {
        return new AaiQuery4Ccvpn();
    }

    private AaiQuery4Ccvpn() {
        headers = new MultivaluedHashMap<>();
        headers.add("X-TransactionId", AaiConfig.X_TRANSACTION_ID);
        headers.add("X-FromAppId", AaiConfig.X_FROMAPP_ID);
        headers.add("Authorization", AaiConfig.getAuthenticationCredentials());
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
    }

    /**
     * Query the logic link information for AAI. This method is based on the API:
     * https://<AAI host>:<AAI port>/aai/v14/network/network-resources/network-resource/{networkId}/pnfs/pnf/{pnfName}/p-interfaces?interface-name={ifName}&operational-status={status}
     * provided by AAI.
     *
     * @param networkId
     * @param pnfName
     * @param ifName
     * @param status
     * @return the ID of the logic link
     */
    public String getLogicLink(String networkId, String pnfName, String ifName, String status) throws CorrelationException {
        Map<String, String> params = new HashMap<>();
        params.put("networkId", networkId);
        params.put("pnfName", pnfName);
        params.put("ifName", ifName);

        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_LINK_QUERY, params)
                + (status == null ? "" : String.format("&operational-status=%s", status)));
        JSONObject linkInfo = getInfo(response.readEntity(String.class), "p-interface", "logical-link");
        return extractValueFromJsonArray(linkInfo.getJSONArray("relationship-data"), "logical-link.link-name");
    }

    /**
     * Query the service instances related to a terminal point. This method is mainly based on the API:
     * https://<AAI host>:<AAI port>/aai/v14/network/connectivities?connectivity-id={connectivityId}
     * and
     * https://<AAI host>:<AAI port>/aai/v14/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}
     * provided by AAI. The path for getting the required instance information is: p-interface → vpn-vpnbinding → connectivity → service instance
     *
     * @param networkId
     * @param pnfName
     * @param ifName
     * @param status
     * @return service instances in JSONObject format
     */
    public JSONObject getServiceInstance(String networkId, String pnfName, String ifName, String status) {
        try {
            JSONObject vpnBindingInfo = getVpnBindingInfo(networkId, pnfName, ifName, status);
            String vpnBindingId = extractValueFromJsonArray(vpnBindingInfo.getJSONArray("relationship-data"),
                                                            "vpn-binding.vpn-id");
            JSONObject connectivityInfo = getConnectivityInfo(vpnBindingId);
            String connectivityId = extractValueFromJsonArray(connectivityInfo.getJSONArray("relationship-data"),
                                                              "connectivity.connectivity-id");
            JSONObject serviceInstanceInfo = getServiceInstanceByConn(connectivityId);
            String serviceInstancePath = serviceInstanceInfo.getString("related-link");

            Response response = get(getHostAddr(), getPath(serviceInstancePath));
            JSONObject instance = JSON.parseObject(response.readEntity(String.class));

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
            return instance;
        } catch (CorrelationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateTerminalPointStatus(String networkId, String pnfName, String ifName,
                                          Map<String, Object> body) throws CorrelationException {
        Map<String, String> params = new HashMap<>();
        params.put("networkId", networkId);
        params.put("pnfName", pnfName);
        params.put("ifName", ifName);
        Response r = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_TP_UPDATE, params));
        JSONObject jsonObject = JSONObject.parseObject(r.readEntity(String.class));
        body.put("resource-version", jsonObject.get("resource-version").toString());

        put(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_TP_UPDATE, params), body);
    }

    public void updateLogicLinkStatus(String linkName, Map<String, Object> body) throws CorrelationException {
        Response r = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_LINK_UPDATE, "linkName", linkName));
        JSONObject jsonObject = JSONObject.parseObject(r.readEntity(String.class));
        body.put("resource-version", jsonObject.get("resource-version").toString());
        body.put("link-type", jsonObject.get("link-type").toString());
        put(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_LINK_UPDATE, "linkName", linkName), body);
    }

    private JSONObject getVpnBindingInfo(String networkId, String pnfName,
                                         String ifName, String status) throws CorrelationException {
        Map<String, String> params = new HashMap();
        params.put("networkId", networkId);
        params.put("pnfName", pnfName);
        params.put("ifName", ifName);
        params.put("status", status);
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_VPN_ADDR, params));
        return getInfo(response.readEntity(String.class), "p-interface", "vpn-binding");
    }

    private JSONObject getConnectivityInfo(String vpnId) throws CorrelationException {
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_CONN_ADDR, "vpnId", vpnId));
        return getInfo(response.readEntity(String.class), "vpn-binding", "connectivity");
    }

    private JSONObject getServiceInstanceByConn(String connectivityId) throws CorrelationException {
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_SERVICE_INSTANCE_ADDR_4_CCVPN,
                                                       "connectivityId", connectivityId));
        return getInfo(response.readEntity(String.class), "connectivity", "service-instance");
    }

    private JSONObject getServiceInstance(String globalCustomerId, String serviceType) throws CorrelationException {
        Map<String, String> params = new HashMap();
        params.put("global-customer-id", globalCustomerId);
        params.put("service-type", serviceType);
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_SERVICE_INSTANCES_ADDR_4_CCVPN, params));
        return JSON.parseObject(response.readEntity(String.class));
    }

    private String getPath(String urlTemplate, Map<String, String> pathParams) {
        String url = urlTemplate;
        for (String key : pathParams.keySet()) {
            url = url.replaceAll("\\{" + key + "\\}", pathParams.get(key));
        }
        return url;
    }

    private String getPath(String urlTemplate, String paramName, String paramValue) {
        return urlTemplate.replaceAll("\\{" + paramName + "\\}", paramValue);
    }

    private String getPath(String serviceInstancePath) {
        Pattern pattern = Pattern.compile("/aai/(v\\d+)/([A-Za-z0-9\\-]+[^/])(/*.*)");
        Matcher matcher = pattern.matcher(serviceInstancePath);
        String ret = "/api";
        if (matcher.find()) {
            ret += "/aai-" + matcher.group(2) + "/" + matcher.group(1) + matcher.group(3);
        }

        return ret;
    }

    private Response get(String host, String path) throws CorrelationException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(host).path(path);
        try {
            Response response = target.request().headers(getAaiHeaders()).get();
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                throw new CorrelationException("Failed to connect to AAI. \nCause: "
                                                       + response.getStatusInfo().getReasonPhrase() + "\nDetails: \n"
                                                       + getErrorMsg(String.format("%s%s", host, path), null, response));
            }
            return response;
        } catch (CorrelationException e) {
            throw e;
        } catch (Exception e) {
            throw new CorrelationException(e.getMessage() + "More info: "
                                                   + getErrorMsg(String.format("%s%s", host, path), null, null), e);
        }
    }

    private void put(String host, String path, Map<String, Object> body) throws CorrelationException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(host).path(path);
        try {
            Response response = target.request().headers(getAaiHeaders()).build("PUT", Entity.json(body))
                    .property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true).invoke();
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                throw new CorrelationException("Failed to connect to AAI. \nCause: "
                                                       + response.getStatusInfo().getReasonPhrase() + "\nDetails: \n"
                                                       + getErrorMsg(String.format("%s%s", host, path), body, response));
            }
        } catch (CorrelationException e) {
            throw e;
        } catch (Exception e) {
            throw new CorrelationException(e.getMessage() + "More info: "
                                                   + getErrorMsg(String.format("%s%s", host, path), body, null), e);
        }
    }

    private JSONObject getInfo(String response, String pField, String field) {
        JSONObject jObject = JSONObject.parseObject(response);
        JSONObject pInterface = extractJsonObject(jObject, pField);
        if (pInterface == null) {
            pInterface = jObject;
        }
        JSONObject relationshipList = extractJsonObject(pInterface, "relationship-list");
        JSONArray relationShip = extractJsonArray(relationshipList, "relationship");
        if (relationShip != null) {
            for (int i = 0; i < relationShip.size(); ++i) {
                final JSONObject object = relationShip.getJSONObject(i);
                if (object.getString("related-to").equals(field)) {
                    return object;
                }
            }
        }
        return null;
    }

    private JSONObject extractJsonObject(JSONObject obj, String key) {
        if (obj != null && key != null && obj.containsKey(key)) {
            return obj.getJSONObject(key);
        }
        return null;
    }

    private JSONArray extractJsonArray(JSONObject obj, String key) {
        if (obj != null && key != null && obj.containsKey(key)) {
            return obj.getJSONArray(key);
        }
        return null;
    }

    private MultivaluedMap getAaiHeaders() {
        return headers;
    }

    private String getHostAddr() {
        return MicroServiceConfig.getMsbServerAddrWithHttpPrefix();
    }

    private String extractValueFromJsonArray(JSONArray relationshipData, String keyName) {
        for (int i = 0; i < relationshipData.size(); ++i) {
            JSONObject item = relationshipData.getJSONObject(i);
            if (item.getString("relationship-key").equals(keyName)) {
                return item.getString("relationship-value");
            }
        }
        return null;
    }

    private String getErrorMsg(String url, Map<String, Object> body, Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rerquest URL: ").append(url).append("\n");
        sb.append("Request Header: ").append(JSONObject.toJSONString(headers)).append("\n");
        if (body != null) {
            sb.append("Request Body: ").append(JSONObject.toJSONString(body)).append("\n");
        }
        if (response != null) {
            sb.append("Request Body: ").append(response.readEntity(String.class));
        }
        return sb.toString();
    }
}
