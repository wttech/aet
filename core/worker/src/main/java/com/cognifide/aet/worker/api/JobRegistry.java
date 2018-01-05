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
package com.cognifide.aet.worker.api;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.datafilter.DataFilterFactory;

public interface JobRegistry {

  CollectorFactory getCollectorFactory(String name);

  ComparatorFactory getComparatorFactoryForType(String type);

  ComparatorFactory getComparatorFactory(String name);

  boolean hasJob(String name);

  DataFilterFactory getDataModifierFactory(String name);

}
