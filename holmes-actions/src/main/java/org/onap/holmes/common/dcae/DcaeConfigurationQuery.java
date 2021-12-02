/*
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.dcae;

import org.onap.holmes.common.config.MicroServiceConfig;
import org.onap.holmes.common.dcae.entity.DcaeConfigurations;
import org.onap.holmes.common.dcae.utils.DcaeConfigurationParser;
import org.onap.holmes.common.exception.CorrelationException;

@Deprecated
public class DcaeConfigurationQuery {

    public static DcaeConfigurations getDcaeConfigurations(String hostname)
            throws CorrelationException {
        String serviceConfig = MicroServiceConfig.getServiceConfigInfoFromCBS(hostname);
        return DcaeConfigurationParser.parse(serviceConfig);
    }
}
