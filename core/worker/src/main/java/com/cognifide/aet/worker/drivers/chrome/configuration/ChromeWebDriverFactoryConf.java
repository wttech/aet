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
package com.cognifide.aet.worker.drivers.chrome.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.cognifide.aet.worker.drivers.WebDriverHelper.DEFAULT_SELENIUM_GRID_URL;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.NAME_LABEL;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.NAME_DESC;
import static com.cognifide.aet.worker.drivers.WebDriverHelper.SELENIUM_GRID_URL_LABEL;

@ObjectClassDefinition(name = "AET Chrome WebDriver Factory", description = "AET Chrome WebDriver Factory")
public @interface ChromeWebDriverFactoryConf {

  String DEFAULT_BROWSER_NAME = "chrome";

  @AttributeDefinition(
      name = NAME_LABEL,
      description = NAME_DESC)
  String name() default DEFAULT_BROWSER_NAME;

  @AttributeDefinition(
      name = SELENIUM_GRID_URL_LABEL,
      description = SELENIUM_GRID_URL_LABEL)
  String seleniumGridUrl() default DEFAULT_SELENIUM_GRID_URL;

  @AttributeDefinition(name = "Chrome options")
  String[] chromeOptions() default
      { "--disable-plugins", "--headless", "--hide-scrollbars", "--disable-gpu" } ;
}
