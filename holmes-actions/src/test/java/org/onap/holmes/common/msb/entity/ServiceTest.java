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
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ServiceTest {
    @Test
    public void testGettersAndSetters() {
        Service<Integer> service = new Service<>();
        service.setServiceName("my-service");
        service.setVersion("1.0");
        service.setUrl("http://example.com");
        service.setProtocol("http");
        service.setVisualRange("local");
        service.setLb_policy("round-robin");
        service.setPath("/api");
        service.setEnable_ssl(true);

        Set<Integer> nodes = new HashSet<>();
        nodes.add(8080);
        nodes.add(8081);
        service.setNodes(nodes);

        List<KeyValuePair> metadata = List.of(new KeyValuePair("key1", "value1"), new KeyValuePair("key2", "value2"));
        service.setMetadata(metadata);

        assertEquals("my-service", service.getServiceName());
        assertEquals("1.0", service.getVersion());
        assertEquals("http://example.com", service.getUrl());
        assertEquals("http", service.getProtocol());
        assertEquals("local", service.getVisualRange());
        assertEquals("round-robin", service.getLb_policy());
        assertEquals("/api", service.getPath());
        assertEquals(true, service.isEnable_ssl());
        assertEquals(nodes, service.getNodes());
        assertEquals(metadata, service.getMetadata());
    }
}