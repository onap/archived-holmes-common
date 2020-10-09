/**
 * Copyright 2020 ZTE Corporation.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.holmes.common.engine.service.impl;

import com.google.common.base.CharMatcher;
import org.junit.Before;
import org.junit.Test;
import org.onap.holmes.common.engine.dao.EngineEntityDao;
import org.onap.holmes.common.engine.entity.EngineEntity;
import org.onap.holmes.common.engine.service.EngineEntityService;
import org.onap.holmes.common.utils.DbDaoUtil;

import java.util.*;

import static com.google.common.base.Predicates.notNull;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class EngineEntityServiceImplTest {
    private EngineEntityService service = new EngineEntityServiceImpl(new DbDaoUtilStub());

    @Test
    public void getLegacyEngines() {
        List<String> legacyEngines = service.getLegacyEngines();
        assertThat(legacyEngines.size(), is(2));
    }

    @Test
    public void getEntity() throws Exception {
        EngineEntity entity = service.getEntity("org.onap.holmes_9201");
        assertThat(entity, notNullValue());
    }

    @Test
    public void getAllEntities() throws Exception {
        List<EngineEntity> entities = service.getAllEntities();
        assertThat(entities.size(), is(1));
    }

    @Test
    public void updateEntity() throws Exception {
        EngineEntity entity = new EngineEntity("org.onap.holmes", 9201);
        long time = System.currentTimeMillis();
        entity.setLastModified(time);
        service.updateEntity(entity);
        assertThat(service.getEntity("org.onap.holmes_9201").getLastModified(), is(time));
    }

    @Test
    public void insertEntity() throws Exception {
        EngineEntity entity = new EngineEntity("org.onap.holmes.another", 9201);
        service.insertEntity(entity);
        assertThat(service.getAllEntities().size(), is(2));
    }

    @Test
    public void deleteEntity() throws Exception {
        service.deleteEntity("org.onap.holmes.another_9201");
        assertThat(service.getAllEntities().size(), is(1));
    }
}

class DbDaoUtilStub extends DbDaoUtil {
    private EngineEntityDao dao = new EngineEntityDaoStub();

    @Override
    public <T> T getJdbiDaoByOnDemand(Class<T> daoClazz) {

        return (T) dao;

    }
}

class EngineEntityDaoStub implements EngineEntityDao {

    private Set<EngineEntity> entitySet = new HashSet(){
        {
            add(new EngineEntity("org.onap.holmes", 9201));
        }
    };

    @Override
    public EngineEntity getEntity(String id) {
        return entitySet.stream().filter(e -> e.getId().equals(id)).findFirst().get();
    }

    @Override
    public List<EngineEntity> getAllEntities() {
        return new ArrayList<>(entitySet);
    }

    @Override
    public List<String> getLegacyEngines() {
        return Arrays.asList("org.onap.holmes", "org.onap.holmes.legacy.1");
    }

    @Override
    public void insertEntity(EngineEntity entity) {
        entitySet.add(entity);
    }

    @Override
    public void updateEntity(EngineEntity entity) {
        entitySet.add(entity);
    }

    @Override
    public void deleteEntity(String id) {
        for (EngineEntity entity : entitySet) {
            if (entity.getId().equals(id)) {
                entitySet.remove(entity);
                break;
            }
        }
    }
}