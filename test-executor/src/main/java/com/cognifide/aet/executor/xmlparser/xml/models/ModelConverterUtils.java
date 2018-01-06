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

import com.cognifide.aet.executor.model.ExtendedUrl;
import com.cognifide.aet.executor.xmlparser.api.ParseException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public final class ModelConverterUtils {

  private ModelConverterUtils() {
    // empty utils constructor
  }

  static List<ExtendedUrl> extendUrlsList(List<Url> urls)
      throws ParseException, UnsupportedEncodingException {
    List<ExtendedUrl> extendedUrls = Lists.newArrayList();
    List<ExtendedUrl> duplicatedUrls = Lists.newArrayList();
    Set<String> names = Sets.newHashSet();
    for (Url url : urls) {
      String urlName;
      if (StringUtils.isBlank(url.getName())) {
        urlName = url.getHref().trim();
      } else {
        urlName = URLEncoder.encode(url.getName().trim(), StandardCharsets.UTF_8.displayName());
      }
      ExtendedUrl extendedUrl = new ExtendedUrl(url.getHref(), urlName, url.getDescription());
      if (!names.add(urlName)) {
        duplicatedUrls.add(extendedUrl);
      } else {
        extendedUrls.add(extendedUrl);
      }
    }
    if (!duplicatedUrls.isEmpty()) {
      StringBuilder builder = new StringBuilder("Duplicated urls:");
      for (ExtendedUrl url : duplicatedUrls) {
        builder.append(String.format("%n%s with name %s", url.getUrl(), url.getName()));
      }
      throw new ParseException(builder.toString());
    }
    return extendedUrls;
  }

}
