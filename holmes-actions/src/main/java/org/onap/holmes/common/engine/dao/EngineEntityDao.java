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

import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.onap.holmes.common.engine.entity.EngineEntity;

import java.util.List;

@RegisterRowMapper(EngineEntityMapper.class)
public interface EngineEntityDao {
    @SqlQuery("SELECT * FROM ENGINE_ENTITY WHERE ID = :id")
    EngineEntity getEntity(@Bind("id") String id);

    @SqlQuery("SELECT * FROM ENGINE_ENTITY")
    List<EngineEntity> getAllEntities();

    @SqlQuery("SELECT DISTINCT(ENGINEINSTANCE) FROM APLUS_RULE")
    List<String> getLegacyEngines();

    @SqlUpdate("INSERT INTO ENGINE_ENTITY VALUES (:id, :ip, :port, :lastModified)")
    void insertEntity(@BindBean EngineEntity entity);

    @SqlUpdate("UPDATE ENGINE_ENTITY SET LASTMODIFIED = :lastModified WHERE ID = :id")
    void updateEntity(@BindBean EngineEntity entity);

    @SqlUpdate("DELETE FROM ENGINE_ENTITY WHERE ID = :id")
    void deleteEntity(@Bind("id") String id);
}