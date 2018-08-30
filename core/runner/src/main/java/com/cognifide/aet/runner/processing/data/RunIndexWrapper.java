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
package com.cognifide.aet.runner.processing.data;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.wrappers.MetadataRunDecorator;
import com.cognifide.aet.communication.api.wrappers.Run;
import com.cognifide.aet.communication.api.wrappers.UrlRunWrapper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.ArrayList;

public class RunIndexWrapper {

  //private final Map<String, Test> tests;

  private Run objectToRunWrapper = null;

  public RunIndexWrapper(Run objectToRunWrapper) {
    this.objectToRunWrapper = objectToRunWrapper;
  }

//  public Test getTest(String testName) {
//    return tests.get(testName);
//  } //update url, test name, url name, url

  public Optional<Url> getTestUrl(String testName, final String urlName) {
    Test test = objectToRunWrapper.getRealSuite().getTest(testName);
    Url url = test.getUrl(urlName);
    return Optional.of(url);
  }

  public Run get(){
    return objectToRunWrapper;
  }

  public ArrayList<MetadataRunDecorator> getUrls() {
    ArrayList<MetadataRunDecorator>urls = new ArrayList<>();
    if(objectToRunWrapper.getType()=="SUITE"){
      Suite suite = (Suite) objectToRunWrapper.getObjectToRun();
      for (Test test : suite.getTests()) {
        for(Url url : test.getUrls()){
          UrlRunWrapper urlRunWrapper = new UrlRunWrapper(url);
          urlRunWrapper.setPreferredBrowserId(test.getPreferredBrowserId());
          urlRunWrapper.setProxy(test.getProxy());
          urlRunWrapper.setTestName(test.getName());
          urls.add(new MetadataRunDecorator(urlRunWrapper, suite));
        }
      }
    }
    return urls;
  }

  public Test getTest(String testName) {
    return objectToRunWrapper.getRealSuite().getTest(testName);
  }

  public int countUrls() {
    int quantityUrls = 0;
    if(objectToRunWrapper.getType()=="SUITE"){
      Suite suite = (Suite) objectToRunWrapper.getObjectToRun();
      for (Test test : suite.getTests()) {
        for(Url url : test.getUrls()){
          quantityUrls++;
        }
      }
    }
    return quantityUrls;
  }
}
