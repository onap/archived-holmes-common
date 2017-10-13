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

import org.onap.holmes.common.dcae.entity.DcaeConfigurations;
import org.onap.holmes.common.dcae.entity.SecurityInfo;

public class DcaeConfigurationsCache {

    private static DcaeConfigurations dcaeConfigurations;

    public synchronized static SecurityInfo getPubSecInfo(String key) {
        if(dcaeConfigurations != null)
            return (SecurityInfo) dcaeConfigurations.get(key);
        return null;
    }

    public synchronized static void setDcaeConfigurations(DcaeConfigurations configurations) {
        dcaeConfigurations = configurations;
    }
}
