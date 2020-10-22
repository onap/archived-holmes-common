/**
 * Copyright 2020 ZTE Corporation.
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

package org.onap.holmes.common.utils;

import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;

public class JerseyClientTest {

    private JerseyClient jerseyClient = new JerseyClient();

    @Test
    public void http() {
        jerseyClient.httpClient();
    }

    @Test
    public void https() throws Exception {
        WhiteboxImpl.invokeMethod(jerseyClient, "init");
        jerseyClient.httpsClient();
    }

    @Test
    public void clientHttp() {
        jerseyClient.client(false);
    }

    @Test
    public void clientHttps() throws Exception {
        WhiteboxImpl.invokeMethod(jerseyClient, "init");
        jerseyClient.client(true);
    }
}