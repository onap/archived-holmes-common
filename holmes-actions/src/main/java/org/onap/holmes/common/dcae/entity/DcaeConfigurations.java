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
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DcaeConfigurations extends HashMap<String, Object>{
    private Map<String, SecurityInfo> streamsPublishes = new HashMap<>();
    private Map<String, SecurityInfo> streamsSubscribes = new HashMap<>();
    private List<Rule> rules = new ArrayList<>();

    public void addDefaultRule(Rule rule) {
        if (null == rule) {
            return;
        }
        this.rules.add(rule);
    }

    public List<Rule> getDefaultRules() {
        return this.rules;
    }

    public SecurityInfo addPubSecInfo(String key, SecurityInfo value) {
        return this.streamsPublishes.put(key, value);
    }

    public SecurityInfo getPubSecInfo(String key) {
        return this.streamsPublishes.get(key);
    }

    public SecurityInfo addSubSecInfo(String key, SecurityInfo value) {
        return this.streamsSubscribes.put(key, value);
    }

    public SecurityInfo getSubSecInfo(String key) {
        return this.streamsSubscribes.get(key);
    }
}
