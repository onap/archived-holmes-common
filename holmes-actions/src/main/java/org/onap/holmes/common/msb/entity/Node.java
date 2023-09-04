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

@JsonIgnoreProperties(ignoreUnknown = true)
public class Node implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ip;
    private String port;
    private String ttl = "";
    private String checkType = "";
    private String checkUrl = "";
    private String checkInterval;
    private String checkTimeOut;

    public Node() {
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getTtl() {
        return this.ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getCheckType() {
        return this.checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getCheckUrl() {
        return this.checkUrl;
    }

    public void setCheckUrl(String checkUrl) {
        this.checkUrl = checkUrl;
    }

    public String getCheckInterval() {
        return this.checkInterval;
    }

    public void setCheckInterval(String checkInterval) {
        this.checkInterval = checkInterval;
    }

    public String getCheckTimeOut() {
        return this.checkTimeOut;
    }

    public void setCheckTimeOut(String checkTimeOut) {
        this.checkTimeOut = checkTimeOut;
    }

    public String toString() {
        return this.ip + ":" + this.port + "  ttl:" + this.ttl;
    }
}

