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

package org.onap.holmes.common.dcae.utils;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.Test;
import org.onap.holmes.common.dcae.entity.DcaeConfigurations;
import org.onap.holmes.common.dcae.entity.SecurityInfo;

public class DcaeConfigurationParserTest {

    @Test
    public void parse() throws Exception {
        DcaeConfigurations obj = DcaeConfigurationParser.parse(readConfigurationsFromFile("dcae.config.json"));

        assertThat(obj.getDefaultRules().size(), equalTo(1));
        assertThat(obj.get("collector.keystore.alias"), equalTo("dynamically generated"));
        assertThat(((SecurityInfo) obj.getPubSecInfo("sec_measurement")).getAafPassword(), equalTo("aaf_password"));
        assertThat(((SecurityInfo) obj.getPubSecInfo("sec_measurement")).getDmaapInfo().getLocation(), equalTo("mtl5"));
    }

    private String readConfigurationsFromFile(String fileName) throws URISyntaxException, FileNotFoundException {
        URL url = DcaeConfigurationParserTest.class.getClassLoader().getResource(fileName);
        File configFile = new File(new URI(url.toString()).getPath());
        BufferedReader br = new BufferedReader(new FileReader(configFile));

        final StringBuilder sb = new StringBuilder();
        br.lines().forEach(line -> {
            sb.append(line);
        });
        try {
            br.close();
        } catch (IOException e) {
            // Do nothing
        }
        return sb.toString();
    }

}