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
package com.cognifide.aet.runner.processing.data.wrappers;

import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.wrappers.MetadataRunDecorator;
import com.cognifide.aet.communication.api.wrappers.Run;
import com.cognifide.aet.communication.api.wrappers.UrlRunWrapper;
import java.util.ArrayList;
import java.util.Optional;

public class UrlRunIndexWrapper extends RunIndexWrapper<Url> {

  public UrlRunIndexWrapper(Run<Url> objectToRunWrapper) {
    super(objectToRunWrapper);
  }

  @Override
  public ArrayList<MetadataRunDecorator<Url>> getUrls() {
    ArrayList<MetadataRunDecorator<Url>> urls = new ArrayList<>();
    Optional<Test> test = objectToRunWrapper.getRealSuite()
        .getTest(objectToRunWrapper.getTestName());
    if (test.isPresent()) {
      Url url = objectToRunWrapper.getObjectToRun();
      cleanUrlFromExecutionData(url);
      UrlRunWrapper urlRunWrapper = new UrlRunWrapper(url, test.get());
      urls.add(new MetadataRunDecorator<>(urlRunWrapper, objectToRunWrapper.getRealSuite()));
    }
    return urls;
  }

  @Override
  public int countUrls() {
    return 1;
  }
}
