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
package com.cognifide.aet.sanity.functional;

import com.cognifide.qa.bb.logging.ReporterModule;
import com.cognifide.qa.bb.modules.CoreModule;
import com.google.inject.AbstractModule;

public class GuiceModule extends AbstractModule {

  @Override
  protected void configure() {
    if (System.getProperty("configuration.paths") == null) {
      System.setProperty("configuration.paths", "src/main/resources/config/dev;src/main/resources/config/common");
    }
    install(new CoreModule());
    install(new ReporterModule());
  }
}
