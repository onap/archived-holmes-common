/**
 * Copyright 2020 ZTE Corporation.
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

package org.onap.holmes.common.engine.service.impl;

import org.onap.holmes.common.database.DbDaoUtil;
import org.onap.holmes.common.engine.dao.EngineEntityDao;
import org.onap.holmes.common.engine.entity.EngineEntity;
import org.onap.holmes.common.engine.service.EngineEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EngineEntityServiceImpl implements EngineEntityService {

    private EngineEntityDao engineEntityDao;

    @Autowired
    public EngineEntityServiceImpl(DbDaoUtil dbDaoUtil){
        engineEntityDao = dbDaoUtil.getJdbiDaoByOnDemand(EngineEntityDao.class);
    }

    @Override
    public EngineEntity getEntity(String id) {
        return engineEntityDao.getEntity(id);
    }

    @Override
    public List<EngineEntity> getAllEntities() {
        return engineEntityDao.getAllEntities();
    }

    @Override
    public List<String> getLegacyEngines() {
        return engineEntityDao.getLegacyEngines();
    }

    @Override
    public void updateEntity(EngineEntity entity) {
        engineEntityDao.updateEntity(entity);
    }

    @Override
    public void insertEntity(EngineEntity entity) {
        engineEntityDao.insertEntity(entity);
    }

    @Override
    public void deleteEntity(String id) {
        engineEntityDao.deleteEntity(id);
    }
}
