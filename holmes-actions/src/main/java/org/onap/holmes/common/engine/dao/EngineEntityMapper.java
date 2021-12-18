/**
 * Copyright 2020-2021 ZTE Corporation.
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

package org.onap.holmes.common.engine.dao;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.onap.holmes.common.engine.entity.EngineEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EngineEntityMapper implements RowMapper<EngineEntity> {
    @Override
    public EngineEntity map(ResultSet rs, StatementContext ctx) throws SQLException {
        EngineEntity entity = new EngineEntity();
        entity.setIp(rs.getString("ip"));
        entity.setPort(rs.getInt("port"));
        entity.setLastModified(rs.getLong("lastmodified"));
        return entity;
    }
}