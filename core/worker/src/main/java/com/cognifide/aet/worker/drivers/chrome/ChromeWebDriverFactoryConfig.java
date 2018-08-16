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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.cognifide.aet.worker.drivers.WebDriverHelper.NAME_LABEL;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.NAME_DESC;

@ObjectClassDefinition(name = "AET Chrome WebDriver Factory", description = "AET Chrome WebDriver Factory")
public @interface ChromeWebDriverFactoryConfig {

  String DEFAULT_BROWSER_NAME = "chrome";

  String SELENIUM_GRID_URL_LABEL = "Selenium grid URL";
  String SELENIUM_GRID_URL_DESC = "Url to selenium grid hub. When null local Chrome driver will be used. Local Chrome driver does not work on Linux";
  String DEFAULT_SELENIUM_GRID_URL = "http://localhost:4444/wd/hub";

  @AttributeDefinition(
      name = NAME_LABEL,
      description = NAME_DESC,
      defaultValue = DEFAULT_BROWSER_NAME)
  String name();

  @AttributeDefinition(
      name = ChromeWebDriverFactoryConfig.SELENIUM_GRID_URL_LABEL,
      description = ChromeWebDriverFactoryConfig.SELENIUM_GRID_URL_DESC,
      defaultValue = ChromeWebDriverFactoryConfig.DEFAULT_SELENIUM_GRID_URL)
  String seleniumGridUrl();
}
