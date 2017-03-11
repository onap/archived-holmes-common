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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jvnet.hk2.annotations.Service;
import org.openo.holmes.common.api.entity.ServiceRegisterEntity;
import org.openo.holmes.common.config.MicroServiceConfig;
import org.openo.holmes.common.constant.AlarmConst;

@Slf4j
@Service
public class MSBRegisterUtil {

    public void register(ServiceRegisterEntity entity) throws IOException {
        log.info("start inventory micro service register");
        boolean flag = false;
        int retry = 0;
        while (!flag && retry < 20) {
            log.info("holmes micro service register.retry:" + retry);
            retry++;
            flag = inner_register(entity);
            if (!flag) {
                log.warn("micro service register failed, sleep 30S and try again.");
                threadSleep(30000);
            } else {
                log.info("micro service register success!");
                break;
            }
        }
        log.info("holmes micro service register end.");
    }

    private void setHeader(HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader("Content-Type", "text/html;charset=UTF-8");
        httpRequestBase.setHeader("Accept", "application/json");
        httpRequestBase.setHeader("Content-Type", "application/json");
    }

    private boolean inner_register(ServiceRegisterEntity entity) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(entity);
            HttpPost httpPost = new HttpPost(
                    MicroServiceConfig.getMsbServerAddr() + "/api/microservices/v1/services?createOrUpdate=true");
            HttpGet httpGet = new HttpGet(
                    MicroServiceConfig.getMsbServerAddr() + "/api/microservices/v1/services/");
            if (StringUtils.isNotEmpty(content)) {
                httpPost.setEntity(new ByteArrayEntity(content.getBytes()));
            }
            this.setHeader(httpPost);
            this.setHeader(httpGet);
            HttpResponse response;
            try {
                response = httpClient.execute(httpPost);
            } catch (Exception e) {
                log.warn("Registering the service to the bus failure", e);
                return false;
            }
            HttpResponse responseGet = null;
            try {
                responseGet = httpClient.execute(httpPost);
                log.info("all service:" + EntityUtils.toString(responseGet.getEntity()));
            } catch (Exception e) {
                if (responseGet != null) {
                    log.info(responseGet.getStatusLine().getReasonPhrase());
                }
                log.warn("query all service failure", e);
            }
            if (response.getStatusLine().getStatusCode() == AlarmConst.MICRO_SERVICE_STATUS_SUCCESS) {
                log.info("Registration successful service to the bus :" + EntityUtils.toString(response.getEntity()));
                return true;
            } else {
                log.warn(
                        "Registering the service to the bus failure:" + response.getStatusLine().getStatusCode() + " " +
                                response.getStatusLine().getReasonPhrase());
                return false;
            }
        } catch (IOException e) {
            log.warn("ServiceRegisterEntity:" + entity + " parse failed", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.warn("At the time of registering service httpclient close failure", e);
            }
        }
        return false;
    }

    private void threadSleep(int second) {
        log.info("start sleep ....");
        try {
            Thread.sleep(second);
        } catch (InterruptedException error) {
            log.error("thread sleep error.errorMsg:" + error.getMessage());
        }
        log.info("sleep end .");
    }
}