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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.cognifide.aet.executor.model.ExtendedUrl;
import com.cognifide.aet.executor.xmlparser.api.ParseException;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class ModelConverterUtilsTest {

  @Test(expected = ParseException.class)
  public void extendUrlsList_whenUrlDuplicated_expectParseException() throws Exception {
    List<Url> urls = Lists.newArrayList(new Url("https://www.google.com", null, null), new Url(
        "https://www.google.com", null, null));
    ModelConverterUtils.extendUrlsList(urls);
  }

  @Test
  public void extendUrlsList_whenUrlsFromTheSameDomain_expectDifferentNamesForConvertedUrls()
      throws Exception {
    List<Url> urls = Lists
        .newArrayList(new Url("https://www.google.com?q=blah", null, null), new Url(
            "https://www.google.com", null, null));
    List<ExtendedUrl> extendedUrls = ModelConverterUtils.extendUrlsList(urls);
    assertThat(extendedUrls.size(), is(2));
    String name1 = extendedUrls.get(0).getName();
    String name2 = extendedUrls.get(1).getName();
    assertTrue(StringUtils.isNotBlank(name1));
    assertTrue(StringUtils.isNotBlank(name2));
    assertFalse(name1.equals(name2));
  }

  @Test(expected = ParseException.class)
  public void extendUrlsList_withTheSameNameGiven_expectParseException() throws Exception {
    Url url1 = new Url("https://www.google.com", "some_name", null);
    Url url2 = new Url("https://www.google.com?q=blah", "some_name", null);
    List<Url> urls = Lists.newArrayList(url1, url2);
    ModelConverterUtils.extendUrlsList(urls);
  }

  @Test
  public void extendUrlsList_expectConvertedUrls() throws Exception {
    List<Url> urls = new LinkedList<>();
    urls.add(new Url("http://some-example-url.com/A/?param=first&param=second", null, null));
    urls.add(new Url("http://some-example-url.com/ÁÉÍÓÚÝáéíóúý", null, null));
    urls.add(new Url(
        "http://some-example-url.com/a/~b`!/#c$/%D^e/&f'=*g<h,(/i)j_K/;lm:[nk{|/l}a+b@|c]/?d?e>f\".",
        null, null));
    List<ExtendedUrl> extendedUrls = ModelConverterUtils.extendUrlsList(urls);
    for (ExtendedUrl url : extendedUrls) {
      assertTrue(StringUtils.isNotBlank(url.getName()));
    }
  }

  @Test
  public void extendUrlsList_withSpace_expectNamesEncoded() throws Exception {
    Url url = new Url("https://www.google.com", "some name", null);
    final List<ExtendedUrl> extendedUrls = ModelConverterUtils
        .extendUrlsList(Collections.singletonList(url));
    String name1 = extendedUrls.get(0).getName();
    assertThat(name1, is("some+name"));
  }
}
