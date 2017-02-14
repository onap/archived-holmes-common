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
package org.openo.holmes.common.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jvnet.hk2.annotations.Service;
import org.openo.holmes.common.api.entity.AlarmsCorrelation;
import org.skife.jdbi.v2.Query;

import org.openo.holmes.common.db.mapper.AlarmsCorrelationMapper;
import org.openo.holmes.common.utils.DbDaoUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlarmCorrelationQueryDao {

  @Inject
  private DbDaoUtil dbDaoUtil;

  @Inject
  private AlarmsCorrelationMapper mapper;

  private final static String SELECT_TABLE_SQL = "SELECT * FROM APLUS_CORRELATION ";

  public List<AlarmsCorrelation> queryByFilter(String where) {
    List<AlarmsCorrelation> alarmsCorrelations = new ArrayList<AlarmsCorrelation>();
    StringBuilder querySql = new StringBuilder(SELECT_TABLE_SQL).append(where);
    log.info("Query alarm correlation table! Sql:[" + querySql + "].");
    Query<Map<String, Object>> query = dbDaoUtil.getHandle().createQuery(querySql.toString());
    List<Map<String, Object>> dbDataMaps = query.list();
    for (Map<String, Object> map : dbDataMaps) {
      alarmsCorrelations.add(mapper.getAlarmCorrelationByMap(map));
    }
    log.info("Success to query alarm correlation table! total count:[" + dbDataMaps.size() + "].");
    return alarmsCorrelations;
  }
}
