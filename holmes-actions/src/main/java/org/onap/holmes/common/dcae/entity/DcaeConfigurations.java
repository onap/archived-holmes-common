/**
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

package org.onap.holmes.common.dcae.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.NoArgsConstructor;

public class DcaeConfigurations extends HashMap<String, Object>{

    private static final String STREAMS_PUBLISHES = "streamsPublishes";
    private static final String STREAMS_SUBSCRIBES = "streamsSubscribes";
    private static final String RULES = "rules";

    public DcaeConfigurations(){
        super();
        this.put(STREAMS_PUBLISHES, new HashMap<String, SecurityInfo>());
        this.put(STREAMS_SUBSCRIBES, new HashMap<String, SecurityInfo>());
        this.put(RULES, new ArrayList<Rule>());
    }

    public void addDefaultRule(Rule rule) {
        if (null == rule) {
            return;
        }
        ((List<Rule>)(this.get(RULES))).add(rule);
    }

    public List<Rule> getDefaultRules() {
        return (List<Rule>)(this.get(RULES));
    }

    public SecurityInfo addPubSecInfo(String key, SecurityInfo value) {
        return ((Map<String, SecurityInfo>)(this.get(STREAMS_PUBLISHES))).put(key, value);
    }

    public SecurityInfo getPubSecInfo(String key) {
        return ((Map<String, SecurityInfo>)(this.get(STREAMS_PUBLISHES))).get(key);
    }

    public SecurityInfo addSubSecInfo(String key, SecurityInfo value) {
        return ((Map<String, SecurityInfo>)(this.get(STREAMS_SUBSCRIBES))).put(key, value);
    }

    public SecurityInfo getSubSecInfo(String key) {
        return ((Map<String, SecurityInfo>)(this.get(STREAMS_SUBSCRIBES))).get(key);
    }

    public Set<String> getSubKeys(){
        return ((Map<String, SecurityInfo>)(this.get(STREAMS_SUBSCRIBES))).keySet();
    }
}
