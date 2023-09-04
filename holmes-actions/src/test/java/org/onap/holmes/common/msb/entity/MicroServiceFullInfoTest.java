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

import java.util.*;

import static org.junit.Assert.*;

public class MicroServiceFullInfoTest {
    @Test
    public void testGettersAndSetters() {
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("service-name");
        microServiceFullInfo.setVersion("1.0");
        microServiceFullInfo.setUrl("http://example.com");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setVisualRange("local");
        microServiceFullInfo.setStatus("active");

        Set<NodeInfo> nodes = new HashSet<>();
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setNodeId("node-1");
        nodeInfo.setIp("127.0.0.1");
        nodeInfo.setPort("8080");
        nodeInfo.setTtl("300");
        nodeInfo.setCreated_at(new Date());
        nodeInfo.setUpdated_at(new Date());
        nodeInfo.setExpiration(new Date());
        nodes.add(nodeInfo);
        microServiceFullInfo.setNodes(nodes);

        List<KeyValuePair> metadata = new ArrayList<>();
        metadata.add(new KeyValuePair("key1", "value1"));
        metadata.add(new KeyValuePair("key2", "value2"));
        microServiceFullInfo.setMetadata(metadata);

        assertEquals("service-name", microServiceFullInfo.getServiceName());
        assertEquals("1.0", microServiceFullInfo.getVersion());
        assertEquals("http://example.com", microServiceFullInfo.getUrl());
        assertEquals("http", microServiceFullInfo.getProtocol());
        assertEquals("local", microServiceFullInfo.getVisualRange());
        assertEquals("active", microServiceFullInfo.getStatus());
        assertEquals(nodes, microServiceFullInfo.getNodes());
        assertEquals(metadata, microServiceFullInfo.getMetadata());
    }

    @Test
    public void testToString() {
        MicroServiceFullInfo microServiceFullInfo = new MicroServiceFullInfo();
        microServiceFullInfo.setServiceName("service-name");
        microServiceFullInfo.setVersion("1.0");
        microServiceFullInfo.setUrl("http://example.com");
        microServiceFullInfo.setProtocol("http");
        microServiceFullInfo.setVisualRange("local");
        microServiceFullInfo.setStatus("active");

        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setNodeId("node-1");
        nodeInfo.setIp("127.0.0.1");
        nodeInfo.setPort("8080");
        nodeInfo.setTtl("300");
        nodeInfo.setCreated_at(new Date());
        nodeInfo.setUpdated_at(new Date());
        nodeInfo.setExpiration(new Date());

        Set<NodeInfo> nodes = new HashSet<>();
        nodes.add(nodeInfo);
        microServiceFullInfo.setNodes(nodes);

        List<KeyValuePair> metadata = new ArrayList<>();
        metadata.add(new KeyValuePair("key1", "value1"));
        metadata.add(new KeyValuePair("key2", "value2"));
        microServiceFullInfo.setMetadata(metadata);

        // Test the toString method
        String expectedOutput = "MicroService List:\r\n" +
                "serviceName:service-name\r\n" +
                "version:1.0\r\n" +
                "url:http://example.com\r\n" +
                "protocol:http\r\n" +
                "visualRange:local\r\n" +
                "nodes:\r\n" +
                "  nodeId-node-1\r\n" +
                "  ip-127.0.0.1\r\n" +
                "  port-8080\r\n" +
                "  ttl-300\r\n" +
                "  Created_at-" + nodeInfo.getCreated_at() + "\r\n" +
                "  Updated_at-" + nodeInfo.getUpdated_at() + "\r\n" +
                "  Expiration-" + nodeInfo.getExpiration() + "\r\n" +
                "metadata:\r\n" +
                "  key-key1\r\n" +
                "  value-value1\r\n" +
                "  key-key2\r\n" +
                "  value-value2\r\n";

        assertEquals(expectedOutput, microServiceFullInfo.toString());
    }
}