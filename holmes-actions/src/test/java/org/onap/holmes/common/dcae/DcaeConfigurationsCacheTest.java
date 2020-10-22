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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.holmes.common.dcae.entity.DcaeConfigurations;
import org.onap.holmes.common.dcae.entity.SecurityInfo;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest(DcaeConfigurationsCache.class)
@RunWith(PowerMockRunner.class)
public class DcaeConfigurationsCacheTest {

    @Test
    public void testDcaeConfigurationsCache() {
        DcaeConfigurations dcaeConfigurations = new DcaeConfigurations();
        SecurityInfo securityInfo = new SecurityInfo();
        securityInfo.setAafUsername("tset11");
        dcaeConfigurations.addPubSecInfo("test", securityInfo);
        DcaeConfigurationsCache.setDcaeConfigurations(dcaeConfigurations);
        System.out.println(DcaeConfigurationsCache.getDcaeConfigurations());
        assertThat(DcaeConfigurationsCache.getPubSecInfo("test").getAafUsername(),
                equalTo(securityInfo.getAafUsername()));
    }

    @Test
    public void testDcaeConfigurationCacheNull() {
        DcaeConfigurationsCache.setDcaeConfigurations(null);
        assertThat(DcaeConfigurationsCache.getPubSecInfo("test"), nullValue());
    }

    @Test
    public void testAddPubSecInfo() {
        DcaeConfigurationsCache.addPubSecInfo("test", new SecurityInfo());
    }
}