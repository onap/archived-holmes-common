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
package org.onap.holmes.common.dropwizard.ioc.bundle;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import org.eclipse.jetty.util.component.LifeCycle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletProperties;
import org.jvnet.hk2.annotations.Service;
import org.onap.holmes.common.dropwizard.ioc.annotation.BaseService;
import org.onap.holmes.common.dropwizard.ioc.annotation.Lazy;
import org.onap.holmes.common.dropwizard.ioc.annotation.PostBaseService;
import org.onap.holmes.common.dropwizard.ioc.annotation.PreBaseService;
import org.onap.holmes.common.dropwizard.ioc.annotation.PreLoad;
import org.onap.holmes.common.dropwizard.ioc.annotation.PreServiceLoad;
import org.onap.holmes.common.dropwizard.ioc.utils.ServiceBinder;
import org.onap.holmes.common.dropwizard.ioc.utils.ServiceLocatorHolder;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * complete the integration of hK2 container and dropwizard
 *
 * @author hu.rui
 *
 */

public class AutoConfigBundle<T extends Configuration> implements ConfiguredBundle<T> {

  private static final Logger LOG = LoggerFactory.getLogger(AutoConfigBundle.class);

  private ServiceLocator locator;
  private Reflections reflections;
  private Set<Class<?>> services;

  private Bootstrap<?> bootstrap;


  AutoConfigBundle(final String packageName) {
    this(Lists.newArrayList(packageName));
  }

  AutoConfigBundle(List<String> packageNames) {
    FilterBuilder filterBuilder = new FilterBuilder();

    packageNames.stream().forEach(packageName -> {
      filterBuilder.include(FilterBuilder.prefix(packageName));
    });
    ConfigurationBuilder reflectionCfg = new ConfigurationBuilder();

    packageNames.stream().forEach(packageName -> {
      reflectionCfg.addUrls(ClasspathHelper.forPackage(packageName));
    });

    reflectionCfg.filterInputsBy(filterBuilder).setScanners(new SubTypesScanner(),
        new TypeAnnotationsScanner());
    reflections = new Reflections(reflectionCfg);

    locator = ServiceLocatorFactory.getInstance().create("dw-hk2");

    ServiceLocatorHolder.setLocator(locator);

  }

  public static <T extends Configuration> AutoConfigBundleBuider<T> newBuilder() {
    return new AutoConfigBundleBuider<T>();
  }

  @Override
  public void initialize(final Bootstrap<?> bootstrap) {

    this.bootstrap = bootstrap;
    registerPreLoadService();

    LOG.debug("Intialzing auto config bundle.");
  }

  private void registerPreLoadService() {

    registerService(PreLoad.class);

  }


  @Override
  public void run(final T configuration, final Environment environment) throws Exception {

    registerConfigurationProvider(configuration, environment);


    registerEnvironment(environment);
    registerObjectMapper(environment);

    environment.getApplicationContext().getServletContext()
        .setAttribute(ServletProperties.SERVICE_LOCATOR, locator);

    registerService(PreBaseService.class);
    registerService(BaseService.class);
    registerService(PostBaseService.class);
    this.registerService(PreServiceLoad.class);


    registerServices();

    // registerManaged(environment);
    registerLifecycle(environment);
    registerServerLifecycleListeners(environment);
    registerJettyLifeCycleListener(environment);
    registerTasks(environment);
    registerHealthChecks(environment);
    registerProviders(environment);
    registerResources(environment);

    environment.lifecycle().manage(new ServiceLocatorManaged(locator));

  }



  private void registerProviders(Environment environment) {
    reflections.getSubTypesOf(Provider.class).stream().filter(services::contains)
        .forEach(providerKlass -> {
          try {
            environment.jersey().register(locator.getService(providerKlass));
          } catch (Exception e) {
            LOG.warn("", e);
          }

          LOG.info("Registering Dropwizard Provider, class name : {}", providerKlass.getName());

        });

  }

