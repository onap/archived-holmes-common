/**
 * Copyright 2023 ZTE Corporation.
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

package org.onap.holmes.common.msb.entity;

import java.io.Serializable;
import java.util.Iterator;

public class MicroServiceFullInfo extends Service<NodeInfo> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status = "1";

    public MicroServiceFullInfo() {
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("MicroService List:").append("\r\n");
        buf.append("serviceName:").append(this.getServiceName()).append("\r\n");
        buf.append("version:").append(this.getVersion()).append("\r\n");
        buf.append("url:").append(this.getUrl()).append("\r\n");
        buf.append("protocol:").append(this.getProtocol()).append("\r\n");
        buf.append("visualRange:").append(this.getVisualRange()).append("\r\n");
        buf.append("nodes:").append("\r\n");
        Iterator nodes = this.getNodes().iterator();

        while (nodes.hasNext()) {
            NodeInfo nodeInstace = (NodeInfo) nodes.next();
            buf.append("  nodeId-").append(nodeInstace.getNodeId()).append("\r\n");
            buf.append("  ip-").append(nodeInstace.getIp()).append("\r\n");
            buf.append("  port-").append(nodeInstace.getPort()).append("\r\n");
            buf.append("  ttl-").append(nodeInstace.getTtl()).append("\r\n");
            buf.append("  Created_at-").append(nodeInstace.getCreated_at()).append("\r\n");
            buf.append("  Updated_at-").append(nodeInstace.getUpdated_at()).append("\r\n");
            buf.append("  Expiration-").append(nodeInstace.getExpiration()).append("\r\n");
        }

        buf.append("metadata:").append("\r\n");
        if (this.getMetadata() != null && this.getMetadata().size() > 0) {
            nodes = this.getMetadata().iterator();
            while (nodes.hasNext()) {
                KeyValuePair keyValuePair = (KeyValuePair) nodes.next();
                buf.append("  key-").append(keyValuePair.getKey()).append("\r\n");
                buf.append("  value-").append(keyValuePair.getValue()).append("\r\n");
            }
        }

        return buf.toString();
    }
}
