/**
 * Copyright 2018-2020 ZTE Corporation.
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.onap.holmes.common.aai.config.AaiConfig;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Logger log = LoggerFactory.getLogger(AaiQuery4Ccvpn.class);

    private MultivaluedMap<String, Object> headers;

    public static AaiQuery4Ccvpn newInstance() {
        return new AaiQuery4Ccvpn();
    }

    private static final String EMPTY_STR = "";

    private static final JsonObject EMPTY_JSON = new JsonObject();

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
        JsonObject linkInfo = getInfo(response.readEntity(String.class), "p-interface", "logical-link");
        if (linkInfo == null) {
            log.warn(String.format("Link information is missing from AAI. Method: [getLogicLink], " +
                    "params: [networkId - %s, pnfName - %s, ifName - %s].", networkId, pnfName, ifName));
            return EMPTY_STR;
        }
        return extractValueFromJsonArray(linkInfo.get("relationship-data").getAsJsonArray(), "logical-link.link-name");
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
    public JsonObject getServiceInstance(String networkId, String pnfName, String ifName, String status) {
        try {
            JsonObject vpnBindingInfo = getVpnBindingInfo(networkId, pnfName, ifName, status);
            if (vpnBindingInfo == null) {
                log.warn(String.format("VPN binding information is missing from AAI. " +
                        "Method: [getServiceInstance], params: [networkId - %s, pnfName - %s, " +
                        "ifName - %s, status - %s].", networkId, pnfName, ifName, status));
                return EMPTY_JSON;
            }
            String vpnBindingId = extractValueFromJsonArray(vpnBindingInfo.get("relationship-data").getAsJsonArray(),
                    "vpn-binding.vpn-id");
            JsonObject connectivityInfo = getConnectivityInfo(vpnBindingId);
            if (connectivityInfo == null) {
                log.warn(String.format("Connectivity information is missing from AAI. " +
                        "Method: [getServiceInstance], params: [networkId - %s, pnfName - %s, " +
                        "ifName - %s, status - %s].", networkId, pnfName, ifName, status));
                return EMPTY_JSON;
            }
            String connectivityId = extractValueFromJsonArray(connectivityInfo.get("relationship-data").getAsJsonArray(),
                    "connectivity.connectivity-id");
            JsonObject serviceInstanceInfo = getServiceInstanceByConn(connectivityId);
            if (serviceInstanceInfo == null) {
                log.warn(String.format("Service instance information is missing from AAI. " +
                        "Method: [getServiceInstance], params: [networkId - %s, pnfName - %s, " +
                        "ifName - %s, status - %s].", networkId, pnfName, ifName, status));
                return EMPTY_JSON;
            }
            String serviceInstancePath = serviceInstanceInfo.get("related-link").getAsString();

            Response response = get(getHostAddr(), getPath(serviceInstancePath));
            JsonObject instance = JsonParser.parseString(response.readEntity(String.class)).getAsJsonObject();

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
        JsonObject jsonObject = JsonParser.parseString(r.readEntity(String.class)).getAsJsonObject();
        body.put("resource-version", jsonObject.get("resource-version").toString());

        put(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_TP_UPDATE, params), body);
    }

    public void updateLogicLinkStatus(String linkName, Map<String, Object> body) throws CorrelationException {
        Response r = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_LINK_UPDATE, "linkName", linkName));
        JsonObject jsonObject = JsonParser.parseString(r.readEntity(String.class)).getAsJsonObject();
        body.put("resource-version", jsonObject.get("resource-version").toString());
        body.put("link-type", jsonObject.get("link-type").toString());
        put(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_LINK_UPDATE, "linkName", linkName), body);
    }

    private JsonObject getVpnBindingInfo(String networkId, String pnfName,
                                         String ifName, String status) throws CorrelationException {
        Map<String, String> params = new HashMap();
        params.put("networkId", networkId);
        params.put("pnfName", pnfName);
        params.put("ifName", ifName);
        params.put("status", status);
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_VPN_ADDR, params));
        return getInfo(response.readEntity(String.class), "p-interface", "vpn-binding");
    }

    private JsonObject getConnectivityInfo(String vpnId) throws CorrelationException {
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_CONN_ADDR, "vpnId", vpnId));
        return getInfo(response.readEntity(String.class), "vpn-binding", "connectivity");
    }

    private JsonObject getServiceInstanceByConn(String connectivityId) throws CorrelationException {
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_SERVICE_INSTANCE_ADDR_4_CCVPN,
                "connectivityId", connectivityId));
        return getInfo(response.readEntity(String.class), "connectivity", "service-instance");
    }

    private JsonObject getServiceInstance(String globalCustomerId, String serviceType) throws CorrelationException {
        Map<String, String> params = new HashMap();
        params.put("global-customer-id", globalCustomerId);
        params.put("service-type", serviceType);
        Response response = get(getHostAddr(), getPath(AaiConfig.MsbConsts.AAI_SERVICE_INSTANCES_ADDR_4_CCVPN, params));
        return JsonParser.parseString(response.readEntity(String.class)).getAsJsonObject();
    }

    private String getPath(String urlTemplate, Map<String, String> pathParams) {
        String url = urlTemplate;
        for (Map.Entry<String, String> entry : pathParams.entrySet()) {
            url = url.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
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

    private JsonObject getInfo(String response, String pField, String field) {
        JsonObject jObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject pInterface = extractJsonObject(jObject, pField);
        if (pInterface == null) {
            pInterface = jObject;
        }
        JsonObject relationshipList = extractJsonObject(pInterface, "relationship-list");
        JsonArray relationShip = extractJsonArray(relationshipList, "relationship");
        if (relationShip != null) {
            for (int i = 0; i < relationShip.size(); ++i) {
                final JsonObject object = relationShip.get(i).getAsJsonObject();
                if (object.get("related-to").getAsString().equals(field)) {
                    return object;
                }
            }
        }
        return null;
    }

    private JsonObject extractJsonObject(JsonObject obj, String key) {
        if (obj != null && key != null && obj.has(key)) {
            return obj.get(key).getAsJsonObject();
        }
        return null;
    }

    private JsonArray extractJsonArray(JsonObject obj, String key) {
        if (obj != null && key != null && obj.has(key)) {
            return obj.get(key).getAsJsonArray();
        }
        return null;
    }

    private MultivaluedMap getAaiHeaders() {
        return headers;
    }

    private String getHostAddr() {
        return MicroServiceConfig.getMsbServerAddrWithHttpPrefix();
    }

    private String extractValueFromJsonArray(JsonArray relationshipData, String keyName) {
        if (relationshipData != null) {
            for (int i = 0; i < relationshipData.size(); ++i) {
                JsonObject item = relationshipData.get(i).getAsJsonObject();
                if (item.get("relationship-key").getAsString().equals(keyName)) {
                    return item.get("relationship-value").getAsString();
                }
            }
        }
        return null;
    }

    private String getErrorMsg(String url, Map<String, Object> body, Response response) {
        Gson gson = new Gson();
        StringBuilder sb = new StringBuilder();
        sb.append("Rerquest URL: ").append(url).append("\n");
        sb.append("Request Header: ").append(gson.toJson(headers)).append("\n");
        if (body != null) {
            sb.append("Request Body: ").append(gson.toJson(body)).append("\n");
        }
        if (response != null) {
            sb.append("Request Body: ").append(response.readEntity(String.class));
        }
        return sb.toString();
    }
}
