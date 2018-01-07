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
package com.cognifide.aet.job.common.comparators.permormance.clientside.parser;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.permormance.clientside.report.ClientSidePerformanceReport;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import org.apache.commons.lang3.StringUtils;

public class ClientSidePerformanceParser {

  public ClientSidePerformanceComparatorResultData parse(String json) throws ProcessingException {

    Type type = new TypeToken<ClientSidePerformanceReport>() {
    }.getType();
    try {
      ClientSidePerformanceReport report = new GsonBuilder().setLenient().create()
          .fromJson(json, type);
      ComparatorStepResult.Status result = getResultStatus(report.getPrettyOverallScore());

      return new ClientSidePerformanceComparatorResultData(result, report);
    } catch (JsonSyntaxException e) {
      throw new ProcessingException(e.getMessage(), e);
    }
  }

  private ComparatorStepResult.Status getResultStatus(String overallScore) {
    switch (StringUtils.defaultString(overallScore)) {
      case "A":
        return ComparatorStepResult.Status.PASSED;
      case "F":
        return ComparatorStepResult.Status.FAILED;
      default:
        return ComparatorStepResult.Status.WARNING;
    }
  }
}
