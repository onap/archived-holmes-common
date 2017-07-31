/**
 * Copyright 2016 ZTE Corporation.
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


package org.openo.holmes.common.utils;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;
import org.junit.Test;

public class JudgeNullUtilTest {

    @Test
    public void isEmpty_array_length_zero() {
        boolean resultShorts = JudgeNullUtil.isEmpty(new short[]{});
        boolean resultInts = JudgeNullUtil.isEmpty(new int[]{});
        boolean resultLongs = JudgeNullUtil.isEmpty(new long[]{});
        boolean resultObjects = JudgeNullUtil.isEmpty(new Object[]{});
        boolean resultStrings = JudgeNullUtil.isEmpty(new String[]{});
        boolean resultLists = JudgeNullUtil.isEmpty(new List[]{});
        assertThat(true, equalTo(resultShorts));
        assertThat(true, equalTo(resultInts));
        assertThat(true, equalTo(resultLongs));
        assertThat(true, equalTo(resultObjects));
        assertThat(true, equalTo(resultStrings));
        assertThat(true, equalTo(resultLists));
    }
}