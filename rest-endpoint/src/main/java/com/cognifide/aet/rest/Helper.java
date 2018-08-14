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
package com.cognifide.aet.rest;

import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.SimpleDBKey;
import javax.servlet.http.HttpServletRequest;

public final class Helper {

  static final String REST_PREFIX = "/api";
  static final String ARTIFACT_PART_PATH = "artifact";
  static final String METADATA_PART_PATH = "metadata";
  static final String HISTORY_PART_PATH = "history";
  static final String REPORT_PART_PATH = "/report";
  static final String CONFIGS_PART_PATH = "/configs";
  static final String LOCK_PART_PATH = "/lock";
  static final String XUNIT_PART_PATH = "/xunit";
  static final String PATH_SEPARATOR = "/";

  public static final String COMPANY_PARAM = "company";
  public static final String PROJECT_PARAM = "project";
  public static final String CORRELATION_ID_PARAM = "correlationId";
  public static final String SUITE_PARAM = "suite";
  public static final String VERSION_PARAM = "version";
  public static final String ID_PARAM = "id";
  public static final String REPORT_PART_PATH_DEFAULT_PAGE = "index.html";

  private Helper() {
    //private helper constructor
  }

  public static String getMetadataPath() {
    return REST_PREFIX + PATH_SEPARATOR + METADATA_PART_PATH;
  }

  public static String getHistoryPath() {
    return REST_PREFIX + PATH_SEPARATOR + HISTORY_PART_PATH;
  }

  public static String getArtifactPath() {
    return REST_PREFIX + PATH_SEPARATOR + ARTIFACT_PART_PATH;
  }

  public static String getReportPath() {
    return REPORT_PART_PATH;
  }

  public static String getConfigsPath() {
    return CONFIGS_PART_PATH;
  }

  public static String getLocksPath() {
    return LOCK_PART_PATH;
  }

  public static String getXUnitPath() {
    return XUNIT_PART_PATH;
  }

  public static String getReportPathDefaultPage() {
    return REPORT_PART_PATH + PATH_SEPARATOR + REPORT_PART_PATH_DEFAULT_PAGE;
  }

  public static DBKey getDBKeyFromRequest(HttpServletRequest req) throws ValidatorException {
    String company = req.getParameter(Helper.COMPANY_PARAM);
    String project = req.getParameter(Helper.PROJECT_PARAM);
    SimpleDBKey dbKey = new SimpleDBKey(company, project);
    dbKey.validate(null);
    return dbKey;
  }
}
