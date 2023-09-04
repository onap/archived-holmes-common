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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Service<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String serviceName;
    private String version = "";
    private String url = "";
    private String protocol = "";
    private String visualRange = "1";
    private String lb_policy = "";
    private String path = "";
    private Set<T> nodes;
    private List<KeyValuePair> metadata;
    private boolean enable_ssl = false;

    public Service() {
    }

    public boolean isEnable_ssl() {
        return this.enable_ssl;
    }

    public void setEnable_ssl(boolean enable_ssl) {
        this.enable_ssl = enable_ssl;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLb_policy() {
        return this.lb_policy;
    }

    public void setLb_policy(String lb_policy) {
        this.lb_policy = lb_policy;
    }

    public List<KeyValuePair> getMetadata() {
        return this.metadata;
    }

    public void setMetadata(List<KeyValuePair> metadata) {
        this.metadata = metadata;
    }

    public Set<T> getNodes() {
        return this.nodes;
    }

    public void setNodes(Set<T> nodes) {
        this.nodes = nodes;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getVisualRange() {
        return this.visualRange;
    }

    public void setVisualRange(String visualRange) {
        this.visualRange = visualRange;
    }
}
