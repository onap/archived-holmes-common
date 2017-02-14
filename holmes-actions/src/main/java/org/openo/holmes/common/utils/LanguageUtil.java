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

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.openo.holmes.common.constant.AlarmConst;

public class LanguageUtil {

    private LanguageUtil() {
    }

    public static String getLanguage(HttpServletRequest servletRequest) {
        String language = (String) servletRequest.getHeader("language-option");
        if (JudgeNullUtil.isEmpty(language)) {
            language = AlarmConst.ZH_CN;
        }
        if (language.startsWith(AlarmConst.I18N_ZH)) {
            language = AlarmConst.I18N_ZH;
        } else if (language.startsWith(AlarmConst.I18N_EN)) {
            language = AlarmConst.I18N_EN;
        }
        return language;
    }

    public static Locale getLocale(HttpServletRequest servletRequest) {
        String language = (String) servletRequest.getHeader("language-option");
        if (JudgeNullUtil.isEmpty(language)) {
            language = AlarmConst.ZH_CN;
        }
        if (language.startsWith(AlarmConst.I18N_ZH)) {
            language = AlarmConst.I18N_ZH;
        } else if (language.startsWith(AlarmConst.I18N_EN)) {
            language = AlarmConst.I18N_EN;
        }
        Locale locale = new Locale(language);
        return locale;
    }
}
