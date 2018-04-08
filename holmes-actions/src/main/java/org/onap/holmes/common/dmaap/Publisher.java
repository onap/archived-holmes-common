/*
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.dmaap;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.onap.holmes.common.exception.CorrelationException;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import javax.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.utils.HttpsUtils;

@Getter
@Setter
@Service
@Slf4j
public class Publisher {

    private String topic;
    private String url;
    private String authInfo;
    private String authExpDate;

    public boolean publish(PolicyMsg msg) throws CorrelationException {
        String content;
        try {
            content = JSON.toJSONString(msg);
        } catch (Exception e) {
            throw new CorrelationException("Failed to convert the message object to a json string.",
                    e);
        }
        HttpResponse httpResponse;
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept", MediaType.APPLICATION_JSON);
        headers.put("Content-Type", MediaType.APPLICATION_JSON);
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpsUtils.getHttpClient(HttpsUtils.DEFUALT_TIMEOUT);
            httpResponse = HttpsUtils.post(url, headers, new HashMap<>(), new StringEntity(content, "utf-8"), httpClient);
        } catch (Exception e) {
            throw new CorrelationException("Failed to connect to DCAE.", e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.warn("Failed to close http client!");
                }
            }
        }
        return checkStatus(httpResponse);
    }

    private boolean checkStatus(HttpResponse httpResponse) {
        return (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) ? true : false;
    }
}
