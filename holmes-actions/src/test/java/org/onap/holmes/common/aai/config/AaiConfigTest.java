/**
 * Copyright 2017 ZTE Corporation.
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
package org.onap.holmes.common.aai.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import org.junit.Test;

public class AaiConfigTest {

    @Test
    public void testAaiConfig_get_static_fields() {
        String aaiVnfAddr = "/aai/v19/network/generic-vnfs/generic-vnf";
        String aaiVmAddr = "/aai/v19/search/nodes-query?search-node-type=vserver&filter=";
        String xTransactionId = "9999";
        String xFromAppId = "jimmy-postman";
        assertThat(aaiVnfAddr, equalTo(AaiConfig.AaiConsts.AAI_VNF_ADDR));
        assertThat(aaiVmAddr, equalTo(AaiConfig.AaiConsts.AAI_VM_ADDR));
        assertThat(xTransactionId, equalTo(AaiConfig.X_TRANSACTION_ID));
        assertThat(xFromAppId, equalTo(AaiConfig.X_FROMAPP_ID));
    }

    @Test
    public void testAaiConfig_getAuthenticationCredentials() {
        String expect = "Basic QUFJOkFBSQ==";
        assertThat(expect, equalTo(AaiConfig.getAuthenticationCredentials()));
    }
}