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

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.openo.baseservice.i18n.I18n;

@Slf4j
public class I18nProxy {

    /*-----------------------Engine------------------------------- */
    public static final String ENGINE_CONTENT_ILLEGALITY = "ENGINE_CONTENT_ILLEGALITY";
    public static final String ENGINE_DEPLOY_RULE_FAILED = "ENGINE_DEPLOY_RULE_FAILED";
    public static final String ENGINE_DELETE_RULE_NULL = "ENGINE_DELETE_RULE_NULL";
    public static final String ENGINE_DELETE_RULE_FAILED = "ENGINE_DELETE_RULE_FAILED";
    public static final String ENGINE_INSERT_RULE_CACHE_FAILED = "ENGINE_INSERT_RULE_CACHE_FAILED";
    public static final String ENGINE_DELETE_RULE_FAILED_FROM_CACHE = "ENGINE_DELETE_RULE_FAILED_FROM_CACHE";
    public static final String ENGINE_CONTAINS_PACKAGE = "ENGINE_CONTAINS_PACKAGE";
    public static final String ENGINE_QUERY_CACHE_FAILED = "ENGINE_QUERY_CACHE_FAILED";
    /*-----------------------Rule Management------------------------------- */
    public static final String RULE_MANAGEMENT_CALL_DELETE_RULE_REST_FAILED = "RULE_MANAGEMENT_CALL_DELETE_RULE_REST_FAILED";
    public static final String RULE_MANAGEMENT_CALL_DEPLOY_RULE_REST_FAILED = "RULE_MANAGEMENT_CALL_DEPLOY_RULE_REST_FAILED";
    public static final String RULE_MANAGEMENT__CALL_CHECK_RULE_REST_FAILED = "RULE_MANAGEMENT__CALL_CHECK_RULE_REST_FAILED";
    public static final String RULE_MANAGEMENT_CREATE_QUERY_SQL_FAILED = "RULE_MANAGEMENT_CREATE_QUERY_SQL_FAILED";
    public static final String RULE_MANAGEMENT_QUERY_RULE_FAILED = "RULE_MANAGEMENT_QUERY_RULE_FAILED";
    public static final String RULE_MANAGEMENT_CREATE_RULE_FAILED = "RULE_MANAGEMENT_CREATE_RULE_FAILED";
    public static final String RULE_MANAGEMENT_DELETE_RULE_FAILED = "RULE_MANAGEMENT_DELETE_RULE_FAILED";
    public static final String RULE_MANAGEMENT_UPDATE_RULE_FAILED = "RULE_MANAGEMENT_UPDATE_RULE_FAILED";
    public static final String RULE_MANAGEMENT_REQUEST_OBJECT_IS_EMPTY = "RULE_MANAGEMENT_REQUEST_OBJECT_IS_EMPTY";
    public static final String RULE_MANAGEMENT_RULE_NAME_IS_EMPTY = "RULE_MANAGEMENT_RULE_NAME_IS_EMPTY";
    public static final String RULE_MANAGEMENT_RULE_NOT_EXIST_DATABASE = "RULE_MANAGEMENT_RULE_NOT_EXIST_DATABASE";
    public static final String RULE_MANAGEMENT_UNKNOWN_EXCEPTION = "RULE_MANAGEMENT_UNKNOWN_EXCEPTION";
    public static final String RULE_MANAGEMENT_REPEAT_RULE_NAME = "RULE_MANAGEMENT_REPEAT_RULE_NAME";
    public static final String RULE_MANAGEMENT_DATA_FORMAT_ERROR = "RULE_MANAGEMENT_DATA_FORMAT_ERROR";
    public static final String RULE_MANAGEMENT_PARAMETER_ENABLED_ERROR = "RULE_MANAGEMENT_PARAMETER_ENABLED_ERROR";

    private Optional<I18n> optional = null;

    private I18nProxy() {
        optional = I18n.getInstance("correlation");
    }

    private static class I18nProxyHolder {

        private static final I18nProxy INSTANCE = new I18nProxy();

        private I18nProxyHolder() {

        }
    }

    public static I18nProxy getInstance() {
        return I18nProxyHolder.INSTANCE;
    }

    public String getValue(Locale locale, String key) {
        return optional.get().getLabelValue(key, locale);
    }

    public String getValueByArgs(Locale locale, String key, String[] arguments) {
        return optional.get().getLabelValue(key, locale, arguments);
    }

    public Map<String, String> getValue(String key) {
        return optional.get().getLabelValues(key);
    }

    public String jsonI18n(String key) {

        return optional.get().getCanonicalLabelValues(key);
    }

    public String i18nWithArgs(String key, String[] args) {
        String value = "";
        try {
            value = JacksonUtil.beanToJson(optional.get().getLabelValues(key, args));
        } catch (JsonProcessingException e) {
            log.info("get i18n error, key is :" + key, e);
        } catch (IllegalArgumentException e) {
            log.info("get i18n error IllegalArgumentException, key is :" + key + ",args is :  " + Arrays.toString(args),
                    e);
        }

        return value;
    }

}
