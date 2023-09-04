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

import java.util.Date;

import static org.junit.Assert.*;

public class NodeInfoTest {
    @Test
    public void testGettersAndSetters() {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setNodeId("node-1");
        nodeInfo.setStatus("active");

        Date expiration = new Date();
        nodeInfo.setExpiration(expiration);

        Date createdAt = new Date();
        nodeInfo.setCreated_at(createdAt);

        Date updatedAt = new Date();
        nodeInfo.setUpdated_at(updatedAt);

        assertEquals("node-1", nodeInfo.getNodeId());
        assertEquals("active", nodeInfo.getStatus());
        assertEquals(expiration, nodeInfo.getExpiration());
        assertEquals(createdAt, nodeInfo.getCreated_at());
        assertEquals(updatedAt, nodeInfo.getUpdated_at());
    }
}