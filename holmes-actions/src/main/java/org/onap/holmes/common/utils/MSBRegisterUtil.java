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

package org.onap.holmes.common.utils;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

import com.eclipsesource.jaxrs.consumer.ConsumerFactory;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.api.entity.ServiceRegisterEntity;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.exception.CorrelationException;
import org.onap.holmes.common.msb.MicroserviceBusRest;
import org.onap.msb.sdk.discovery.common.RouteException;
import org.onap.msb.sdk.discovery.entity.MicroServiceFullInfo;
import org.onap.msb.sdk.discovery.entity.MicroServiceInfo;
import org.onap.msb.sdk.httpclient.msb.MSBServiceClient;

@Slf4j
@Service
public class MSBRegisterUtil {

    public void register2Msb(MicroServiceInfo msinfo) throws CorrelationException {
        String[] msbAddrInfo = MicroServiceConfig.getMsbAddrInfo();
        MSBServiceClient msbClient = new MSBServiceClient(msbAddrInfo[0],
                Integer.parseInt(msbAddrInfo[1]));

        log.info("Start to register Holmes Service to MSB...");
        MicroServiceFullInfo microServiceFullInfo = null;
        int retry = 0;
        while (null == microServiceFullInfo && retry < 20) {
            log.info("Holmes Service Registration. Retry: " + retry);
            retry++;
            try {
                microServiceFullInfo = msbClient.registerMicroServiceInfo(msinfo, false);
            } catch (RouteException e) {

            }

            if (null == microServiceFullInfo) {
                log.warn("Failed to register the service to MSB. Sleep 30s and try again.");
                threadSleep(30000);
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

    private void threadSleep(int second) {
        log.info("Start sleeping...");
        try {
            Thread.sleep(second);
        } catch (InterruptedException error) {
            log.error("thread sleep error message:" + error.getMessage(), error);
            Thread.currentThread().interrupt();
        }
        log.info("Wake up.");
    }
}