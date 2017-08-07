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

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.onap.holmes.common.dcae.entity.DcaeConfigurations;
import org.onap.holmes.common.dcae.entity.Rule;
import org.onap.holmes.common.dcae.entity.SecurityInfo;
import org.onap.holmes.common.exception.CorrelationException;

public class DcaeConfigurationParser {

    private static final List<String> OBJECT_ATTRS = Arrays
            .asList(new String[]{"streams_subscribes", "streams_publishes", "services_calls", "services_provides"});

    public static DcaeConfigurations parse(String jsonStr) throws CorrelationException {
        if (StringUtils.isEmpty(jsonStr)) {
            throw new CorrelationException(
                    "Can not resolve configurations from DCAE. The configuration string is empty");
        }

        DcaeConfigurations ret = new DcaeConfigurations();

        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.fromObject(jsonStr);
        } catch (Exception e) {
            throw new CorrelationException(e.getMessage(), e);
        }

        fillInRules(ret, jsonObject);
        fillInPublishesInfo(ret, jsonObject);

        if (jsonObject.containsKey("streams_subscribes")) {

        }

        JSONObject finalJsonObject = jsonObject;
        Stream.of(jsonObject.keySet().toArray(new String[0]))
                .filter(key -> !OBJECT_ATTRS.contains(key))
                .forEach(key -> ret.put(key.toString(), finalJsonObject.getString(String.valueOf(key))));
        return ret;
    }

    private static void fillInPublishesInfo(DcaeConfigurations ret, JSONObject jsonObject) {
        if (jsonObject.containsKey("streams_publishes")) {
            JSONObject publishes = jsonObject.getJSONObject("streams_publishes");
            for (Object key : publishes.keySet()) {
                ret.addPubSecInfo((String) key,
                        createSecurityInfo((String) key, publishes.getJSONObject((String) key)));
            }
        }
    }

    private static SecurityInfo createSecurityInfo(String key, JSONObject entity) {
        SecurityInfo securityInfo = new SecurityInfo();
        securityInfo.setType(entity.getString("type"));
        if (!entity.get("aaf_password").equals("null")) {
            securityInfo.setAafPassword(entity.getString("aaf_password"));
        }
        if (!entity.get("aaf_username").equals("null")) {
            securityInfo.setAafUsername(entity.getString("aaf_username"));
        }
        securityInfo.setSecureTopic(!key.endsWith("unsecure"));
        fillInDmaapInfo(securityInfo, entity.getJSONObject("dmaap_info"));
        return securityInfo;
    }

    private static void fillInDmaapInfo(SecurityInfo securityInfo, JSONObject jsonDmaapInfo) {
        SecurityInfo.DmaapInfo dmaapInfo = securityInfo.getDmaapInfo();
        dmaapInfo.setLocation(jsonDmaapInfo.getString("location"));
        dmaapInfo.setTopicUrl(jsonDmaapInfo.getString("topic_url"));
        if (!jsonDmaapInfo.get("client_id").equals("null")) {
            dmaapInfo.setClientId(jsonDmaapInfo.getString("client_id"));
        }
        if (!jsonDmaapInfo.get("client_role").equals("null")) {
            dmaapInfo.setClientRole(jsonDmaapInfo.getString("client_role"));
        }
    }

    private static void fillInRules(DcaeConfigurations ret, JSONObject jsonObject) {
        Set<Entry<String, Object>> entries = jsonObject.entrySet();
        for (Entry<String, Object> entry : entries) {
            if (entry.getKey().startsWith("holmes.default.rule")) {
                ret.addDefaultRule(new Rule(entry.getKey(), (String) entry.getValue()));
            }
        }
    }
}
