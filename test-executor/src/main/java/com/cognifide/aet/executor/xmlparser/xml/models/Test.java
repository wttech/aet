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
package com.cognifide.aet.executor.xmlparser.xml.models;

import com.cognifide.aet.executor.model.TestRun;
import com.cognifide.aet.executor.xmlparser.api.ParseException;
import com.cognifide.aet.executor.xmlparser.xml.utils.ValidationUtils;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.core.Validate;

public class Test {

  private static final String ATTRIBUTE_PARENT_NAME = "test";

  @Attribute
  private String name;

  @Attribute(required = false)
  private String useProxy;

  @Attribute(required = false)
  private String browser;

  @Element
  private Collect collect;

  @Element
  private Compare compare;

  @ElementList
  private List<Url> urls;

  public TestRun adaptToTestRun() throws ParseException {
    try {
      TestRun result = new TestRun(collect.adaptToCollectorSteps(), compare.adaptToComparatorsSteps(),
          ModelConverterUtils.extendUrlsList(urls), name, useProxy, browser);
      return result;
    } catch (ParseException | UnsupportedEncodingException e) {
      throw new ParseException(
          String.format("Exception occurs during adapting %s test:%n%s", name, e.getMessage()), e);
    }
  }

  @Validate
  public void validate() throws ParseException {
    this.name = ValidationUtils
        .validateCaseInsensitiveNameAttribute(ATTRIBUTE_PARENT_NAME, "name", name);
  }

}
