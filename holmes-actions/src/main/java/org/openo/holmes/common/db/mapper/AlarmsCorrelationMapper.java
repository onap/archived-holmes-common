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
package org.openo.holmes.common.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.jvnet.hk2.annotations.Service;
import org.openo.holmes.common.api.entity.AlarmsCorrelation;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

@Service
public class AlarmsCorrelationMapper implements ResultSetMapper<AlarmsCorrelation> {

  private final static String RULE_ID = "ruleId";
  private final static String RULE_INFO = "ruleInfo";
  private final static String RESULT_TYPE = "resultType";
  private final static String CREATE_TIME = "createTime";
  private final static String PARENT_ALARM_ID = "parentAlarmId";
  private final static String CHILD_ALARM_ID = "childAlarmId";
  private final static String RESERVE1 = "reserve1";
  private final static String RESERVE2 = "reserve2";
  private final static String RESERVE3 = "reserve3";

  @Override
  public AlarmsCorrelation map(int i, ResultSet resultSet, StatementContext statementContext)
      throws SQLException {
    AlarmsCorrelation aplusCorrelation = new AlarmsCorrelation();
    aplusCorrelation.setRuleId(resultSet.getString(RULE_ID));
    aplusCorrelation.setRuleInfo(resultSet.getString(RULE_INFO));
    aplusCorrelation.setResultType(resultSet.getByte(RESULT_TYPE));
    aplusCorrelation.setCreateTime(resultSet.getDate(CREATE_TIME));
    aplusCorrelation.setParentAlarmId(resultSet.getLong(PARENT_ALARM_ID));
    aplusCorrelation.setChildAlarmId(resultSet.getLong(CHILD_ALARM_ID));
    aplusCorrelation.setReserve1(resultSet.getLong(RESERVE1));
    aplusCorrelation.setReserve2(resultSet.getLong(RESERVE2));
    aplusCorrelation.setReserve3(resultSet.getLong(RESERVE3));
    return aplusCorrelation;
  }

  public AlarmsCorrelation getAlarmCorrelationByMap(Map<String, Object> map) {
    AlarmsCorrelation aplusCorrelation = new AlarmsCorrelation();
    aplusCorrelation.setRuleId(getStringValue4Map(map, RULE_ID));
    aplusCorrelation.setRuleInfo(getStringValue4Map(map, RULE_INFO));
    aplusCorrelation.setResultType(getByteValue4Map(map, RESULT_TYPE, -1));
    aplusCorrelation.setCreateTime((Date) map.get(CREATE_TIME));
    aplusCorrelation.setParentAlarmId(getLongValue4Map(map, PARENT_ALARM_ID, -1));
    aplusCorrelation.setChildAlarmId(getLongValue4Map(map, CHILD_ALARM_ID, -1));
    aplusCorrelation.setReserve1(getLongValue4Map(map, RESERVE1, -1));
    aplusCorrelation.setReserve2(getLongValue4Map(map, RESERVE2, -1));
    aplusCorrelation.setReserve3(getLongValue4Map(map, RESERVE3, -1));
    return aplusCorrelation;
  }

  private String getStringValue4Map(Map<String, Object> map, String key) {
    Object value = map.get(key);
    return value == null ? "" : String.valueOf(value);
  }

  private long getLongValue4Map(Map<String, Object> map, String key, long defaultValue) {
    Object value = map.get(key);
    return value == null ? defaultValue : Long.valueOf(String.valueOf(value));
  }

  private byte getByteValue4Map(Map<String, Object> map, String key, int defaultValue) {
    Object value = map.get(key);
    return value == null ? Byte.valueOf(String.valueOf(defaultValue))
        : Byte.valueOf(String.valueOf(value));
  }
}
