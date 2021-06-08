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

import lombok.Getter;
import lombok.Setter;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.dmaap.entity.PolicyMsg;
import org.onap.holmes.common.utils.JerseyClient;

import javax.ws.rs.client.Entity;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Service
public class Publisher {
    private String url;
    private JerseyClient client = JerseyClient.newInstance(TimeUnit.SECONDS.toMillis(30));

    public void publish(PolicyMsg msg) {
        client.post(url, Entity.json(msg));
    }
}
