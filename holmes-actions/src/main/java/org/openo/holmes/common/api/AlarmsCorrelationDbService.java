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
package org.openo.holmes.common.api;

import java.util.List;
import org.jvnet.hk2.annotations.Contract;
import org.openo.holmes.common.api.entity.AlarmsCorrelation;
import org.openo.holmes.common.api.entity.AlarmsCorrelationFilter;
import org.openo.holmes.common.exception.DbException;

@Contract
public interface AlarmsCorrelationDbService {

    public void saveAlarmsCorrelation(AlarmsCorrelation alarmsCorrelation) throws DbException;

    public List<AlarmsCorrelation> queryAlarmsCorrelationByFilter(
        AlarmsCorrelationFilter alarmsCorrelationFilter) throws DbException;

    public List<AlarmsCorrelation> queryAllAlarmsCorrelation();
}
