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
package org.openo.holmes.common.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import org.openo.holmes.common.api.stat.Alarm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class CorrelationResult implements Serializable{

  @JsonProperty
  private String ruleId;

  @JsonProperty
  private long createTimeL;

  @JsonProperty
  private byte resultType;

  @JsonProperty
  private Alarm[] affectedAlarms;
}
