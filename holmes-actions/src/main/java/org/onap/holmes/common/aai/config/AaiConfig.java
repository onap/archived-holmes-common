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
package org.onap.holmes.common.aai.config;

public class AaiConfig {

    public static String VNF_ADDR = "/aai/v11/network/generic-vnfs/generic-vnf?";

    public static String VM_ADDR = "/aai/v11/search/nodes-query?search-node-type=vserver&filter=";

    public static String X_TRANSACTION_ID = "9999";

    public static String X_FROMAPP_ID = "jimmy-postman";

    private static String AAI_AUTHENTICATION_USER = "AAI";

    private static String AAI_AUTHENTICATION_PAASWORD = "AAI";

    public static String getAuthenticationCredentials() {
        String usernameAndPassword = AAI_AUTHENTICATION_USER + ":"
                + AAI_AUTHENTICATION_PAASWORD;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
    }

}
