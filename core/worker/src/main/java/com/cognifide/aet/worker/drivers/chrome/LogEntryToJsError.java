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
package com.cognifide.aet.worker.drivers.chrome;

import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.logging.LogEntry;

public class LogEntryToJsError implements Function<LogEntry, JsErrorLog> {

  @Override
  public JsErrorLog apply(LogEntry input) {
    JsErrorLog jsErrorLog = null;
    if (input != null) {
      final String message = input.getMessage();

      final String source = extractSource(message);
      int lineNo = extractLineNo(message);
      final String errorMessage = extractMessage(message);

      jsErrorLog = new JsErrorLog(errorMessage, source, lineNo);
    }
    return jsErrorLog;
  }

  private String extractMessage(String message) {
    String errorMessage;
    final String linesAndError = StringUtils.substringAfter(message, " ");
    errorMessage = StringUtils.substringAfter(linesAndError, " ");
    return errorMessage;
  }

  private int extractLineNo(String message) {
    final String linesAndError = StringUtils.substringAfter(message, " ");
    final String lineNumberLog = StringUtils.substringBefore(linesAndError, " ");
    final String lineNumber = StringUtils.substringBefore(lineNumberLog, ":");
    return StringUtils.isNumeric(lineNumber) ? Integer.valueOf(lineNumber) : -1;
  }

  private static String extractSource(String message) {
    String source = "";
    if (StringUtils.startsWith(message, "http")) {
      source = StringUtils.substringBefore(message, " ");
    }
    return source;
  }
}