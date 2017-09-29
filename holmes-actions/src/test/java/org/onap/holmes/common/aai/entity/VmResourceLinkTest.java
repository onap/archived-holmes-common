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
package org.onap.holmes.common.aai.entity;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class VmResourceLinkTest {

    private VmResourceLink vmResourceLink;

    @Before
    public void setUp() {
        vmResourceLink = new VmResourceLink();
    }

    @Test
    public void getterAndSetter_resourceLink() {
        String resourceLink = "test";
        vmResourceLink.setResourceLink(resourceLink);
        assertThat(resourceLink, equalTo(vmResourceLink.getResourceLink()));
    }

    @Test
    public void getterAndSetter_resourceType() {
        String resourceType = "test1";
        vmResourceLink.setResourceType("test1");
        assertThat(resourceType, equalTo(vmResourceLink.getResourceType()));
    }
}