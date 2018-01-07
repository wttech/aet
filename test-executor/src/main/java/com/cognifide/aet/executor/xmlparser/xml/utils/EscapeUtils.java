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
package com.cognifide.aet.executor.xmlparser.xml.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public final class EscapeUtils {

  private static final Pattern URL_TAG_PATTERN = Pattern.compile("(<url)(\\s+)(.*)(\\s*)(/>)");

  private static final Pattern ATTRIBUTES_PATTERN = Pattern.compile("([^=\"]*=\"[^\"]*\")(\\s*)");

  private static final String DESCRIPTION_PATTERN = "^[a-zA-Z0-9\\_\\- ]+$";

  private static final Integer DESCRIPTION_MAX_LENGTH = 40;

  private EscapeUtils() {
    // empty utils constructor
  }

  public static String escapeUrls(String xmlString) {
    Matcher matcher = URL_TAG_PATTERN.matcher(xmlString);
    StringBuffer urlTagStringBuffer = new StringBuffer();
    while (matcher.find()) {
      String attrs = matcher.group(3);
      Matcher attrMatcher = ATTRIBUTES_PATTERN.matcher(attrs);
      StringBuffer attrsStringBuffer = new StringBuffer();
      while (attrMatcher.find()) {
        String attr = attrMatcher.group(1);
        if (attr.startsWith("description=")) {
          validateAttr(attr);
        }

        if (attr.startsWith("href=")) {
          attr = String.format("href=\"%s\"",
              StringEscapeUtils.escapeXml11(attr.substring(6, attr.length() - 1)));
        }
        attrMatcher.appendReplacement(attrsStringBuffer, attr + " ");
      }
      attrMatcher.appendTail(attrsStringBuffer);
      String urlString = String.format("<url %s/>", attrsStringBuffer.toString());
      matcher.appendReplacement(urlTagStringBuffer, urlString);
    }
    matcher.appendTail(urlTagStringBuffer);
    return urlTagStringBuffer.toString();
  }

  public static void validateAttr(String attribute) {
    String description = attribute.substring(13, attribute.length() - 1);

    if (StringUtils.isNotEmpty(description) && description.length() > DESCRIPTION_MAX_LENGTH) {
      throw new IllegalArgumentException(String.format(
          "URL attribute description is longer than max %d chars: %s", DESCRIPTION_MAX_LENGTH,
          description));
    }
    if (StringUtils.isNotEmpty(description) && !description.matches(DESCRIPTION_PATTERN)) {
      throw new IllegalArgumentException(String.format("Invalid URL description provided: %s",
          description));
    }
  }

}
