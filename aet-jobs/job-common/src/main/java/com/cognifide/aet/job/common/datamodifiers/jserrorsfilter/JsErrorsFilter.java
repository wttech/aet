/*
 * Cognifide AET :: Job Common
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.job.common.datamodifiers.jserrorsfilter;

import com.cognifide.aet.job.api.collector.JsErrorLog;
import com.cognifide.aet.job.api.datamodifier.AbstractDataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.jserrors.JsErrorsCollectorResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * @author tomasz.misiewicz
 *
 */
public class JsErrorsFilter extends AbstractDataModifierJob<JsErrorsCollectorResult> {

    public static final String NAME = "js-errors-filter";

    private static final String PARAM_ERROR = "error";

    private static final String PARAM_SOURCE = "source";

    private static final String PARAM_LINE = "line";

    private String errorMessage;

    private String sourceFile;

    private Integer line;

    @Override
    public void setParameters(Map<String, String> params) throws ParametersException {
        errorMessage = params.get(PARAM_ERROR);
        sourceFile = params.get(PARAM_SOURCE);
        if (params.containsKey(PARAM_LINE)) {
            try {
                line = Integer.parseInt(params.get(PARAM_LINE));
            } catch (NumberFormatException e) {
                throw new ParametersException(
                        "Provided line: " + params.get(PARAM_LINE) + " is not a numeric value.", e);
            }
        }
        validateParameters(errorMessage, sourceFile, line);
    }

    @Override
    public JsErrorsCollectorResult modifyData(JsErrorsCollectorResult data) throws ProcessingException {
        JsErrorsCollectorResult result = data;
        Iterator<JsErrorLog> it = result.getJsErrorLogs().iterator();
        while (it.hasNext()) {
            JsErrorLog jse = it.next();
            if (matchSourceFile(sourceFile, jse.getSourceName())
                    && matchStrings(errorMessage, jse.getErrorMessage())
                    && matchNumbers(line, jse.getLineNumber())) {
                it.remove();
            }
        }
        return result;
    }

    @Override
    public String getInfo() {
        return NAME + " DataModifier with parameters: " + PARAM_SOURCE + ": " + sourceFile + " " + PARAM_ERROR
                + ": " + errorMessage + " " + PARAM_LINE + ": " + line;
    }

    private void validateParameters(String errorMessege, String sourceFile, Integer line)
            throws ParametersException {
        if (errorMessege == null && sourceFile == null && line == null) {
            throw new ParametersException("At least one parameter must be provided");
        }
    }

    private boolean matchStrings(String paramValue, String errorValue) {
        return StringUtils.isEmpty(paramValue) || StringUtils.equalsIgnoreCase(paramValue, errorValue);
    }

    private boolean matchSourceFile(String paramValue, String errorValue) {
        return StringUtils.isEmpty(paramValue) || StringUtils.endsWith(errorValue, paramValue);
    }

    private boolean matchNumbers(Integer paramValue, int errorValue) {
        return paramValue == null || paramValue.intValue() == errorValue;
    }

}
