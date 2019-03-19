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

import static com.cognifide.aet.communication.api.metadata.Step.SCREEN;

import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.StorageException;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = SuiteDataService.class)
public class SuiteDataService {

  @Reference
  private MetadataDAO metadataDAO;

  /**
   * @param currentRun - current suite run
   * @return suite wrapper with all patterns from the last or specified (see
   * Suite.patternCorrelationId) run, if this is the first run of the suite, patterns will be
   * empty.
   */
  public Suite enrichWithPatterns(final Suite currentRun) throws StorageException {
    final SimpleDBKey dbKey = new SimpleDBKey(currentRun);
    Suite lastVersion = metadataDAO.getLatestRun(dbKey, currentRun.getName());
    Suite pattern;
    if (currentRun.getPatternCorrelationId() != null) {
      pattern = metadataDAO.getSuite(dbKey, currentRun.getPatternCorrelationId());
    } else {
      pattern = lastVersion;
    }
    return SuiteMergeStrategy.merge(currentRun, lastVersion, pattern);
  }

  public Suite saveSuite(final Suite suite) throws ValidatorException, StorageException {
    return metadataDAO.saveSuite(suite);
  }

  public Suite replaceSuite(final Suite oldSuite, final Suite newSuite) throws StorageException {
    metadataDAO.replaceSuite(oldSuite,newSuite);
    return newSuite;
  }

  public Suite getSuite(DBKey dbKey, String correlationId) throws StorageException {
    return metadataDAO.getSuite(dbKey, correlationId);
  }

  public Suite resetPattern(Suite suite)throws StorageException, ValidatorException{
  return  metadataDAO.updateSuite(suite);//TODO
  }

  public Suite resetPattern(Suite suite,String url)throws StorageException, ValidatorException{

    //code for first test
    List<Test> tests = suite.getTests();
    if ((tests !=null && !tests.isEmpty())) {
    Test test = tests.get(tests.size()-1);//last?
    Set<Url> urls = test.getUrls();
    Url selectedUrl= urls.stream().filter(u-> url.equals(u.getUrl())).findFirst().get();
    Step selectedStep = selectedUrl.getSteps().stream().filter(step -> SCREEN.equals(step.getName())).findFirst().get();
    selectedStep.replacePatternWithCurrentPattern();

    }else{
       throw new IllegalStateException("  todo");
    }

    //code for production
//    suite.getTests().stream().reduce(getLastElementFromList()).orElseThrow(()->new IllegalStateException(""))
//        .getUrl(url).orElseThrow(()->new IllegalStateException(""))
//        .getSteps().stream().filter(step -> SCREEN.equals(step.getName()))
//        .findFirst().orElseThrow(()->new IllegalStateException(""))
//        .replacePatternWithCurrentPattern();

    return metadataDAO.updateSuite(suite);
  }

  private BinaryOperator<Test> getLastElementFromList() {
    return (first, second) -> second;
  }

}
