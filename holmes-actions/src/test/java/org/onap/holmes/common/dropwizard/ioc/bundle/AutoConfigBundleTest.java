/*
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
package org.onap.holmes.common.dropwizard.ioc.bundle;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class AutoConfigBundleTest {
    @Test
    public void newBuilder() throws Exception {
        assertThat(AutoConfigBundle.newBuilder(), instanceOf(AutoConfigBundleBuider.class));
    }

    @Test
    public void initialize() throws Exception {
        AutoConfigBundle.newBuilder().build().initialize(new Bootstrap<>(new IOCApplication<Configuration>() {
            @Override
            public void initialize(Bootstrap<Configuration> bootstrap) {
                super.initialize(bootstrap);
            }
        }));
    }

    @Test
    public void run() throws Exception {
        AutoConfigBundle.newBuilder().build().run(new Configuration(), new Environment(
                "Test", new ObjectMapper(), new Validator() {
            @Override
            public <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... classes) {
                return null;
            }

            @Override
            public <T> Set<ConstraintViolation<T>> validateProperty(T t, String s, Class<?>... classes) {
                return null;
            }

            @Override
            public <T> Set<ConstraintViolation<T>> validateValue(Class<T> aClass, String s, Object o, Class<?>... classes) {
                return null;
            }

            @Override
            public BeanDescriptor getConstraintsForClass(Class<?> aClass) {
                return null;
            }

            @Override
            public <T> T unwrap(Class<T> aClass) {
                return null;
            }

            @Override
            public ExecutableValidator forExecutables() {
                return null;
            }
        }, new MetricRegistry(), ClassLoader.getSystemClassLoader()
        ));
    }

}