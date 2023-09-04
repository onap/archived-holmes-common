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

import static org.junit.Assert.*;

public class NodeTest {

    @Test
    public void testToString() {
        Node node = new Node();
        node.setIp("127.0.0.1");
        node.setPort("8080");
        node.setTtl("300");

        String expectedOutput = "127.0.0.1:8080  ttl:300";
        assertEquals(expectedOutput, node.toString());
    }

    @Test
    public void testGettersAndSetters() {
        Node node = new Node();
        node.setIp("192.168.1.1");
        node.setPort("8888");
        node.setTtl("600");
        node.setCheckType("http");
        node.setCheckUrl("/health");
        node.setCheckInterval("30s");
        node.setCheckTimeOut("5s");

        assertEquals("192.168.1.1", node.getIp());
        assertEquals("8888", node.getPort());
        assertEquals("600", node.getTtl());
        assertEquals("http", node.getCheckType());
        assertEquals("/health", node.getCheckUrl());
        assertEquals("30s", node.getCheckInterval());
        assertEquals("5s", node.getCheckTimeOut());
    }

}