/**
 * Copyright 2021 ZTE Corporation.
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

package org.onap.holmes.common;

import org.jvnet.hk2.annotations.Contract;
import org.onap.holmes.common.utils.MsbRegister;
import org.onap.msb.sdk.discovery.entity.MicroServiceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Contract
abstract public class AbstractServiceInitializer {

    @Inject
    private MsbRegister msbRegister;

    private final Logger logger = LoggerFactory.getLogger(AbstractServiceInitializer.class);

    public AbstractServiceInitializer() {
    }

    @PostConstruct
    private void initialize() {
        try {
            msbRegister.register2Msb(createMicroServiceInfo());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    abstract protected MicroServiceInfo createMicroServiceInfo();
}
