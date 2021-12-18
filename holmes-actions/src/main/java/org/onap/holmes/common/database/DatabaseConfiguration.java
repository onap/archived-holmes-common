/**
 * Copyright 2021 ZTE Corporation.
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
package org.onap.holmes.common.database;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class DatabaseConfiguration {
//
//    @Value("${spring.datasource.url}")
//    private String url;
//
//    @Value("${spring.datasource.username}")
//    private String username;
//
//    @Value("${spring.datasource.password}")
//    private String pwd;
//
//    @Value("${spring.datasource.dirver-class-name}")
//    private String driverClass;
//
//    @Bean
//    public DataSource driverManagerDataSource() {
//        System.out.println("======================================: " + driverClass);
//        DriverManagerDataSource ds = new DriverManagerDataSource();
//        ds.setDriverClassName(driverClass);
//        ds.setUrl(url);
//        ds.setUsername(username);
//        ds.setPassword(pwd);
//        return ds;
//    }

//    @Bean
//    public DataSourceTransactionManager dataSourceTransactionManager(DataSource dataSource) {
//        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
//        dataSourceTransactionManager.setDataSource(dataSource);
//        return dataSourceTransactionManager;
//    }

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        return Jdbi.create(dataSource)
                .installPlugin(new SqlObjectPlugin())
                .installPlugin(new PostgresPlugin());
    }

//    @Bean
//    public Jdbi jdbi(DataSource ds, List<JdbiPlugin> jdbiPlugins, List<RowMapper<?>> rowMappers) {
//        TransactionAwareDataSourceProxy proxy = new TransactionAwareDataSourceProxy(ds);
//        Jdbi jdbi = Jdbi.create(proxy);
//        jdbiPlugins.forEach(plugin -> jdbi.installPlugin(plugin));
//        rowMappers.forEach(mapper -> jdbi.registerRowMapper(mapper));
//        return jdbi;
//    }
}
