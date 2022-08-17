/*
 * Copyright 2020-2022 ZTE Corporation.
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
package org.onap.holmes.common.dmaap.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClosedLoopControlNameCache extends ConcurrentHashMap<String, String> {

    private static final Logger log = LoggerFactory.getLogger(ClosedLoopControlNameCache.class);

    @Override
    public String put(String packageName, String controlLoopName) {
        log.info(String.format("<%s:%s> was added into ClosedLoopControlNameCache.", packageName, controlLoopName));
        return super.put(packageName, controlLoopName);
    }

    public String remove(String packageName) {
        log.info(String.format("<%s> was removed from ClosedLoopControlNameCache.", packageName));
        return super.remove(packageName);
    }
}
