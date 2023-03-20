/*
 * Copyright 2017-2023 ZTE Corporation.
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

package org.onap.holmes.common.utils;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

public class Md5Util {

    private static HashFunction hf = Hashing.md5();
    private static Charset defaultCharset = Charset.forName("UTF-8");

    private Md5Util() {

    }

    public static String md5(String data) {
        String actualData = data == null ? "" : data;
        HashCode hash = hf.newHasher().putString(actualData, defaultCharset).hash();
        return hash.toString();
    }

    public static String md5(Object data) {
        String actualData = data == null ? "{}" : GsonUtil.beanToJson(data);
        return md5(actualData);
    }
}
