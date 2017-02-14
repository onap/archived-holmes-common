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
package org.openo.holmes.common.api;

import org.jvnet.hk2.annotations.Contract;
import org.openo.holmes.common.api.stat.Alarm;

@Contract
public interface AlarmsCorrelationMqService {

    public static final String MQ_TOPIC_NAME_ALARMS_CORRELATION =
        "topic://voss/fm/alarms_correlation";

    public static final String MQ_QUEUE_NAME_ALARMS_CORRELATION =
        "queue://voss/fm/alarms_correlation";

    public static final String MQ_TOPIC_NAME_ALARM = "topic://voss/fm/alarm";

    public boolean sendMQTopicMsg(String ruleId, long createTimeL, Alarm parentAlarm,
        Alarm childAlarm);

    public boolean sendMQTopicMsg(Alarm alarm);

    public boolean sendMQQueueMsg(String ruleId, long createTimeL, Alarm parentAlarm,
        Alarm childAlarm);
}
