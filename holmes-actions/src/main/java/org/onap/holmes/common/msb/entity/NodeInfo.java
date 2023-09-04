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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class NodeInfo extends Node {
    private static final long serialVersionUID = 8955786461351557306L;
    private String nodeId;
    private String status;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date expiration;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date created_at;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date updated_at;

    public NodeInfo() {
    }

    public Date getExpiration() {
        return this.expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Date getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
