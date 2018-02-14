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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.onap.holmes.common.dcae.entity.DcaeConfigurations;
import org.onap.holmes.common.dcae.entity.Rule;
import org.onap.holmes.common.dcae.entity.SecurityInfo;
import org.onap.holmes.common.exception.CorrelationException;

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

        JSONObject jsonObject = null;
        try {
//            jsonObject = JSONObject.fromObject(jsonStr);
            jsonObject = JSON.parseObject(jsonStr);
        } catch (Exception e) {
            throw new CorrelationException(e.getMessage(), e);
        }

        fillInRules(ret, jsonObject);
        fillInPublishesInfo(ret, jsonObject);
        fillInSubscribesInfo(ret, jsonObject);

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

    private static void fillInSubscribesInfo(DcaeConfigurations ret, JSONObject jsonObject) {
        if (jsonObject.containsKey("streams_subscribes")) {
            JSONObject subscribes = jsonObject.getJSONObject("streams_subscribes");
            for (Object key : subscribes.keySet()) {
                ret.addSubSecInfo((String) key,
                        createSecurityInfo((String) key, subscribes.getJSONObject((String) key)));
            }
        }
    }

    private static SecurityInfo createSecurityInfo(String key, JSONObject entity) {
        SecurityInfo securityInfo = new SecurityInfo();
        if (entity.containsKey("type")) {
            securityInfo.setType(entity.getString("type"));
        }
        if (entity.containsKey("aaf_password")) {
            securityInfo.setAafPassword(entity.getString("aaf_password"));
        }
        if (entity.containsKey("aaf_username")) {
            securityInfo.setAafUsername(entity.getString("aaf_username"));
        }
        securityInfo.setSecureTopic(!key.endsWith("unsecure"));
        fillInDmaapInfo(securityInfo, entity.getJSONObject("dmaap_info"));
        return securityInfo;
    }

    private static void fillInDmaapInfo(SecurityInfo securityInfo, JSONObject jsonDmaapInfo) {
        SecurityInfo.DmaapInfo dmaapInfo = securityInfo.getDmaapInfo();
        if (jsonDmaapInfo.containsKey("location")){
            dmaapInfo.setLocation(jsonDmaapInfo.getString("location"));
        }
        if (jsonDmaapInfo.containsKey("topic_url")) {
            dmaapInfo.setTopicUrl(jsonDmaapInfo.getString("topic_url"));
        }
        if (jsonDmaapInfo.containsKey("client_id")) {
            dmaapInfo.setClientId(jsonDmaapInfo.getString("client_id"));
        }
        if (jsonDmaapInfo.containsKey("client_role")) {
            dmaapInfo.setClientRole(jsonDmaapInfo.getString("client_role"));
        }
        if (jsonDmaapInfo.containsKey("type")) {
            dmaapInfo.setType(jsonDmaapInfo.getString("type"));
        }
    }

    private static void fillInRules(DcaeConfigurations ret, JSONObject jsonObject) {
        Set<Entry<String, Object>> entries = jsonObject.entrySet();
        for (Entry<String, Object> entry : entries) {
            if (entry.getKey().startsWith("holmes.default.rule")) {
                String value = (String) entry.getValue();
                String[] contents = value.split(RULE_CONTENT_SPLIT);
                ret.addDefaultRule(new Rule(entry.getKey(), contents[0], contents[1], 1));
            }
        }
    }
}
