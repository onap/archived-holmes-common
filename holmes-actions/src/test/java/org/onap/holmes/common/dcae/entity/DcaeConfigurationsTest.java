/*
 * Copyright 2020 ZTE Corporation.
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

package org.onap.holmes.common.dcae.entity;

import org.junit.Test;

import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class DcaeConfigurationsTest {

    @Test
    public void testAddDefaultRule_null_param() throws Exception {
        DcaeConfigurations dcaeConfigurations = new DcaeConfigurations();
        dcaeConfigurations.addDefaultRule(null);
        assertThat(dcaeConfigurations.getDefaultRules().size(), is(0));
    }

    @Test
    public void testGettersAndSetters() throws Exception {
        DcaeConfigurations dcaeConfigurations = new DcaeConfigurations();
        dcaeConfigurations.addSubSecInfo("test", new SecurityInfo());
        assertThat(dcaeConfigurations.getSubSecInfo("test"), notNullValue());

        Set<String> keys = dcaeConfigurations.getSubKeys();
        assertThat(keys.contains("test"), is(true));
    }
}