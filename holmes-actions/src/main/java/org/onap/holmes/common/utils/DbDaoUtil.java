/**
 * Copyright 2017-2020 ZTE Corporation.
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
package org.onap.holmes.common.utils;

import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Service
@Slf4j
public class DbDaoUtil {

    private DBI jdbi;
    @Inject
    private Environment environmentProvider;
    @Inject
    private DataSourceFactory dataSourceFactoryProvider;

    private DBIFactory factory = new DBIFactory();

    @PostConstruct
    public synchronized void init() {
        if (jdbi == null) {
            jdbi = factory.build(environmentProvider, dataSourceFactoryProvider, "postgres");
        }
    }

    public <K> K getDao(Class<K> clazz) {
        try {
            return jdbi.open(clazz);
        } catch (Exception e) {
            log.warn("get object instance of Dao error.", e);
        }
        return null;
    }

    public Handle getHandle() {
        try {
            return jdbi.open();
        } catch (Exception e) {
            log.warn("get object instance of Dao error.", e);
        }
        return null;
    }

    public void close(Object obj) {
        if (obj != null) {
            try {
                jdbi.close(obj);
            } catch (Exception e) {
                log.warn("close jdbi connection error.", e);
            }
        }
    }

    public <T> T getJdbiDaoByOnDemand(Class<T> daoClazz) {

        return jdbi.onDemand(daoClazz);

    }

    public <T> T getJdbiDaoByOpen(Class<T> daoClazz) {

        return jdbi.open(daoClazz);

    }
}
