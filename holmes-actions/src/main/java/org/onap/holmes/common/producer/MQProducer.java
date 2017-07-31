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
package org.openo.holmes.common.producer;

import java.io.Serializable;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.hk2.api.IterableProvider;
import org.jvnet.hk2.annotations.Service;
import org.openo.holmes.common.api.entity.CorrelationResult;
import org.openo.holmes.common.api.stat.Alarm;
import org.openo.holmes.common.api.stat.AplusResult;
import org.openo.holmes.common.config.MQConfig;
import org.openo.holmes.common.constant.AlarmConst;
import org.apache.activemq.ActiveMQConnectionFactory;

@Service
@Slf4j
@NoArgsConstructor
public class MQProducer {

    @Inject
    private IterableProvider<MQConfig> mqConfigProvider;
    private ConnectionFactory connectionFactory;

    public void init() {

        String brokerURL =
            "tcp://" + mqConfigProvider.get().brokerIp + ":" + mqConfigProvider.get().brokerPort;
        connectionFactory = new ActiveMQConnectionFactory(mqConfigProvider.get().brokerUsername,
            mqConfigProvider.get().brokerPassword, brokerURL);
    }

    public void sendAlarmMQTopicMsg(Alarm alarm) {
        sendMQTopicMsg(alarm);
    }

    public void sendCorrelationMQTopicMsg(String ruleId, long createTimeL, Alarm parentAlarm,
            Alarm childAlarm) {
        CorrelationResult correlationResult = getCorrelationResult(ruleId, createTimeL, parentAlarm, childAlarm);
        sendMQTopicMsg(correlationResult);
    }

    private <T> void sendMQTopicMsg(T t) {
        Serializable msgEntity = (Serializable) t;
        Connection connection = null;
        Session session;
        Destination destination = null;
        MessageProducer messageProducer;

        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            if (t instanceof CorrelationResult) {
                destination = session.createTopic(AlarmConst.MQ_TOPIC_NAME_ALARMS_CORRELATION);
            } else if (t instanceof Alarm) {
                destination = session.createTopic(AlarmConst.MQ_TOPIC_NAME_ALARM);
            }
            messageProducer = session.createProducer(destination);
            ObjectMessage message = session.createObjectMessage(msgEntity);
            messageProducer.send(message);
            session.commit();
        } catch (Exception e) {
            log.error("Failed send correlation." + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    log.error("Failed close connection." + e.getMessage(), e);
                }
            }
        }
    }

    private CorrelationResult getCorrelationResult(String ruleId, long createTimeL, Alarm parentAlarm,
            Alarm childAlarm) {
        CorrelationResult correlationResult = new CorrelationResult();
        correlationResult.setRuleId(ruleId);
        correlationResult.setCreateTimeL(createTimeL);
        correlationResult.setResultType(AplusResult.APLUS_CORRELATION);
        correlationResult.setAffectedAlarms(new Alarm[]{parentAlarm, childAlarm});
        return correlationResult;
    }
}
