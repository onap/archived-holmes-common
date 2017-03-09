/**
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
package org.openo.holmes.common.constant;

public interface AlarmConst {

    String MQ_TOPIC_NAME_ALARMS_CORRELATION = "topic://voss/fm/alarms_correlation";

    String MQ_TOPIC_NAME_ALARM = "topic://voss/fm/alarm";

    String NFVO_PATH = "/openoapi/umc/v1/fm/curalarms/findAll";

    int NFVO_STATUS_OK = 200;

    String I18N_EN = "en";

    String I18N_ZH = "zh";

    String ZH_CN = "zh_CN";

    String ADMIN = "admin";

    int MICRO_SERVICE_STATUS_SUCCESS = 201;

    int MICRO_SERVICE_PORT = 8086;

    String HTTP = "http://";
}
