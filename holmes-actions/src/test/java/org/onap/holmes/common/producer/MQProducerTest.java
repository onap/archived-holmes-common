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

package org.onap.holmes.common.producer;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import org.glassfish.hk2.api.IterableProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.onap.holmes.common.api.stat.Alarm;
import org.onap.holmes.common.api.entity.CorrelationResult;
import org.onap.holmes.common.api.stat.VesAlarm;
import org.onap.holmes.common.config.MQConfig;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

public class MQProducerTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private IterableProvider<MQConfig> mqConfigProvider;

    private ConnectionFactory connectionFactory;

    private MQProducer mqProducer;

    @Before
    public void before() throws Exception {
        mqProducer = new MQProducer();

        mqConfigProvider = PowerMock.createMock(IterableProvider.class);
        connectionFactory = PowerMock.createMock(ConnectionFactory.class);

        Whitebox.setInternalState(mqProducer, "mqConfigProvider", mqConfigProvider);
        Whitebox.setInternalState(mqProducer, "connectionFactory", connectionFactory);
        PowerMock.resetAll();
    }

    @Test
    public void init() {
        MQConfig mqConfig = new MQConfig();
        mqConfig.brokerIp = "127.0.0.1";
        mqConfig.brokerPort = 61616;
        mqConfig.brokerPassword = "admin";
        mqConfig.brokerUsername = "admin";
        expect(mqConfigProvider.get()).andReturn(mqConfig).anyTimes();

        PowerMock.replayAll();

        mqProducer.init();

        PowerMock.verifyAll();
    }

    @Test
    public void sendAlarmMQTopicMsg() throws Exception {
        VesAlarm alarm = new VesAlarm();
        Connection connection = PowerMock.createMock(Connection.class);
        Session session = PowerMock.createMock(Session.class);
        Destination destination = PowerMock.createMock(Topic.class);
        MessageProducer messageProducer = PowerMock.createMock(MessageProducer.class);
        ObjectMessage objMessage = PowerMock.createMock(ObjectMessage.class);

        expect(connectionFactory.createConnection()).andReturn(connection);
        connection.start();
        expect(connection.createSession(anyBoolean(), anyInt())).andReturn(session);
        expect(session.createTopic(anyObject(String.class))).andReturn((Topic) destination);
        expect(session.createProducer(anyObject(Destination.class))).andReturn(messageProducer);

        expect(session.createObjectMessage(anyObject(Alarm.class))).andReturn(objMessage);
        messageProducer.send(objMessage);
        session.commit();
        connection.close();

        PowerMock.replayAll();

        mqProducer.sendAlarmMQTopicMsg(alarm);

        PowerMock.verifyAll();

    }

    @Test
    public void sendAlarmMQTopicMsg_exception() throws Exception {
        thrown.expect(JMSException.class);
        VesAlarm alarm = new VesAlarm();

        expect(connectionFactory.createConnection()).andThrow(new JMSException(""));

        PowerMock.replayAll();

        mqProducer.sendAlarmMQTopicMsg(alarm);

        PowerMock.verifyAll();
    }

    @Test
    public void sendCorrelationMQTopicMsg() throws Exception {

        Connection connection = PowerMock.createMock(Connection.class);
        Session session = PowerMock.createMock(Session.class);
        Destination destination = PowerMock.createMock(Topic.class);
        MessageProducer messageProducer = PowerMock.createMock(MessageProducer.class);
        ObjectMessage objMessage = PowerMock.createMock(ObjectMessage.class);

        expect(connectionFactory.createConnection()).andReturn(connection);
        connection.start();
        expect(connection.createSession(anyBoolean(), anyInt())).andReturn(session);
        expect(session.createTopic(anyObject(String.class))).andReturn((Topic) destination);
        expect(session.createProducer(anyObject(Destination.class))).andReturn(messageProducer);

        expect(session.createObjectMessage(anyObject(CorrelationResult.class)))
                .andReturn(objMessage);
        messageProducer.send(objMessage);
        session.commit();
        connection.close();

        PowerMock.replayAll();

        Alarm parentAlarm = new Alarm();
        Alarm childAlarm = new Alarm();
        mqProducer.sendCorrelationMQTopicMsg("ruleId", 123L, parentAlarm, childAlarm);

        PowerMock.verifyAll();

    }

    @Test
    public void sendCorrelationMQTopicMsg_exception() throws Exception {
        thrown.expect(JMSException.class);

        expect(connectionFactory.createConnection()).andThrow(new JMSException(""));

        PowerMock.replayAll();

        Alarm parentAlarm = new Alarm();
        Alarm childAlarm = new Alarm();
        mqProducer.sendCorrelationMQTopicMsg("ruleId", 123L, parentAlarm, childAlarm);

        PowerMock.verifyAll();

    }
} 