  private void registerTasks(Environment environment) {
    reflections.getSubTypesOf(Task.class).stream().filter(services::contains).forEach(taskKlass -> {
      try {
        environment.admin().addTask(locator.getService(taskKlass));
      } catch (Exception e) {
        LOG.warn("", e);
      }
      LOG.info("Registering Dropwizard Task, class name : {}", taskKlass.getName());
    });

  }

  private void registerJettyLifeCycleListener(Environment environment) {
    reflections.getSubTypesOf(LifeCycle.Listener.class).stream().filter(services::contains)
        .forEach(lifecycleListenerKlass -> {

          try {
            environment.lifecycle()
                .addLifeCycleListener(locator.getService(lifecycleListenerKlass));
          } catch (Exception e) {
            LOG.warn("", e);
          }
          LOG.info("Registering Dropwizard lifecycleListener, class name : {}",
              lifecycleListenerKlass.getName());
        });

  }

  private void registerServerLifecycleListeners(Environment environment) {

    reflections.getSubTypesOf(ServerLifecycleListener.class).stream().filter(services::contains)
        .forEach(serverLifecycleListenerKlass -> {
          try {
            environment.lifecycle()
                .addServerLifecycleListener(locator.getService(serverLifecycleListenerKlass));
          } catch (Exception e) {
            LOG.warn("", e);
          }
          LOG.info("Registering Dropwizard serverLifecycleListener, class name : {}",
              serverLifecycleListenerKlass.getName());
        });

  }

  private void registerLifecycle(Environment environment) {
    reflections.getSubTypesOf(LifeCycle.class).stream().filter(services::contains)
        .forEach(lifeCycleKlass -> {
          try {
            environment.lifecycle().manage(locator.getService(lifeCycleKlass));
          } catch (Exception e) {
            LOG.warn("", e);
          }
          LOG.info("Registering Dropwizard LifeCycle, class name : {}", lifeCycleKlass.getName());
        });
  }

  /*
   * private void registerManaged(Environment environment) {
   * 
   * reflections.getSubTypesOf(Managed.class).stream().filter(services::contains)
   * .forEach(managedKlass -> { try {
   * environment.lifecycle().manage(locator.getService(managedKlass)); } catch (Exception e) {
   * LOG.warn("", e); } LOG.info("Registering Dropwizard managed, class name : {}",
   * managedKlass.getName()); });
   * 
   * }
   */

