/**
 * Copyright 2021 ZTE Corporation.
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

package org.onap.holmes.common;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
public class ConfigFileScannerTest {

    @Rule
    ExpectedException thrown = ExpectedException.none();

    @Test
    public void testScan_file_normal() {
        ConfigFileScanner scanner = new ConfigFileScanner();
        String file = ConfigFileScannerTest.class.getResource("/ConfigFileScannerTestData.txt").getFile();
        Map<String, String> files = scanner.scan(file);
        assertThat(files.values().iterator().next(), equalTo("isTest=true"));
    }

    @Test
    public void testScan_file_empty() {
        ConfigFileScanner scanner = new ConfigFileScanner();
        String file = ConfigFileScannerTest.class.getResource("/ConfigFileScannerEmpty.txt").getFile();
        Map<String, String> files = scanner.scan(file);
        assertThat(files.size(), is(0));
    }

    @Test
    public void testScan_directory_normal() {
        ConfigFileScanner scanner = new ConfigFileScanner();
        String file = ConfigFileScannerTest.class.getResource("/").getFile();
        Map<String, String> files = scanner.scan(file);
        assertThat(files.size(), is(5));
    }
}