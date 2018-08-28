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
package com.cognifide.aet.job.common.datafilters.accessibilityfilter;

import com.cognifide.aet.job.api.datafilter.DataFilterFactory;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import java.util.List;
import java.util.Map;
import org.osgi.service.component.annotations.Component;

@Component
public class AccessibilityFilterFactory implements DataFilterFactory {

  @Override
  public String getName() {
    return AccessibilityFilter.NAME;
  }

  @Override
  public DataFilterJob<List<AccessibilityIssue>> createInstance(Map<String, String> params)
      throws ParametersException {
    AccessibilityFilter filter = new AccessibilityFilter();
    filter.setParameters(params);
    return filter;
  }
}
