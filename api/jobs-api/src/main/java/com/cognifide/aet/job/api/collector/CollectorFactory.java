/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.job.api.collector;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import java.util.Map;

/**
 * This factory is used to instantiate Collector instance with given name.
 *
 * Implementation of this interface should be a OSGI service so each implementation requires to have
 * {@literal @}Service and {@literal @}Component annotation to work and register properly.
 */
public interface CollectorFactory {

  /**
   * @return name, which the collector factory will be registered on. It has to be unique for all
   * modules in collect phase. It is also name of tag definition for collector used in suite.
   */
  String getName();

  /**
   * Creates collector job. Each call should return new instance of a collector object unless it is
   * not stateful - it has no parameters and variable fields.
   *
   * @param properties - properties with url to perform operation on.
   * @param parameters -  a map that contains all parameters defined in test suite xml file for
   * collector.
   * @param webCommunicationWrapper - object that provides tested page data.
   * @return collector job instance.
   */
  CollectorJob createInstance(CollectorProperties properties, Map<String, String> parameters,
      WebCommunicationWrapper webCommunicationWrapper) throws ParametersException;
}
