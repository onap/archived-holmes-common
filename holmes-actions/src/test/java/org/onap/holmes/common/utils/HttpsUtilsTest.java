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

package org.onap.holmes.common.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.holmes.common.exception.CorrelationException;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import sun.net.www.http.HttpClient;

@PrepareForTest({HttpsUtils.class, CloseableHttpClient.class})
@RunWith(PowerMockRunner.class)
public class HttpsUtilsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private HttpsUtils httpsUtils;

    @Before
    public void setUp() {
        httpsUtils = new HttpsUtils();
    }

    @Test
    public void testHttpsUtil_get_excepiton() throws Exception {
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to use get method query data from server");
        String url = "host";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");
        Map<String, String> para = new HashMap<>();
        para.put("tset", "1111");
        String response = HttpsUtils.get(url, header);
        assertThat(response, equalTo(""));
    }

    @Test
    public void testHttpsUtil_post_excepiton() throws Exception {
        thrown.expect(CorrelationException.class);
        thrown.expectMessage("Failed to use post method query data from server");
        String url = "host";
        Map<String, String> header = new HashMap<>();
        header.put("accept", "application/json");
        Map<String, String> para = new HashMap<>();
        para.put("tset", "1111");
        String response = HttpsUtils.post(url, header, para, null);
        assertThat(response, equalTo(""));
    }
}