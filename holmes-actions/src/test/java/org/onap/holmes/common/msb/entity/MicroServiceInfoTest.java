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

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MicroServiceInfoTest {
    @Test
    public void testToString() {
        MicroServiceInfo microServiceInfo = getMicroServiceInfo();

        String expectedOutput = "MicroService List:\r\n" +
                "serviceName:service-name\r\n" +
                "version:1.0\r\n" +
                "url:http://example.com\r\n" +
                "protocol:http\r\n" +
                "visualRange:local\r\n" +
                "enable_ssl:true\r\n" +
                "nodes:\r\n" +
                "  ip-127.0.0.1\r\n" +
                "  port-8080\r\n" +
                "  ttl-300\r\n";

        assertEquals(expectedOutput, microServiceInfo.toString());
    }

    private static MicroServiceInfo getMicroServiceInfo() {
        MicroServiceInfo microServiceInfo = new MicroServiceInfo();
        microServiceInfo.setServiceName("service-name");
        microServiceInfo.setVersion("1.0");
        microServiceInfo.setUrl("http://example.com");
        microServiceInfo.setProtocol("http");
        microServiceInfo.setVisualRange("local");
        microServiceInfo.setEnable_ssl(true);

        Set<Node> nodes = new HashSet<>();
        Node node = new Node();
        node.setIp("127.0.0.1");
        node.setPort("8080");
        node.setTtl("300");
        nodes.add(node);
        microServiceInfo.setNodes(nodes);
        return microServiceInfo;
    }
}