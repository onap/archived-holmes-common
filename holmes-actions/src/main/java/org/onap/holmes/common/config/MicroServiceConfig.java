/**
 * Copyright  2017-2020 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.onap.holmes.common.constant.AlarmConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.regex.Pattern;

public class MicroServiceConfig {

    final static public String CONSUL_ADDR_SUF = ":8500/v1/catalog/service/";
    final static public String CONSUL_HOST = "CONSUL_HOST";
    final static public String HOSTNAME = "HOSTNAME";
    final static public String POD_IP = "POD_IP";
    final static public String CONFIG_BINDING_SERVICE = "CONFIG_BINDING_SERVICE";
    final static public String DOCKER_HOST = "DOCKER_HOST";
    final static public String MSB_ADDR = "MSB_ADDR";
    final static public Pattern IP_REG = Pattern.compile("(http(s)?://)?(\\d+\\.\\d+\\.\\d+\\.\\d+)(:(\\d+))?");
    final static public String AAI_HOSTNAME = "aai.onap";

    final static public Logger log = LoggerFactory.getLogger(MicroServiceConfig.class);

    public static String getEnv(String name) {
        String value = System.getenv(name);
        if (value == null) {
            value = System.getProperty(name);
        }
        return value;
    }

    public static String getConsulAddrInfo() {
        return "http://" + getEnv(CONSUL_HOST) + CONSUL_ADDR_SUF;
    }

    public static String getServiceAddrInfoFromDcaeConsulByHostName(String hostname) {
        String ret = null;
        String queryString = getConsulAddrInfo() + hostname;
        log.info("Query the " + hostname + " address using the URL: " + queryString);
        try {
            JsonObject addrJson = JsonParser.parseString(execQuery(queryString))
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject();
            if (addrJson != null && addrJson.get("ServiceAddress") != null
                    && addrJson.get("ServicePort") != null) {
                ret = "http://" + addrJson.get("ServiceAddress").getAsString() + ":" + addrJson
                        .get("ServicePort").getAsString();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        log.info("The " + hostname + " address is " + ret);
        return ret;
    }

    private static String execQuery(String queryString) {
        Client client = ClientBuilder.newBuilder().build();
        Response response = client.target(queryString).request().get();
        return response.readEntity(String.class);
    }

    public static String getServiceConfigInfoFromCBS(String hostname) {
        String ret = null;
        String url = getServiceAddrInfoFromDcaeConsulByHostName(getEnv(CONFIG_BINDING_SERVICE)) + "/service_component/" + hostname;
        try {
            ret = execQuery(url);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        log.info("The query url is: " + url + ". The corresponding configurations are " + ret);
        return ret;
    }

    public static String getMsbServerAddrWithHttpPrefix() {
        String[] addrInfo = getMsbIpAndPort();
        String ret = addrInfo[0] + ":" + addrInfo[1];
        if (!ret.startsWith(AlarmConst.HTTP) || !ret.startsWith(AlarmConst.HTTPS)) {
            ret = AlarmConst.HTTP + ret;
        }
        return ret;
    }

    public static String getAaiAddr() {
        return AlarmConst.HTTPS + AAI_HOSTNAME + ":8443";
    }

    public static String[] getMsbIpAndPort() {
        return split(getEnv(MSB_ADDR));
    }

    public static String[] getMicroServiceIpAndPort() {
        String[] serviceAddrInfo = null;
        String info = getServiceAddrInfoFromDcaeConsulByHostName(getEnv(HOSTNAME));
        log.info("Got the service information of \"" + getEnv(HOSTNAME) + "\" from Consul. The response is " + info + ".");

        if (info != null && !info.isEmpty()) {
            if (!isIpAddress(info)) {
                info = getEnv(POD_IP);
            }
            serviceAddrInfo = split(info);
        } else {
            serviceAddrInfo = split(getEnv(HOSTNAME));
        }
        return serviceAddrInfo;
    }

    private static boolean isIpAddress(String info) {
        return IP_REG.matcher(info).matches();
    }

    private static String[] split(String addr) {
        String ip;
        String port = "80";
        if (addr.lastIndexOf(":") == -1) {
            ip = addr;
        } else if (addr.lastIndexOf(":") < 5 && addr.indexOf("://") != -1) {
            ip = addr.substring(addr.indexOf("//") + 2);    //remove the http(s):// prefix
        } else {
            ip = addr.substring(addr.indexOf("://") != -1 ? addr.indexOf("//") + 2 : 0, addr.lastIndexOf(":"));
            port = addr.substring(addr.lastIndexOf(":") + 1);
        }
        return new String[]{ip, port};
    }

}
