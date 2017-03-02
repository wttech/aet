/**
 * Automated Exploratory Tests
 * <p>
 * Author: pnad@github
 * used code - Copyright (C) 2017 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.modifiers.js;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;

import org.apache.commons.lang3.BooleanUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class JsModifier implements CollectorJob {

    public static final String NAME = "js";

    private static final Logger LOG = LoggerFactory.getLogger(JsModifier.class);

    private static final String CMD_PARAM = "cmd";

    private final WebDriver webDriver;

    private final CollectorProperties properties;

    private String cmd;

    public JsModifier(WebDriver webDriver, CollectorProperties properties) {
        this.webDriver = webDriver;
        this.properties = properties;
    }

    @Override
    public CollectorStepResult collect() throws ProcessingException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Execute JS cmd: {} on page: {} properties url: {}",
                    cmd, webDriver.getCurrentUrl(), properties.getUrl(),
                    properties.getUrl());
        }
        return jsElement(webDriver, cmd);
    }

    @Override
    public void setParameters(Map<String, String> params) throws ParametersException {
        cmd = params.get(CMD_PARAM);
        ParametersValidator.checkNotBlank(cmd, "cmd parameter is mandatory");
    }

    public CollectorStepResult jsElement(WebDriver driver, String cmd) throws ProcessingException {
        CollectorStepResult result;

        try {
            ((JavascriptExecutor) driver).executeScript(cmd);

            result = CollectorStepResult.newModifierResult();
        } catch (Exception e) {
            throw new ProcessingException("Can't execute cmd = " + cmd, e);
        }
        return result;
    }

}
