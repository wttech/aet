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
package com.cognifide.aet.job.common.datafilters.removelines;

import com.cognifide.aet.job.api.datafilter.DataFilterJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveLinesDataModifier implements DataFilterJob<String> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveLinesDataModifier.class);

  private static final Pattern PARAM_REGEX = Pattern.compile("([0-9]+,[0-9]+;)*([0-9]+,[0-9]+)");

  private static final String NEWLINE = "\n";

  public static final String NAME = "remove-lines";

  private static final String DATA_RANGES = "dataRanges";

  private static final String PATTERN_RANGES = "patternRanges";

  private Set<Integer> dataIndexesToRemove;

  private Set<Integer> patternIndexesToRemove;

  private String dataRanges;

  private String patternRanges;

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    dataRanges = params.get(DATA_RANGES);
    patternRanges = params.get(PATTERN_RANGES);
    validateParameters(dataRanges, patternRanges);
    dataIndexesToRemove = extractIndexes(dataRanges);
    patternIndexesToRemove = extractIndexes(patternRanges);
  }

  private Set<Integer> extractIndexes(String dataRanges) throws ParametersException {
    Set<Integer> indexesToRemove = new HashSet<Integer>();
    if (dataRanges != null) {
      for (String range : dataRanges.split(";")) {
        try {
          String[] split = range.split(",");
          indexesToRemove.addAll(ContiguousSet.create(
              Range.closed(Integer.valueOf(split[0]), Integer.valueOf(split[1])),
              DiscreteDomain.integers()));
        } catch (IllegalArgumentException e) {
          throw new ParametersException("Bad range: " + range, e);
        }
      }
    }
    return indexesToRemove;
  }

  @Override
  public String modifyData(String data) throws ProcessingException {
    return modify(data, dataIndexesToRemove);
  }

  @Override
  public String modifyPattern(String data) throws ProcessingException {
    return modify(data, patternIndexesToRemove);
  }

  @Override
  public String getInfo() {
    return NAME + " DataModifier with parameters: " + DATA_RANGES + ": " + dataRanges + " "
        + PATTERN_RANGES + ": " + patternRanges;
  }

  private String modify(String data, Set<Integer> indexesToRemove) {
    List<String> lines = Arrays.asList(StringUtils.split(data, NEWLINE));
    Set<Integer> dataIndexes = ContiguousSet.create(Range.closed(1, lines.size()),
        DiscreteDomain.integers());
    if (!dataIndexes.containsAll(indexesToRemove)) {
      LOGGER.warn("Some of defined ranges exceed source lenght. Source length is: " + lines.size());
    }
    Set<Integer> filtereedIndexesToRemove = Sets.intersection(dataIndexes, indexesToRemove);
    List<String> modifiedLines = new ArrayList<String>(
        lines.size() - filtereedIndexesToRemove.size());
    for (int i = 0; i < lines.size(); i++) {
      if (!filtereedIndexesToRemove.contains(i + 1)) {
        modifiedLines.add(lines.get(i));
      }
    }
    return StringUtils.join(modifiedLines, NEWLINE);
  }

  public void validateParameters(String dataRanges, String patternRanges)
      throws ParametersException {
    if (StringUtils.isBlank(dataRanges) && StringUtils.isBlank(patternRanges)) {
      throw new ParametersException("Either dataRanges or patternRanges should not be empty!");
    }
    for (String ranges : new String[]{dataRanges, patternRanges}) {
      if (StringUtils.isNotBlank(ranges) && !PARAM_REGEX.matcher(ranges).matches()) {
        throw new ParametersException("Bad format : " + ranges);
      }
    }

  }
}
