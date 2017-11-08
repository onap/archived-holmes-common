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

package org.onap.holmes.common.utils;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.onap.holmes.common.dcae.entity.DcaeConfigurations;
import org.onap.holmes.common.dcae.entity.SecurityInfo;

public class Md5UtilTest {
    @Test
    public void testMd5NormalDiff(){
        String contents1 = "contents1";
        String contents2 = "contents2";

        assertThat(Md5Util.md5(contents1), not(equalTo(Md5Util.md5(contents2))));
    }

    @Test
    public void testMd5NormalSame(){
        String contents1 = "contents";
        String contents2 = "contents";

        assertThat(Md5Util.md5(contents1), equalTo(Md5Util.md5(contents2)));
    }

    @Test
    public void testMd5Null(){
        String contents1 = null;
        String contents2 = null;

        assertThat(Md5Util.md5(contents1), equalTo(Md5Util.md5(contents2)));
    }

    @Test
    public void testMd5ObjDiff(){
        DcaeConfigurations config1 = new DcaeConfigurations();
        DcaeConfigurations config2 = new DcaeConfigurations();

        config1.addPubSecInfo("config1", new SecurityInfo());
        config2.addPubSecInfo("config2", new SecurityInfo());

        try {
            assertThat(Md5Util.md5(config1), not(equalTo(Md5Util.md5(config2))));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMd5ObjSame(){
        DcaeConfigurations config1 = new DcaeConfigurations();
        DcaeConfigurations config2 = new DcaeConfigurations();

        config1.addPubSecInfo("config", new SecurityInfo());
        config2.addPubSecInfo("config", new SecurityInfo());

        try {
            assertThat(Md5Util.md5(config1), equalTo(Md5Util.md5(config2)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}