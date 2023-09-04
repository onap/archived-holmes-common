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

public class MicroServiceInfo extends Service<Node> implements Serializable {
    private static final long serialVersionUID = 1L;

    public MicroServiceInfo() {
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("MicroService List:").append("\r\n");
        buf.append("serviceName:").append(this.getServiceName()).append("\r\n");
        buf.append("version:").append(this.getVersion()).append("\r\n");
        buf.append("url:").append(this.getUrl()).append("\r\n");
        buf.append("protocol:").append(this.getProtocol()).append("\r\n");
        buf.append("visualRange:").append(this.getVisualRange()).append("\r\n");
        buf.append("enable_ssl:").append(this.isEnable_ssl()).append("\r\n");
        buf.append("nodes:").append("\r\n");
        Iterator nodes = this.getNodes().iterator();

        while (nodes.hasNext()) {
            Node nodeInstace = (Node) nodes.next();
            buf.append("  ip-").append(nodeInstace.getIp()).append("\r\n");
            buf.append("  port-").append(nodeInstace.getPort()).append("\r\n");
            buf.append("  ttl-").append(nodeInstace.getTtl()).append("\r\n");
        }

        return buf.toString();
    }
}
