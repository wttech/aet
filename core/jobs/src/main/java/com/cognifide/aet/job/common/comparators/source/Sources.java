/**
 * AET
 *
 * Copyright (C) 2018 Cognifide Limited
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
package com.cognifide.aet.job.common.comparators.source;


import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can generate pattern source and tested data source from artifact. It will also format
 * and filter sources.
 */
class Sources {

  private static final Logger LOGGER = LoggerFactory.getLogger(Sources.class);

  private final ComparatorProperties properties;
  private final ArtifactsDAO artifactsDAO;
  private final List<DataFilterJob> dataFilterJobs;
  private final CodeFormatter codeFormatter;

  private String patternSource;
  private String dataSource;

  Sources(ArtifactsDAO artifactsDAO, ComparatorProperties properties,
      List<DataFilterJob> dataFilterJobs, CodeFormatter codeFormatter) {
    this.properties = properties;
    this.artifactsDAO = artifactsDAO;
    this.dataFilterJobs = dataFilterJobs;
    this.codeFormatter = codeFormatter;
  }

  @SuppressWarnings("unchecked")
  void generate(SourceCompareType sourceCompareType) throws IOException, ProcessingException {
    String pattern = artifactsDAO.getArtifactAsString(properties, properties.getPatternId());
    String data = artifactsDAO.getArtifactAsString(properties, properties.getCollectedId());

    this.patternSource = codeFormatter.format(pattern, sourceCompareType);
    this.dataSource = codeFormatter.format(data, sourceCompareType);

    for (DataFilterJob<String> dataFilterJob : dataFilterJobs) {
      filter(dataFilterJob);
    }
  }

  private void filter(DataFilterJob<String> dataFilterJob) throws ProcessingException {
    String company = properties.getCompany();
    String project = properties.getProject();

    LOGGER.info("Starting {}. Company: {} Project: {}",
        dataFilterJob.getInfo(), company, project);

    dataSource = dataFilterJob.modifyData(dataSource);
    LOGGER.info("Successfully ended data modifications using  {}. Company: {} Project: {}",
        dataFilterJob.getInfo(), company, project);

    patternSource = dataFilterJob.modifyPattern(patternSource);
    LOGGER.info("Successfully ended pattern modifications using {}. Company: {} Project: {}",
        dataFilterJob.getInfo(), company, project);
  }

  String getPatternSource() {
    return patternSource;
  }

  String getDataSource() {
    return dataSource;
  }
}
