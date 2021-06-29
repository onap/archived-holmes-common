/**
 * Copyright 2018 ZTE Corporation.
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
package org.onap.holmes.common.utils.transactionid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionIdUtils {
    public static final String REQUEST_ID_HEADER = "X-TransactionID";
    public static final String INVOCATIONIDID_HEADER = "X-InvocationID";
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$");

    public static String getUUID() {
        return java.util.UUID.randomUUID().toString();
    }

    public static String validate(String uuid) {
        Matcher matcher = UUID_PATTERN.matcher(uuid);
        if (matcher.matches()) {
            return uuid;
        }

        return null;
    }
}
