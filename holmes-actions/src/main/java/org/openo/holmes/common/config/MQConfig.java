/**
 * Copyright 2017 ZTE Corporation.
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

package org.openo.holmes.common.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;

public class MQConfig {

    @JsonProperty
    @NotNull
    public String brokerIp = "localhost";

    @JsonProperty
    @NotNull
    public int brokerPort = 5672;


    @JsonProperty
    public String brokerUsername;

    @JsonProperty
    public String brokerPassword;

    @JsonProperty
    public boolean autoDiscover = false;

    @JsonProperty
    public String mqServiceName = "mqService";

    @JsonProperty
    public String mqServiceVersion = "v1";


    @JsonProperty
    public long healthCheckMillisecondsToWait = 2000; // 2 seconds

    @JsonProperty
    public int shutdownWaitInSeconds = 20;

    @JsonProperty
    public int timeToLiveInSeconds = -1; // Default no TTL. Jackson does not support java.util.Optional yet.

    @JsonProperty
    public Map<String, String> extConsumerConfMap = new HashMap<>();

    @JsonProperty
    public Map<String, String> extProducerConfMap = new HashMap<>();


    @Override
    public String toString() {
        return "MQConfig [brokerIp=" + brokerIp + ", brokerPort=" + brokerPort + ", brokerUsername="
            + brokerUsername + ", brokerPassword=" + brokerPassword + ", autoDiscover="
            + autoDiscover + ", mqServiceName=" + mqServiceName + ", mqServiceVersion="
            + mqServiceVersion + ", healthCheckMillisecondsToWait=" + healthCheckMillisecondsToWait
            + ", shutdownWaitInSeconds=" + shutdownWaitInSeconds + ", timeToLiveInSeconds="
            + timeToLiveInSeconds + "]";
    }


}
