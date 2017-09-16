/**
 * Copyright  2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.config;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.glassfish.jersey.client.ClientConfig;
import org.onap.holmes.common.api.stat.Alarm;
import org.onap.holmes.common.constant.AlarmConst;

@Slf4j
public class MicroServiceConfig {

    final static public String CONSUL_ADDR_SUF = ":8500/v1/catalog/service/";
    final static public String CONSUL_HOST = "CONSUL_HOST";
    final static public String HOSTNAME = "HOSTNAME";
    final static public String CONFIG_BINDING_SERVICE = "CONFIG_BINDING_SERVICE";
    final static public String DOCKER_HOST = "DOCKER_HOST";
    final static public String MSB_ADDR = "MSB_ADDR";

    private static String getEnv(String name) {
        String value = System.getenv(name);
        if (value == null) {
            value = System.getProperty(name);
        }
        return value;
    }

    public static String getConsulAddrInfo() {
        return getEnv(CONSUL_HOST) + CONSUL_ADDR_SUF;
    }

    public static String getConfigBindingServiceAddrInfo() {
        String ret = null;
        String queryString = getConsulAddrInfo() + CONFIG_BINDING_SERVICE;
        try {
            JSONObject addrJson = (JSONObject) JSONArray.fromObject(execQuery(queryString)).get(0);
            if (addrJson.has("ServiceAddress") && addrJson.has("ServicePort")) {
                ret = addrJson.getString("ServiceAddress") + ":" + addrJson.getString("ServicePort");
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return ret;
    }

    private static String execQuery(String queryString) {
        Client client = ClientBuilder.newClient(new ClientConfig());
        Response response = client.target(queryString).request().get();
        return response.readEntity(String.class);
    }

    public static String getServiceAddrInfoFromCBS(String serviceName) {
        String ret = null;
        String url = getConfigBindingServiceAddrInfo() + "/service_component/" +serviceName;
        try {
            JSONObject jsonObject = JSONObject.fromObject(execQuery(url));
            if (jsonObject.has(serviceName)) {
                ret = (String) jsonObject.getJSONArray(serviceName).get(0);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return ret;
    }

    public static String getMsbServerAddr() {
        String[] addrInfo = getMsbAddrInfo();
        String ret = addrInfo[0] + ":" + addrInfo[1];
        if (!ret.startsWith(AlarmConst.HTTP) || !ret.startsWith(AlarmConst.HTTPS)){
            ret = AlarmConst.HTTP + ret;
        }
        return ret;
    }

    public static String[] getMsbAddrInfo() {
        String[] msbServerInfo = null;

        String info = getServiceAddrInfoFromCBS(MSB_ADDR);
        if (info != null){
            msbServerInfo = split(info);
        } else {
            msbServerInfo = split(getEnv(MSB_ADDR));
        }

        return msbServerInfo;
    }

    public static String[] getServiceAddrInfo() {
        String[] serviceAddrInfo = null;
        String info = getServiceAddrInfoFromCBS(getEnv(HOSTNAME));
        if (info != null){
            serviceAddrInfo = split(info);
        } else {
            serviceAddrInfo = split(getEnv(HOSTNAME));
        }
        return serviceAddrInfo;
    }

    private static String[] split(String addr) {
        String ip;
        String port = "80";
        if (addr.lastIndexOf(":") == -1){
            ip = addr;
        } else if (addr.lastIndexOf(":") < 5 && addr.indexOf("://") != -1) {
            ip = addr.substring(addr.indexOf("//") + 2);    //remove the http(s):// prefix
        } else {
            ip = addr.substring(addr.indexOf("://") != -1 ? addr.indexOf("//") + 2 : 0, addr.lastIndexOf(":"));
            port = addr.substring(addr.lastIndexOf(":") + 1);
        }
        return new String[] {ip, port};
    }

}
