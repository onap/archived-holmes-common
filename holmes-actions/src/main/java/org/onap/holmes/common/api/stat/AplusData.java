/**
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.holmes.common.api.stat;

import java.io.Serializable;

public interface AplusData extends Serializable {

    /**
     * The data to be analyzed with aging characteristics - 0
     */
    byte APLUS_EVENT = 0;

    /**
     * The data to be analyzed without aging characteristics - 1
     */
    byte APLUS_FACT = 1;


    /**
     * @see #APLUS_EVENT
     * @see #APLUS_FACT
     */
    byte getDataType();

    /**
     * @return String
     */
    String getObjectId();
}
