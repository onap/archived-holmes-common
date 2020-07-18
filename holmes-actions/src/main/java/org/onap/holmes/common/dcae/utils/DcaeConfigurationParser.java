/*
 * Copyright 2017-2020 ZTE Corporation.
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.onap.holmes.common.dcae.entity.DcaeConfigurations;
import org.onap.holmes.common.dcae.entity.Rule;
import org.onap.holmes.common.dcae.entity.SecurityInfo;
import org.onap.holmes.common.exception.CorrelationException;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import static org.onap.holmes.common.utils.GsonUtil.getAsString;

public class DcaeConfigurationParser {

    private static final String RULE_CONTENT_SPLIT = "\\$\\$\\$";

    private static final List<String> OBJECT_ATTRS = Arrays
            .asList(new String[]{"streams_subscribes", "streams_publishes", "services_calls", "services_provides"});

    public static DcaeConfigurations parse(String jsonStr) throws CorrelationException {
        if (StringUtils.isEmpty(jsonStr)) {
            throw new CorrelationException(
                    "Can not resolve configurations from DCAE. The configuration string is empty.");
        }

        DcaeConfigurations ret = new DcaeConfigurations();

        JsonObject jsonObject = null;
        try {
            jsonObject = JsonParser.parseString(jsonStr).getAsJsonObject();
        } catch (Exception e) {
            throw new CorrelationException(e.getMessage(), e);
        }

        fillInRules(ret, jsonObject);
        fillInPublishesInfo(ret, jsonObject);
        fillInSubscribesInfo(ret, jsonObject);

        JsonObject finalJsonObject = jsonObject;
        Stream.of(jsonObject.keySet().toArray(new String[0]))
                .filter(key -> !OBJECT_ATTRS.contains(key))
                .forEach(key -> ret.put(key, finalJsonObject.get(String.valueOf(key)).getAsString()));
        return ret;
    }

    private static void fillInPublishesInfo(DcaeConfigurations ret, JsonObject jsonObject) {
        if (jsonObject.has("streams_publishes")) {
            JsonObject publishes = jsonObject.get("streams_publishes").getAsJsonObject();
            for (Object key : publishes.keySet()) {
                ret.addPubSecInfo((String) key,
                        createSecurityInfo((String) key, publishes.get((String) key).getAsJsonObject()));
            }
        }
    }

    private static void fillInSubscribesInfo(DcaeConfigurations ret, JsonObject jsonObject) {
        if (jsonObject.has("streams_subscribes")) {
            JsonObject subscribes = jsonObject.get("streams_subscribes").getAsJsonObject();
            for (Object key : subscribes.keySet()) {
                ret.addSubSecInfo((String) key,
                        createSecurityInfo((String) key, subscribes.get((String) key).getAsJsonObject()));
            }
        }
    }

    private static SecurityInfo createSecurityInfo(String key, JsonObject entity) {
        SecurityInfo securityInfo = new SecurityInfo();
        securityInfo.setType(getAsString(entity, "type"));
        securityInfo.setAafPassword(getAsString(entity, "aaf_password"));
        securityInfo.setAafUsername(getAsString(entity, "aaf_username"));
        securityInfo.setSecureTopic(!key.endsWith("unsecure"));
        fillInDmaapInfo(securityInfo, entity.get("dmaap_info").getAsJsonObject());
        return securityInfo;
    }

    private static void fillInDmaapInfo(SecurityInfo securityInfo, JsonObject jsonDmaapInfo) {
        SecurityInfo.DmaapInfo dmaapInfo = securityInfo.getDmaapInfo();
        dmaapInfo.setLocation(getAsString(jsonDmaapInfo, "location"));
        dmaapInfo.setTopicUrl(getAsString(jsonDmaapInfo, "topic_url"));
        dmaapInfo.setClientId(getAsString(jsonDmaapInfo, "client_id"));
        dmaapInfo.setClientRole(getAsString(jsonDmaapInfo, "client_role"));
        dmaapInfo.setType(getAsString(jsonDmaapInfo, "type"));
    }

    private static void fillInRules(DcaeConfigurations ret, JsonObject jsonObject) {
        Set<Entry<String, JsonElement>> entries = jsonObject.entrySet();
        for (Entry<String, JsonElement> entry : entries) {
            if (entry.getKey().startsWith("holmes.default.rule")) {
                String value = entry.getValue().getAsString();
                String[] contents = value.split(RULE_CONTENT_SPLIT);
                ret.addDefaultRule(new Rule(entry.getKey(), contents[0], contents[1], 1));
            }
        }
    }
}
