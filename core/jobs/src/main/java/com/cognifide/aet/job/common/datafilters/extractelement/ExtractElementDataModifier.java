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
package com.cognifide.aet.job.common.datafilters.extractelement;

import com.cognifide.aet.job.api.datafilter.AbstractDataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author magdalena.biala
 */
public class ExtractElementDataModifier extends AbstractDataModifierJob<String> {

  public static final String NAME = "extract-element";

  public static final String PARAM_ELEMENT_ID = "elementId";

  public static final String PARAM_ELEMENT_CLASS = "class";

  private String elementId;

  private String elementClass;

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    elementId = params.get(PARAM_ELEMENT_ID);
    elementClass = params.get(PARAM_ELEMENT_CLASS);
    validateParameters();
  }

  private void validateParameters() throws ParametersException {
    if (!(StringUtils.isBlank(elementId) ^ StringUtils.isBlank(elementClass))) {
      throw new ParametersException("Exactly one attribute must be defined!");
    }
  }

  @Override
  public String modifyData(String data) throws ProcessingException {
    String result = StringUtils.EMPTY;
    Document parsedCode = Jsoup.parse(data);
    if (elementId != null) {
      result = modifyDataForElementParam(parsedCode);
    }
    if (elementClass != null) {
      result = modifyDataForClassParam(parsedCode);
    }
    return result;
  }

  @Override
  public String getInfo() {
    return NAME + " DataModifier with parameters: " + PARAM_ELEMENT_CLASS + ": " + elementClass
        + " " + PARAM_ELEMENT_ID + ": " + elementId;
  }

  private String modifyDataForElementParam(Document document) throws ProcessingException {
    String result;
    Element element = document.getElementById(elementId);
    if (element != null) {
      result = element.outerHtml();
    } else {
      throw new ProcessingException("No element with id=" + elementId + " found!");
    }
    return result;
  }

  private String modifyDataForClassParam(Document document) throws ProcessingException {
    String result;
    Elements elements = document.getElementsByClass(elementClass);
    if (!elements.isEmpty()) {
      result = elements.outerHtml();
    } else {
      throw new ProcessingException("No element with class=" + elementClass + " found!");
    }
    return result;
  }

  String getElementId() {
    return elementId;
  }

  String getClassAttribute() {
    return elementClass;
  }
}
