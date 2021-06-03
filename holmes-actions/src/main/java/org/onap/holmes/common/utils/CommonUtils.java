/**
 * Copyright 2021 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.onap.holmes.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;

import java.util.regex.Pattern;

@Slf4j
@Service
public class CommonUtils {

    final static public String IP_REG = "(http(s)?://)?(\\d+\\.\\d+\\.\\d+\\.\\d+)(:(\\d+))?";

    public static boolean isHttpsEnabled() {
        return Boolean.valueOf(getEnv("ENABLE_ENCRYPT"));
    }

    public static String getEnv(String name) {
        String value = System.getenv(name);
        if (value == null) {
            value = System.getProperty(name);
        }
        return value;
    }

    public static boolean isIpAddress(String info) {
        return Pattern.compile(IP_REG).matcher(info).matches();
    }

}
