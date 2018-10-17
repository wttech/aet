/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.cognifide.aet.runner.processing.data;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.StorageException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = SuiteDataService.class)
public class SuiteDataService {

  @Reference
  private MetadataDAO metadataDAO;

  /**
   * @param currentRun - current suite run
   * @return suite wrapper with all patterns from the last or specified (see Suite.patternCorrelationId) run, if this is the first run of the suite, patterns will be empty.
   */
  public Suite enrichWithPatterns(final Suite currentRun) throws StorageException {
    String currentChecksum = getProjectChecksum();//todo get json form bash file
    final SimpleDBKey dbKey = new SimpleDBKey(currentRun);
    Suite lastVersion = metadataDAO.getLatestRun(dbKey, currentRun.getName());
    String checkSumCurrentRunSuite = currentRun.getCheckSum();

    if (!isNullOrEmpty(checkSumCurrentRunSuite)) {
      Suite pattern;
      if (!isNullOrEmpty(currentRun.getPatternCorrelationId())) {
        pattern = metadataDAO.getSuite(dbKey, currentRun.getPatternCorrelationId());
      } else {
        pattern = lastVersion;
      }
      return SuiteMergeStrategy.merge(currentRun, lastVersion, pattern);
    } else {
      Suite pattern = metadataDAO.getSuiteByChecksum(checkSumCurrentRunSuite);
      if (pattern != null) {
        //
        return SuiteMergeStrategy.merge(currentRun, lastVersion, pattern);
      } else {
        try {
          metadataDAO.updateSuite(currentRun);
        } catch (ValidatorException e) {
          e.printStackTrace();
        }
        currentRun.setCheckSumProject(currentChecksum);//todo should by immutable?
        return updateSuit(currentRun);
      }
    }
//    final SimpleDBKey dbKey = new SimpleDBKey(currentRun);
//    Suite lastVersion = metadataDAO.getLatestRun(dbKey, currentRun.getName());
//    Suite pattern;
//    if (currentRun.getPatternCorrelationId() != null) {
//      pattern = metadataDAO.getSuite(dbKey, currentRun.getPatternCorrelationId());
//    } else {
//      pattern = lastVersion;
//    }
//    return SuiteMergeStrategy.merge(currentRun, lastVersion, pattern);
  }

  private Suite updateSuit(Suite currentRun) throws StorageException {
    try {
      return metadataDAO.updateSuite(currentRun);
    } catch (ValidatorException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String getProjectChecksum() {
    Gson gson = new Gson();
    CloseableHttpClient httpclient = HttpClients.createDefault();
    String url = "http://localhost:4502/bin/bridge/checksum";// pass by sh script
    HttpGet httpget = new HttpGet(url);
    String jsonSting = null;
    try {
      CloseableHttpResponse response = httpclient.execute(httpget);
      jsonSting = EntityUtils.toString(response.getEntity());
    } catch (IOException e) {
      e.printStackTrace();//todo
    }
    Type emChecksumsType = new TypeToken<ArrayList<AemChecksum>>() {
    }.getType();
    List<AemChecksum> aemChecksumList = gson.fromJson(jsonSting, emChecksumsType);
    List<String> checksums = aemChecksumList.stream().map(aemChecksum -> aemChecksum.getChecksum()).collect(Collectors.toList());
    String projectChecksum = DigestUtils.md5Hex(checksums.toString());
    return projectChecksum;

  }

  public Suite saveSuite(final Suite suite) throws ValidatorException, StorageException {
    return metadataDAO.saveSuite(suite);
  }
}
