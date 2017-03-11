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

import com.eclipsesource.jaxrs.consumer.ConsumerFactory;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientConfig;
import org.jvnet.hk2.annotations.Service;
import org.openo.holmes.common.api.entity.ServiceRegisterEntity;
import org.openo.holmes.common.config.MicroServiceConfig;
import org.openo.holmes.common.msb.MicroserviceBusRest;

@Slf4j
@Service
public class MSBRegisterUtil {

    public void register(ServiceRegisterEntity entity) throws IOException {
        log.info("start holmes micro service register");
        boolean flag = false;
        int retry = 0;
        while (!flag && retry < 20) {
            log.info("Holmes microservice register. retry:" + retry);
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

    private boolean inner_register(ServiceRegisterEntity entity) {
        ClientConfig config = new ClientConfig();
        try {
            log.info("msbServerAddr:" + MicroServiceConfig.getMsbServerAddr());
            log.info("entity:" + entity);
            MicroserviceBusRest resourceserviceproxy = ConsumerFactory.createConsumer(
                    MicroServiceConfig.getMsbServerAddr(), config, MicroserviceBusRest.class);
            resourceserviceproxy.registerServce("false", entity);
        } catch (Exception error) {
            log.error("microservice register failed!" + error.getMessage());
            return false;
        }
        return true;
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