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


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
    public String getLogicLink(String networkId, String pnfName, String ifName, String status) {
        Map<String, String> params = new HashMap<>();
        params.put("networkId", networkId);
        params.put("pnfName", pnfName);
        params.put("ifName", ifName);
        params.put("status", status);
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_LINK_QUERY, params));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new RuntimeException("Failed to connect to AAI. Cause: "
                    + response.getStatusInfo().getReasonPhrase());
        }

        JSONObject linkInfo = getInfo(JSONObject.toJSONString(response.getEntity()), "p-interface", "logical-link");
        return extractValueFromJsonArray(linkInfo.getJSONArray("relationship-data"), "logical-link.link-name");
    }

    /**
     * Query all the instances related to a terminal point. This method is mainly based on the API:
     * https://<AAI host>:<AAI port>/aai/v14/network/connectivities?connectivity-id={connectivityId}
     * and
     * https://<AAI host>:<AAI port>/aai/v14/business/customers/customer/{global-customer-id}/service-subscriptions/service-subscription/{service-type}
     * provided by AAI. The path for getting the required instance information is: p-interface → vpn-vpnbinding → connectivity → service instance
     *
     * @param networkId
     * @param pnfName
     * @param ifName
     * @param status
     * @return all related service instances in JSONArray format
     */
    public JSONArray getServiceInstances(String networkId, String pnfName, String ifName, String status) {
        try {
            JSONObject vpnBindingInfo = getVpnBindingInfo(networkId, pnfName, ifName, status);
            String vpnBindingId = extractValueFromJsonArray(vpnBindingInfo.getJSONArray("relationship-data"),
                    "vpn-binding.vpn-id");
            JSONObject connectivityInfo = getConnectivityInfo(vpnBindingId);
            String connectivityId = extractValueFromJsonArray(connectivityInfo.getJSONArray("relationship-data"),
                    "connectivity. connectivity-id");
            JSONObject serviceInstanceInfo = getServiceInstanceByConn(connectivityId);
            String serviceInstancePath = serviceInstanceInfo.getString("related-link");
            serviceInstancePath = serviceInstancePath.substring(0, serviceInstancePath.lastIndexOf('/'));

            String[] params = new String[2];

            Pattern pattern = Pattern.compile("/aai/v\\d+/business/customers/customer/(.+)/service-subscriptions/service-subscription/(.+)");
            Matcher matcher = pattern.matcher(serviceInstancePath);
            if (matcher.find()) {
                params[0] = matcher.group(1);
                params[1] = matcher.group(2);
            }

            Response response = get(getHostAddr(), getPath(serviceInstancePath));
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                throw new RuntimeException("Failed to connect to AAI. Cause: "
                        + response.getStatusInfo().getReasonPhrase());
            }
            JSONArray instances = getInstances(JSONObject.toJSONString(response.getEntity()));
            for (int i = 0; i < instances.size(); ++i) {
                JSONObject instance = instances.getJSONObject(i);
                Response res = get(getHostAddr(), serviceInstancePath + "/service-instances?service-instance-id="
                        + instance.getString("service-instance-id"));
                if (res.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                    throw new RuntimeException("Failed to connect to AAI. Cause: "
                            + response.getStatusInfo().getReasonPhrase());
                }
                String inputParams = JSONObject.parseObject(response.readEntity(String.class)).getString("input-parameters");
                instance.put("input-parameters", inputParams);
                instance.put("globalSubscriberId", params[0]);
                instance.put("serviceType", params[1]);
            }

            return instances;
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
        Response response = patch(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_TP_UPDATE, params), body);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new CorrelationException("Failed to connecto to AAI. Cause: "
                    + response.getStatusInfo().getReasonPhrase());
        }
    }

    public void updateLogicLinkStatus(String linkName, Map<String, Object> body) throws CorrelationException {
        Response response = patch(getHostAddr(),
                getPath(AaiConfig.MsbConsts.AAI_TP_UPDATE, "linkName", linkName), body);
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new CorrelationException("Failed to connecto to AAI. Cause: "
                    + response.getStatusInfo().getReasonPhrase());
        }
    }

    private JSONObject getVpnBindingInfo(String networkId, String pnfName,
                                         String ifName, String status) throws CorrelationException {
        Map<String, String> params = new HashMap();
        params.put("networkId", networkId);
        params.put("pnfName", pnfName);
        params.put("ifName", ifName);
        params.put("status", status);
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_VPN_ADDR, params));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new CorrelationException("Failed to connecto to AAI. Cause: "
                    + response.getStatusInfo().getReasonPhrase());
        }
        return getInfo(JSONObject.toJSONString(response.getEntity()), "p-interface", "vpn-binding");
    }

    private JSONObject getConnectivityInfo(String vpnId) throws CorrelationException {
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_CONN_ADDR, "vpnId", vpnId));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new CorrelationException("Failed to connect to AAI. Cause: "
                    + response.getStatusInfo().getReasonPhrase());
        }
        return getInfo(JSONObject.toJSONString(response.getEntity()), "vpn-binding", "connectivity");
    }

    private JSONObject getServiceInstanceByConn(String connectivityId) throws CorrelationException {
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_SERVICE_INSTANCE_ADDR_4_CCVPN,
                "connectivityId", connectivityId));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new CorrelationException("Failed to connect to AAI. Cause: "
                    + response.getStatusInfo().getReasonPhrase());
        }
        return getInfo(JSONObject.toJSONString(response.getEntity()), "connectivity", "service-instance");
    }

    private JSONArray getServiceInstances(String globalCustomerId, String serviceType) throws CorrelationException {
        Map<String, String> params = new HashMap();
        params.put("global-customer-id", globalCustomerId);
        params.put("service-type", serviceType);
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_SERVICE_INSTANCES_ADDR_4_CCVPN, params));
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new CorrelationException("Failed to connect to AAI. Cause: "
                    + response.getStatusInfo().getReasonPhrase());
        }
        return getInstances(JSONObject.toJSONString(response.getEntity()));
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

    private Response get(String host, String path) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(host).path(path);
        return target.request().headers(getAaiHeaders()).get();
    }

    private Response patch(String host, String path, Map<String, Object> body) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(host).path(path);
        return target.request().headers(getAaiHeaders()).method("PATCH", Entity.json(body));
    }

    private JSONObject getInfo(String response, String pField, String field) {
        JSONArray results = extractJsonArray(JSONObject.parseObject(response), "results");
        JSONObject pInterface = extractJsonObject(results.getJSONObject(0), pField);
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

    private JSONArray getInstances(String response) {
        JSONArray results = extractJsonArray(JSONObject.parseObject(response), "results");
        JSONObject pInterface = extractJsonObject(results.getJSONObject(0), "service-subscription");
        JSONObject serviceInstances = extractJsonObject(pInterface, "service-instances");
        JSONArray instance = extractJsonArray(serviceInstances, "service-instance");
        return instance;
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
        String[] msbInfo = MicroServiceConfig.getMsbServerAddrWithHttpPrefix().split(":");
        StringBuilder sb = new StringBuilder("http://");
        sb.append(msbInfo[0]).append(msbInfo[1]);
        return sb.toString();
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
}
