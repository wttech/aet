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
package com.cognifide.aet.job.common.comparators.source;

import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.source.diff.DiffParser;
import com.cognifide.aet.job.common.comparators.source.diff.ResultDelta;
import com.cognifide.aet.job.common.comparators.source.visitors.ContentVisitor;
import com.cognifide.aet.job.common.comparators.source.visitors.MarkupVisitor;
import com.cognifide.aet.job.common.comparators.source.visitors.NodeTraversor;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceComparator implements ComparatorJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(SourceComparator.class);

  public static final String COMPARATOR_TYPE = "source";

  public static final String COMPARATOR_NAME = "source";

  private static final String SOURCE_COMPARE_TYPE = "compareType";

  private final ComparatorProperties properties;

  private final DiffParser diffParser;

  private final ArtifactsDAO artifactsDAO;

  private SourceCompareType sourceCompareType = SourceCompareType.ALL;

  private final List<DataFilterJob> dataFilterJobs;

  public SourceComparator(ArtifactsDAO artifactsDAO, ComparatorProperties properties,
      DiffParser diffParser,
      List<DataFilterJob> dataFilterJobs) {
    this.artifactsDAO = artifactsDAO;
    this.properties = properties;
    this.diffParser = diffParser;
    this.dataFilterJobs = dataFilterJobs;

  }

  @Override
  @SuppressWarnings("unchecked")
  public final ComparatorStepResult compare() throws ProcessingException {
    final ComparatorStepResult result;
    try {
      String patternSource = formatCode(
          artifactsDAO.getArtifactAsString(properties, properties.getPatternId()));
      String dataSource = formatCode(
          artifactsDAO.getArtifactAsString(properties, properties.getCollectedId()));

      for (DataFilterJob<String> dataFilterJob : dataFilterJobs) {
        LOGGER.info("Starting {}. Company: {} Project: {}",
            dataFilterJob.getInfo(), properties.getCompany(), properties.getProject());

        dataSource = dataFilterJob.modifyData(dataSource);

        LOGGER.info("Successfully ended data modifications using  {}. Company: {} Project: {}",
            dataFilterJob.getInfo(), properties.getCompany(), properties.getProject());

        patternSource = dataFilterJob.modifyPattern(patternSource);

        LOGGER.info("Successfully ended pattern modifications using {}. Company: {} Project: {}",
            dataFilterJob.getInfo(), properties.getCompany(), properties.getProject());
      }

      if (StringUtils.isNotBlank(patternSource)) {
        boolean compareTrimmedLines = shouldCompareTrimmedLines(sourceCompareType);
        final List<ResultDelta> deltas = diffParser
            .generateDiffs(patternSource, dataSource, compareTrimmedLines);
        if (deltas.isEmpty()) {
          result = new ComparatorStepResult(null, ComparatorStepResult.Status.PASSED, false);
        } else {
          result = new ComparatorStepResult(artifactsDAO.saveArtifactInJsonFormat(properties,
              Collections.singletonMap("differences", deltas)),
              ComparatorStepResult.Status.FAILED, true);
          result.addData("formattedPattern", artifactsDAO.saveArtifact(properties, patternSource));
          result.addData("formattedSource", artifactsDAO.saveArtifact(properties, dataSource));
          result.addData("sourceCompareType", sourceCompareType.name());
        }
      } else {
        result = new ComparatorStepResult(null, ComparatorStepResult.Status.PASSED);
      }
    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    }

    return result;
  }


  private boolean shouldCompareTrimmedLines(SourceCompareType sourceCompareType) {
    return SourceCompareType.ALLFORMATTED.equals(sourceCompareType)
        || SourceCompareType.MARKUP.equals(sourceCompareType);
  }

  @Override
  public void setParameters(final Map<String, String> params) throws ParametersException {
    if (params.containsKey(SOURCE_COMPARE_TYPE)) {
      this.sourceCompareType = SourceCompareType
          .valueOf(params.get(SOURCE_COMPARE_TYPE).toUpperCase());
    }
  }

  private String formatCode(String code) {
    String result;
    switch (sourceCompareType) {
      case MARKUP:
        result = formatCodeMarkup(code);
        break;
      case CONTENT:
        result = formatCodeContent(code);
        break;
      case ALLFORMATTED:
        result = formatCodeAllFormatted(code);
        break;
      default:
        result = code;
        break;
    }
    return result;
  }

  private String formatCodeAllFormatted(String code) {
    Document doc = Jsoup.parse(code);
    return removeEmptyLines(doc.outerHtml());
  }

  // package scoped for unit test
  String removeEmptyLines(String source) {
    String result = source;
    if (StringUtils.isNotBlank(source)) {
      result = result.replaceAll("(?m)^[ \t]*[\r\n]+", "");
    }
    return result;
  }

  private String formatCodeContent(String code) {
    Document doc = Jsoup.parse(code);
    ContentVisitor visitor = new ContentVisitor();
    NodeTraversor traversor = new NodeTraversor(visitor);
    traversor.traverse(doc);
    return visitor.getFormattedText();
  }

  private String formatCodeMarkup(String code) {
    Document doc = Jsoup.parse(code);
    NodeTraversor traversor = new NodeTraversor(new MarkupVisitor());
    traversor.traverse(doc);
    return doc.html();
  }
}
