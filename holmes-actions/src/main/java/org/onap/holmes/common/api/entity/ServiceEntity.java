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
package org.onap.holmes.common.api.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceEntity {

    private String serviceName;
    private String version;
    private String url;
    private String protocol;
    private String visualRange;
    private List<ServiceNode4Query> nodes;

    private String lb_policy;
    private String publish_port;
    private String namespace;
    private String network_plane_type;
    private String host;
    private String path;
    private Boolean enable_ssl;
    private List metadata;
    private List labels;
    private String status;
    private Boolean is_manual;


}
