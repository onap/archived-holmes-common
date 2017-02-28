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


package org.openo.holmes.common.utils;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.openo.holmes.common.constant.AlarmConst;
import org.powermock.api.easymock.PowerMock;


public class LanguageUtilTest {

    private HttpServletRequest request;

    @Before
    public void setUp() {
        request = PowerMock.createMock(HttpServletRequest.class);
    }

    @Test
    public void getLanguage_header_language_empty() {
        EasyMock.expect(request.getHeader("language-option")).andReturn("");

        PowerMock.replayAll();

        String language = LanguageUtil.getLanguage(request);

        PowerMock.verifyAll();

        assertThat(AlarmConst.I18N_EN, equalTo(language));
    }

    @Test
    public void getLanguage_zh() {
        EasyMock.expect(request.getHeader("language-option")).andReturn(AlarmConst.I18N_ZH);

        PowerMock.replayAll();

        String language = LanguageUtil.getLanguage(request);

        PowerMock.verifyAll();

        assertThat(AlarmConst.I18N_ZH, equalTo(language));
    }

    @Test
    public void getLanguage_en() {
        EasyMock.expect(request.getHeader("language-option")).andReturn(AlarmConst.I18N_EN);

        PowerMock.replayAll();

        String language = LanguageUtil.getLanguage(request);

        PowerMock.verifyAll();

        assertThat(AlarmConst.I18N_EN, equalTo(language));
    }

    @Test
    public void getLocale_header_language_empty() {
        EasyMock.expect(request.getHeader("language-option")).andReturn("");

        PowerMock.replayAll();

        Locale locale = LanguageUtil.getLocale(request);

        PowerMock.verifyAll();

        assertThat(new Locale(AlarmConst.I18N_EN), equalTo(locale));
    }

    @Test
    public void getLocale_zh() {
        EasyMock.expect(request.getHeader("language-option")).andReturn(AlarmConst.I18N_ZH);

        PowerMock.replayAll();

        Locale locale = LanguageUtil.getLocale(request);

        PowerMock.verifyAll();

        assertThat(new Locale(AlarmConst.I18N_ZH), equalTo(locale));
    }

    @Test
    public void getLocale_en() {
        EasyMock.expect(request.getHeader("language-option")).andReturn(AlarmConst.I18N_EN);

        PowerMock.replayAll();

        Locale locale = LanguageUtil.getLocale(request);

        PowerMock.verifyAll();

        assertThat(new Locale(AlarmConst.I18N_EN), equalTo(locale));
    }
}