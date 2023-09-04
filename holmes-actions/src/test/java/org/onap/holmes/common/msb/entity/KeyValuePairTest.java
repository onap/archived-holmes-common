/**
 * Copyright 2023 ZTE Corporation.
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

package org.onap.holmes.common.msb.entity;

import org.junit.Test;

import static org.junit.Assert.*;

public class KeyValuePairTest {

    @Test
    public void testGettersAndSetters() {
        KeyValuePair pair = new KeyValuePair();
        pair.setKey("myKey");
        pair.setValue("myValue");

        assertEquals("myKey", pair.getKey());
        assertEquals("myValue", pair.getValue());
    }

    @Test
    public void testConstructor() {
        KeyValuePair pair = new KeyValuePair("key1", "value1");

        assertEquals("key1", pair.getKey());
        assertEquals("value1", pair.getValue());
    }

}