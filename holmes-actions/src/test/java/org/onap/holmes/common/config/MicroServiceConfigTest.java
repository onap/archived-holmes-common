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

package org.onap.holmes.common.config;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MicroServiceConfigTest {

    @Test
    public void getMsbServerAddrTest() {
        System.setProperty("MSB_ADDR", "test");
        assertThat("http://test", equalTo(MicroServiceConfig.getMsbServerAddr()));
        System.clearProperty("MSB_ADDR");
    }

    @Test
    public void getMsbServerIpTest() {
        System.setProperty("MSB_ADDR", "10.54.23.79");
        assertThat("10.54.23.79", equalTo(MicroServiceConfig.getMsbServerIp()));
        System.clearProperty("MSB_ADDR");
    }

    @Test
    public void getMsbPortTest() {
        System.setProperty("MSB_PORT", "110");
        assertTrue(110 == MicroServiceConfig.getMsbServerPort());
        System.clearProperty("MSB_PORT");
    }

    @Test
    public void getMsbPortTestNonnumeric() {
        System.setProperty("MSB_PORT", "test");
        assertTrue(80 == MicroServiceConfig.getMsbServerPort());
        System.clearProperty("MSB_PORT");
    }

    @Test
    public void getMsbPortTestNullValue() {
        assertTrue(80 == MicroServiceConfig.getMsbServerPort());
    }

    @Test
    public void getServiceIpTest() {
        System.setProperty("SERVICE_IP", "test");
        assertThat("test", equalTo(MicroServiceConfig.getServiceIp()));
        System.clearProperty("SERVICE_IP");
    }
}