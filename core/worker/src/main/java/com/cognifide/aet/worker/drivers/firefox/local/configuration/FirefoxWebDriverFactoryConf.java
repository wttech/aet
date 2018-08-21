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
package com.cognifide.aet.worker.drivers.firefox.local.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.cognifide.aet.worker.drivers.WebDriverHelper.NAME_LABEL;

@ObjectClassDefinition(name = "AET Firefox WebDriver Factory", description = "AET Firefox WebDriver Factory")
public @interface FirefoxWebDriverFactoryConf {

  String PATH_LABEL = "Custom path to Firefox binary";

  String DEFAULT_FF_NAME = "ff";

  String DEFAULT_FIREFOX_BINARY_PATH = "/usr/bin/firefox";

  String LOG_FILE_PATH_LABEL = "Path to firefox error log";

  String DEFAULT_FIREFOX_ERROR_LOG_FILE_PATH = "/opt/aet/firefox/log/stderr.log";

  @AttributeDefinition(name = NAME_LABEL)
  String name() default DEFAULT_FF_NAME;

  @AttributeDefinition(name = PATH_LABEL)
  String path() default DEFAULT_FIREFOX_BINARY_PATH;

  @AttributeDefinition(name = LOG_FILE_PATH_LABEL)
  String logFilePath() default DEFAULT_FIREFOX_ERROR_LOG_FILE_PATH;
}
