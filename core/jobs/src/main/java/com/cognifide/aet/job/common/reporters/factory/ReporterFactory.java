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
package com.cognifide.aet.job.common.reporters.factory;

import com.cognifide.aet.job.common.reporters.accessibility.AccessibilityReporter;
import java.util.HashMap;
import java.util.Map;
import org.osgi.service.component.annotations.Component;

@Component(service = ReporterFactory.class, immediate = true)
public class ReporterFactory {

  private static Map<ReportType, Reporter> reporters = new HashMap<>();

  static {
    reporters.put(ReportType.ACCESSIBILITY, new AccessibilityReporter());
  }

  public Reporter get(ReportType type) {
    Reporter reporter = reporters.get(type);

    if (reporter == null) {
      throw new IllegalStateException("Unable to find reporter of type " + type);
    }

    return reporter;
  }
}
