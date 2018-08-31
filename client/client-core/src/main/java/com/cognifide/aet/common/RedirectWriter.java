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
package com.cognifide.aet.common;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;

@Deprecated
class RedirectWriter {

  private static final String REPORT_URL_PLACEHOLDER = "\\$\\{fullReportUrl}";

  private static final String TEMPLATE_RESOURCE_PATH = "/templates/redirect.html";

  private static final String FILE_NAME = "redirect.html";

  private String buildDirectory;

  public RedirectWriter(String buildDirectory) {
    this.buildDirectory = buildDirectory;
  }

  private String getContent(String reportUrl) throws IOException {
    String result;
    String template = Resources
        .toString(Resources.getResource(getClass(), TEMPLATE_RESOURCE_PATH), Charsets.UTF_8);
    result = template.replaceAll(REPORT_URL_PLACEHOLDER, reportUrl);
    return result;
  }

  public void write(String reportUrl) throws AETException {
    boolean directoryExists = tryCreateDirectory();
    if (directoryExists) {
      File file = new File(getTargetPath());
      try {
        Files.write(getContent(reportUrl), file, Charsets.UTF_8);
      } catch (IOException e) {
        throw new AETException("Failed to create: " + FILE_NAME, e);
      }
    } else {
      throw new AETException("Failed to create directory: " + buildDirectory);
    }
  }

  private String getTargetPath() {
    return buildDirectory + File.separator + FILE_NAME;
  }

  private boolean tryCreateDirectory() {
    File directory = new File(buildDirectory);
    boolean result = directory.exists();
    if (!result) {
      result = directory.mkdirs();
    }
    return result;
  }

}
