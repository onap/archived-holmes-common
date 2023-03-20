/**
 * Copyright 2017-2023 ZTE Corporation.
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

package org.onap.holmes.common.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ServiceRegisterEntity {

    private String serviceName;
    private String version;
    private String url;
    private String protocol;
    private String visualRange = "1";
    private List<ServiceNode> nodes = new ArrayList<>();

    public void setSingleNode(String ip, String port, int ttl) {
        ServiceNode node = new ServiceNode();
        if (ip != null && ip.length() > 0) {
            node.setIp(ip);
        } else {
            node.setIp(null);
        }
        node.setPort(port);
        node.setTtl(ttl);
        nodes.add(node);
    }
}