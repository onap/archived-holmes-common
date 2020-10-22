/**
 * Copyright 2017-2020 ZTE Corporation.
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

package org.onap.holmes.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.msb.sdk.discovery.entity.MicroServiceFullInfo;
import org.onap.msb.sdk.discovery.entity.MicroServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

import static org.onap.holmes.common.utils.JerseyClient.PROTOCOL_HTTP;
import static org.onap.holmes.common.utils.JerseyClient.PROTOCOL_HTTPS;

@Service
public class MsbRegister {
    private static final Logger log = LoggerFactory.getLogger(MsbRegister.class);

    private JerseyClient jerseyClient;

    @Inject
    public MsbRegister(JerseyClient jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    public void register2Msb(MicroServiceInfo msinfo) throws CorrelationException {
        String[] msbAddrInfo = MicroServiceConfig.getMsbIpAndPort();
        boolean isHttpsEnabled = StringUtils.isNotBlank(msbAddrInfo[1])
                && msbAddrInfo[1].equals("443");

        Client client = jerseyClient.client(isHttpsEnabled);
        WebTarget target = client.target(String.format("%s://%s:%s/api/microservices/v1/services",
                isHttpsEnabled ? PROTOCOL_HTTPS : PROTOCOL_HTTP, msbAddrInfo[0], msbAddrInfo[1]));

        log.info("Start to register Holmes Service to MSB...");

        MicroServiceFullInfo microServiceFullInfo = null;
        int retry = 0;
        int interval = 5;
        while (null == microServiceFullInfo && retry < 20) {
            log.info("Holmes Service Registration. Retry: " + retry++);

            Response response = target.queryParam("createOrUpdate", true)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(msinfo, MediaType.APPLICATION_JSON));

            if (response != null) {
                String ret = response.readEntity(String.class);
                int statusCode = response.getStatus();
                log.info(String.format("=========MSB REG=========\nStatus Code: %d\nInformation: %s", statusCode, ret));
                if (HttpStatus.isSuccess(statusCode)) {
                    microServiceFullInfo = GsonUtil.jsonToBean(ret, MicroServiceFullInfo.class);
                }
            }

            if (null == microServiceFullInfo) {
                log.warn(String.format("Failed to register the service to MSB. Sleep %ds and try again.", interval));
                threadSleep(TimeUnit.SECONDS.toSeconds(interval));
                interval += 5;
            } else {
                log.info("Registration succeeded!");
                break;
            }
        }

        if (null == microServiceFullInfo) {
            throw new CorrelationException("Failed to register the service to MSB!");
        }

        log.info("Service registration completed.");
    }

    private void threadSleep(long second) {
        log.info("Start sleeping...");
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException error) {
            log.error("thread sleep error message:" + error.getMessage(), error);
            Thread.currentThread().interrupt();
        }
        log.info("Wake up.");
    }
}