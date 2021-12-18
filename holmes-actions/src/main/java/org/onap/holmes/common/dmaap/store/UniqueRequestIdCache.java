/*
 * Copyright 2020-2021 ZTE Corporation.
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

import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Singleton
public class UniqueRequestIdCache extends ConcurrentHashMap<String, String> {

    private static final Logger log = LoggerFactory.getLogger(UniqueRequestIdCache.class);

    @Override
    public String put(String alarmId, String requestId) {
        log.info(String.format("<%s:%s> was added into UniqueRequestIdCache.", alarmId, requestId));
        return super.put(alarmId, requestId);
    }

    public String remove(String alarmId) {
        log.info(String.format("<%s> was removed into UniqueRequestIdCache.", alarmId));
        return super.remove(alarmId);
    }
}
