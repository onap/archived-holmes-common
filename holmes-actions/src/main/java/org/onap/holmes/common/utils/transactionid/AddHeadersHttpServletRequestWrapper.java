/**
 * Copyright 2018-2023 ZTE Corporation.
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
package org.onap.holmes.common.utils.transactionid;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

public class AddHeadersHttpServletRequestWrapper extends HttpServletRequestWrapper {
    final private Map<String, String> additionalHeaders = new HashMap<>();

    public AddHeadersHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String name, String value) {
        additionalHeaders.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String header = additionalHeaders.get(name);
        return (header != null) ? header : super.getHeader(name);
    }

}
