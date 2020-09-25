/**
 * Copyright 2020 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.holmes.common.engine.entity;

public class EngineEntity {
    private String ip;
    private int port;
    private long lastModified;

    public EngineEntity(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.lastModified = System.currentTimeMillis();
    }

    public EngineEntity() {
    }

    public String getId() {
        return ip + "_" + port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || ! (o instanceof  EngineEntity)) {
            return false;
        }

        return ((EngineEntity) o).getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}