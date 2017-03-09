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

package org.openo.holmes.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jvnet.hk2.annotations.Service;
import org.openo.holmes.common.api.entity.ServiceRegisterEntity;
import org.openo.holmes.common.config.MicroServiceConfig;
import org.openo.holmes.common.constant.AlarmConst;

@Slf4j
@Service
public class MSBRegisterUtil {

    public boolean register(ServiceRegisterEntity entity) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(entity);
            HttpPost httpPost = new HttpPost(MicroServiceConfig.getMsbServerAddr()
                    + "/api/microservices/v1/services?createOrUpdate=false");
            if (StringUtils.isNotEmpty(content)) {
                httpPost.setEntity(new ByteArrayEntity(content.getBytes()));
            }
            this.setHeader(httpPost);
            HttpResponse response;
            try {
                response = httpClient.execute(httpPost);
            } catch (Exception e) {
                log.warn("Registering the service to the bus failure", e);
                return false;
            }
            if (response.getStatusLine().getStatusCode() == AlarmConst.MICRO_SERVICE_STATUS_SUCCESS) {
                log.info("Registration successful service to the bus :" + response.getEntity());
                return true;
            } else {
                log.warn(
                        "Registering the service to the bus failure:" + response.getStatusLine().getStatusCode() + " " +
                                response.getStatusLine().getReasonPhrase());
                return false;
            }
        } finally {
            httpClient.close();
        }
    }

    private void setHeader(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader("Content-Type", "text/html;charset=UTF-8");
        httpRequestBase.setHeader("Accept", "application/json");
        httpRequestBase.setHeader("Content-Type", "application/json");
    }
}