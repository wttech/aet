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
package com.cognifide.aet.job.common.collectors.accessibility;

import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue.IssueType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class AccessibilityIssueDeserializer implements JsonDeserializer<AccessibilityIssue> {

  private static final int MAX_PRE_STRING_LENGTH = 200;

  private static String NULL_ELEMENT_STRING = "Unable to point to the element associated with this issue.";

  @Override
  public AccessibilityIssue deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext jsonDeserializationContext) {
    JsonObject jo = json.getAsJsonObject();
    String message = jo.get("message").getAsString();
    String code = jo.get("code").getAsString();
    IssueType type = IssueType.byValue(jo.get("type").getAsString());
    JsonElement jsonElement = jo.get("elementString");
    String elementString = NULL_ELEMENT_STRING;
    if (jsonElement != null) {
      elementString = jsonElement.getAsString();
    }

    String elementStringAbbrv = StringEscapeUtils.escapeHtml4(elementString);
    elementStringAbbrv = StringUtils
        .abbreviateMiddle(elementStringAbbrv, "...", MAX_PRE_STRING_LENGTH);

    return new AccessibilityIssue(type, message, code, elementString, elementStringAbbrv);
  }
}
