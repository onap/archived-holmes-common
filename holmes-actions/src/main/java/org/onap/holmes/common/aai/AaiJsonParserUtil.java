/*-
 * ============LICENSE_START=======================================================
 * org.onap.holmes.common.aai
 * ================================================================================
 * Copyright (C) 2018-2021 Huawei, ZTE. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.holmes.common.aai;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.onap.holmes.common.config.MicroServiceConfig;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Component
public class AaiJsonParserUtil {

    public static String getPath(String urlTemplate, String paramName, String paramValue) {
        return urlTemplate.replaceAll("\\{" + paramName + "\\}", paramValue);
    }

    public static String getPath(String serviceInstancePath) {
        Pattern pattern = Pattern.compile("/aai/(v\\d+)/([A-Za-z0-9\\-]+[^/])(/*.*)");
        Matcher matcher = pattern.matcher(serviceInstancePath);
        String ret = "/api";
        if (matcher.find()) {
            ret += "/aai-" + matcher.group(2) + "/" + matcher.group(1) + matcher.group(3);
        }

        return ret;
    }

    public static JsonObject getInfo(String response, String field) {
        JsonObject jObject = JsonParser.parseString(response).getAsJsonObject();
        JsonObject relationshipList = extractJsonObject(jObject, "relationship-list");
        JsonArray relationShip = extractJsonArray(relationshipList, "relationship");
        if (relationShip != null) {
            for (int i = 0; i < relationShip.size(); ++i) {
                final JsonObject object = relationShip.get(i).getAsJsonObject();
                if (object.get("related-to").getAsString().equals(field)) {
                    return object;
                }
            }
        }
        return null;
    }

    public static JsonObject extractJsonObject(JsonObject obj, String key) {
        if (obj != null && key != null && obj.has(key)) {
            return obj.get(key).getAsJsonObject();
        }
        return null;
    }

    public static JsonArray extractJsonArray(JsonObject obj, String key) {
        if (obj != null && key != null && obj.has(key)) {
            return obj.get(key).getAsJsonArray();
        }
        return null;
    }

    public static String getHostAddr() {
        return MicroServiceConfig.getMsbServerAddrWithHttpPrefix();
    }
}
