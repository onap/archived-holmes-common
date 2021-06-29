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

package org.onap.holmes.common.utils.transactionid;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class TransactionIdUtilsTest {

    @Test
    public void validate_is_uuid() {
        final String uuid = TransactionIdUtils.getUUID();
        assertThat(TransactionIdUtils.validate(uuid), equalTo(uuid));
    }

    @Test
    public void validate_is_not_uuid() {
        assertThat(TransactionIdUtils.validate("a-random-string"), is(nullValue()));
    }

    @Test
    public void validate_contains_uuid() {
        final String uuid = "test" + TransactionIdUtils.getUUID();
        assertThat(TransactionIdUtils.validate(uuid), is(nullValue()));
    }
}