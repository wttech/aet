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
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult.Status;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.comparator.ComparatorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.source.diff.DiffParser;
import com.cognifide.aet.job.common.comparators.source.diff.ResultDelta;
import com.cognifide.aet.job.common.comparators.source.diff.ResultDelta.TYPE;
import com.cognifide.aet.vs.ArtifactsDAO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class SourceComparator implements ComparatorJob {

  private static final String SOURCE_COMPARE_TYPE = "compareType";

  private final ComparatorProperties properties;
  private final DiffParser diffParser;
  private final ArtifactsDAO artifactsDAO;
  private final Sources sources;

  private SourceCompareType sourceCompareType;

  SourceComparator(ArtifactsDAO artifactsDAO, ComparatorProperties properties,
      DiffParser diffParser, Sources sources) {

    this.artifactsDAO = artifactsDAO;
    this.properties = properties;
    this.diffParser = diffParser;
    this.sources = sources;
  }

  @Override
  public void setParameters(final Map<String, String> params) throws ParametersException {
    this.sourceCompareType =
        Optional.ofNullable(params.get(SOURCE_COMPARE_TYPE))
            .map(String::toUpperCase)
            .map(SourceCompareType::valueOf)
            .orElse(SourceCompareType.ALL);
  }

  @Override
  public final ComparatorStepResult compare() throws ProcessingException {
    try {
      return compareSources();
    } catch (Exception ex) {
      throw new ProcessingException(ex.getMessage(), ex);
    }
  }

  @SuppressWarnings("unchecked")
  private ComparatorStepResult compareSources() throws IOException, ProcessingException {
    sources.generate(sourceCompareType);
    return getResultOfCompare();
  }

  private ComparatorStepResult getResultOfCompare() {
    List<ResultDelta> deltas = calculateDeltas();
    ComparatorStepResult.Status status = calculateStatus(deltas);
    ComparatorStepResult result = createNewStepResult(deltas, status);
    addFormattedSources(result);
    return result;
  }

  private List<ResultDelta> calculateDeltas() {
    return Optional.of(sources.getPatternSource())
        .map(this::diffSources)
        .orElse(new ArrayList<>());
  }

  private List<ResultDelta> diffSources(String patternSource) {
    boolean compareTrimmedLines = shouldCompareTrimmedLines(sourceCompareType);
    return diffParser.generateDiffs(patternSource, sources.getDataSource(), compareTrimmedLines);
  }

  private boolean shouldCompareTrimmedLines(SourceCompareType sourceCompareType) {
    return SourceCompareType.ALLFORMATTED.equals(sourceCompareType)
        || SourceCompareType.MARKUP.equals(sourceCompareType);
  }

  private Status calculateStatus(List<ResultDelta> deltas) {
    return deltas.stream().anyMatch(d -> d.getType().equals(TYPE.CHANGE)) ?
        Status.FAILED : Status.PASSED;
  }

  private ComparatorStepResult createNewStepResult(List<ResultDelta> deltas, Status status) {
    Map<String, List<ResultDelta>> differences = Collections.singletonMap("differences", deltas);
    String artifactId = artifactsDAO.saveArtifactInJsonFormat(properties, differences);
    boolean rebaseable = Status.FAILED.equals(status);
    return new ComparatorStepResult(artifactId, status, rebaseable);
  }

  private void addFormattedSources(ComparatorStepResult result) {
    String pattern = artifactsDAO.saveArtifact(properties, sources.getPatternSource());
    String data = artifactsDAO.saveArtifact(properties, sources.getDataSource());
    result.addData("formattedPattern", pattern);
    result.addData("formattedSource", data);
    result.addData("sourceCompareType", sourceCompareType.name());
  }
}
