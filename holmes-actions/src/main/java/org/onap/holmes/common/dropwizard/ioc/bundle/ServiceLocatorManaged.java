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

import org.glassfish.hk2.api.ServiceLocator;

import io.dropwizard.lifecycle.Managed;

/**
 * Life cycle management for IOC containers
 * @author hu.rui
 *
 */
public class ServiceLocatorManaged implements Managed{
	
	
	
	private ServiceLocator locator;
	
	

	public ServiceLocatorManaged(ServiceLocator locator) {
		super();
		this.locator = locator;
	}

	@Override
	public void start() throws Exception {
		
		
	}

	@Override
	public void stop() throws Exception {
		locator.shutdown();
	}

}
