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
package org.openo.holmes.common.constant;

public interface AlarmConst {

    public static final String MQ_QUEUE_NAME_EMF_UP = "queue://zenap/fm/emf_up";

    public static final String MQ_TOPIC_NAME_EMF_DOWN = "topic://zenap/fm/emf_down";

    public static final String MQ_TOPIC_NAME_NORTH_UP = "topic://zenap/fm/north_up";

    public static final String MQ_TOPIC_NAME_ALARM_RULE = "topic://zenap/fm/alarm_rule";

    public static final String INTERNAL_MQ_QUEUE_NAME_HISTORY_ALARM_2_DB = "queue://zenap/fm/historyalarm2DB";

    public static final String MQ_SELECTOR_KEY = "nf";

    public static final String MQ_EMF_DOWN_MSG_FILTER_KEY = "type";

    public static final String MQ_EMF_UP_MSG_FILTER_KEY = "type";

    public static final String INTERNAL_MQ_EMF_UP_ACKALARM_MSG_FILTER_VALUE = "ACK";

    public static final String MQ_EMF_DOWN_CLEARALARM_MSG_FILTER_VALUE = "CLEAR";

    public static final String MQ_EMF_UP_CLEARALARM_MSG_FILTER_VALUE = "CLEAR";

    public static final String MQ_EMF_UP_CHANGEALARM_MSG_FILTER_VALUE = "CHANGE";

    public static final String MQ_EMF_UP_RAISEALARM_MSG_FILTER_VALUE = "RAISE";

    public static final String INTERNAL_MQ_CLEARALARM_2_DB_FILTER_VALUE = "CLEAR";

    public static final String COMETD_MESSAGE_TOPIC = "cometd2Client";

    public static final String COMETD_MESSAGE_CHANEL = "/broadcast_channel/alarm_cometd_chanel";

    public static final String COMETD_NF_COUNTER_CHANEL = "/nf_counter_cometd_chanel";

    public static final String COMETD_PROMPTING_RULE_CHANEL = "/prompting_rule_cometd_chanel";

    public static final String COMETD_MASK_RULE_MESSAGE_FILTER_KEY = "mask_rule";

    public static final String COMETD_CLEAR_ALARM_MESSAGE_FILTER_KEY = "clear_alarm";

    public static final String COMETD_ACK_ALARM_MESSAGE_FILTER_KEY = "ack_alarm";

    public static final String COMETD_HISTORY_ALARM_ACK_STATE_MESSAGE_FILTER_KEY = "history_alarm_ack";

    public static final String CACHE_ACTIVE_ALARM_KEY = "TAG_ACTIVE_ALARM";

    public static final String CACHE_ALARM_CODE_KEY = "TAG_ALARM_CODE";

    public static final String CACHE_ALARM_RESTYPE_AND_RESVERSION_KEY = "TAG_ALARM_RESTYPE_AND_RESVERSION";

    public static final String CACHE_ALARM_REASON_KEY = "TAG_ALARM_REASON";

    public static final String CACHE_ALARM_MAIN_KEY = "TAG_ALARM_MAIN";

    public static final short STATUS_ENABLE = 0;

    public static final short STATUS_DISABLE = 1;

    public static final short STATUS_DELETED = 2;

    public static final short UNDEFINE_LEVEL = 0;

    public static final short CRITICA_LEVEL = 1;

    public static final short MAJOR_LEVEL = 2;

    public static final short MINOR_LEVEL = 3;

    public static final short WARNING_LEVEL = 4;

    public static final String I18N_EN = "en";

    public static final String I18N_ZH = "zh";

    public static final String ZH_CN = "zh_CN";

    public static final String EN_US = "en_US";

    public static final String EXECUTE_TIMER = "0 0 0 */1 * ? ";

    public static final String COMMON_EM_RESTYPE = "common_em";

    public static final String EM_LOCATION = "em";

    public static final String BASE_MOC = "em";

    public static final String SYSTEM_ID = "SystemId";

    public static final String ADMIN = "admin";

    public static final long UNDEFINE_ALARM_CODE = -1;

    // for rule code
    public static final long FORWARD_FAILED_ALARM_CODE = 1028L;

    public static long ACTIVE_PERSISTING_RULE_CODE = 1017L;
    public static long UNACKNOWLEDGED_PERSISTING_RULE_CODE = 1018L;
}
