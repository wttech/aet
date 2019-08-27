/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.rest.helpers;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.nio.charset.StandardCharsets;

@Component(service = FreeMarkerConfigurationManager.class, immediate = true)
public class FreeMarkerConfigurationManager {

    private Configuration configuration;

    @Activate
    public void activate() {
        configuration = new Configuration(Configuration.VERSION_2_3_19);
        configuration.setClassForTemplateLoading(getClass(), "/templates/");
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setFallbackOnNullLoopVariable(false);
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
