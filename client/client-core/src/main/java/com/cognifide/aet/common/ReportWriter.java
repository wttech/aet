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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.apache.commons.io.IOUtils;

@Deprecated
class ReportWriter {

  void write(String buildDirectory, String endpointUrl, String saveAsFileName) throws IOException {
    URL url = new URL(endpointUrl);
    boolean directoryExists = tryCreateDirectory(buildDirectory);
    if (directoryExists) {
      try (InputStream inputStream = url.openStream();
          OutputStream outputStream = new FileOutputStream(
              getTargetPath(buildDirectory, saveAsFileName))) {
        IOUtils.copy(inputStream, outputStream);
      }
    } else {
      throw new IOException("Failed to create directory: " + buildDirectory);
    }
  }

  private String getTargetPath(String buildDirectory, String saveAsFileName) {
    return buildDirectory + File.separator + saveAsFileName;
  }

  private boolean tryCreateDirectory(String buildDirectory) {
    File directory = new File(buildDirectory);
    boolean result = directory.exists();
    if (!result) {
      result = directory.mkdirs();
    }
    return result;
  }

}