  private void registerObjectMapper(Environment environment) {

    final ObjectMapper objectMapper = environment.getObjectMapper();

    ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
      @Override
      protected void configure() {
        bind(objectMapper).to(ObjectMapper.class);

        LOG.info("Registering Dropwizard objectMapper, class name : {}",
            objectMapper.getClass().getName());
      }
    });

  }

  private void registerEnvironment(final Environment environment) {

    ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
      @Override
      protected void configure() {
        bind(environment).to(Environment.class);

        LOG.info("Registering Dropwizard environment, class name : {}",
            environment.getClass().getName());
      }
    });

  }

  private void registerConfigurationProvider(final T configuration, final Environment environment) {

    ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
      @Override
      protected void configure() {
        bind(configuration);
        LOG.info("Registering Dropwizard Configuration class name:{}",
            configuration.getClass().getName());
          bind((Configuration) configuration).to(Configuration.class);
          LOG.info("Registering Dropwizard Configuration class name:{}",
              Configuration.class.getName());
      }
    });

    registerSubConfigure(configuration, environment);

  }

  private void registerSubConfigure(final T configuration, final Environment environment) {
    final List<Field> subDeclaredFields =
        Arrays.asList(configuration.getClass().getDeclaredFields());
    List<Field> parentDeclaredFields = Arrays.asList(Configuration.class.getDeclaredFields());

    List<Field> filtersubDeclaredFields = subDeclaredFields.stream()
        .filter(subDeclaredField -> !subDeclaredField.getType().isPrimitive())
        .filter(subDeclaredField -> !subDeclaredField.getType().equals(String.class))
        .filter(subDeclaredField -> !parentDeclaredFields.contains(subDeclaredField))
        .collect(Collectors.toList());

    ServiceLocatorUtilities.bind(locator, new AbstractBinder() {
      @Override
      protected void configure() {
        filtersubDeclaredFields.forEach(subField -> {
          subField.setAccessible(true);
          try {
            Object subConfig = subField.get(configuration);
            if (subConfig != null) {
              bind(subConfig);
              LOG.info("Registering Dropwizard Sub Configuration class name {}",
                  subConfig.getClass().getName());
            }

          } catch (Exception e) {
            LOG.error("bind sub config:{} fail", subField);
          }
        });
      }
    });

  }

  private void registerServices() {
    services = this.reflections.getTypesAnnotatedWith(Service.class, true);
    if (!services.isEmpty()) {
      ServiceLocatorUtilities.bind(locator, new ServiceBinder(services));

      services.forEach(s -> {
        LOG.info("Registering Dropwizard service, class name : {}", s.getName());
      });

      services.stream().filter(serviceClazz -> (serviceClazz.getAnnotation(Lazy.class) == null))
          .peek(serviceClazz -> LOG.info("active service, class name : {}", serviceClazz.getName()))
          .forEach(serviceClazz -> {
            try {
              long startTime = System.currentTimeMillis();
              locator.getService(serviceClazz);
              LOG.info("active service, class name : {},cost time:{}", serviceClazz.getName(),
                  (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
              LOG.warn("", e);
            }

          });

    } else {
      LOG.warn("Registering Dropwizard service is empty");

    }

  }

  private void registerResources(final Environment environment) {
    reflections.getTypesAnnotatedWith(Path.class).stream().forEach(resourceClass -> {

      LOG.info("begin Registering Dropwizard resource, class name : {}", resourceClass.getName());
      try {
        Object resourceObject = locator.getService(resourceClass);
        if (resourceObject != null) {
          environment.jersey().register(resourceObject);
          LOG.info("Registering Dropwizard resource, class name : {}", resourceClass.getName());
        } else {
          LOG.warn(resourceClass.getName() + " not use Service annotation");
        }
      } catch (Exception e) {
        LOG.error("", e);
      }


    });
  }

  private void registerHealthChecks(final Environment env) {

    reflections.getSubTypesOf(HealthCheck.class).stream().filter(services::contains)
        .forEach(healthCheckKlass -> {
          try {
            env.healthChecks().register(healthCheckKlass.getName(),
                locator.getService(healthCheckKlass));
          } catch (Exception e) {
            LOG.warn("", e);
          }
          LOG.info("Registering Dropwizard healthCheck, class name : {}",
              healthCheckKlass.getName());
        });

  }



  private void registerService(Class<? extends Annotation> annotationClazz) {

    Set<Class<?>> services = this.reflections.getTypesAnnotatedWith(annotationClazz, true);
    if (!services.isEmpty()) {
      ServiceLocatorUtilities.bind(locator, new ServiceBinder(services));

      services.forEach(s -> {
        LOG.info("{} Registering  service, class name : {}", annotationClazz.getName(),
            s.getName());
      });

      services.stream().filter(serviceClazz -> (serviceClazz.getAnnotation(Lazy.class) == null))
          .peek(serviceClazz -> LOG.info("active service, class name : {}", serviceClazz.getName()))
          .forEach(serviceClazz -> {
            try {
              long startTime = System.currentTimeMillis();
              locator.getService(serviceClazz);
              LOG.info("active service, class name : {},cost time:{}", serviceClazz.getName(),
                  (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
              LOG.warn("", e);
            }

          });

    } else {
      LOG.warn("Registering {} service is empty", annotationClazz.getName());

    }

  }
}
