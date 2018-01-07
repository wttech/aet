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
package com.cognifide.aet.job.common.comparators.source.diff;

import static com.cognifide.aet.job.common.comparators.source.diff.DiffMatchPatch.Diff;

import com.cognifide.aet.job.common.comparators.source.diff.ResultDelta.TYPE;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class DiffParser {

  private static final String NEW_LINE = "\n";

  private static final String BR_TAG = "<br>";

  private static final Function<String, String> TRIM_FUNCTION = new Function<String, String>() {
    @Override
    public String apply(String input) {
      return StringUtils.trim(input);
    }
  };

  private static DiffMatchPatch diffMatchPatch = new DiffMatchPatch();

  /**
   * Generates deltas of changes using DiffUtils library. If {trimmed} all source lines are trimmed
   * before comparison in order to remove white spaces differences between html tags.
   *
   * @param pattern - pattern to compare
   * @param source - source to compare
   * @param trimmedLines - flag if lines should be trimmed
   * @return List of Delta changes
   */
  public List<ResultDelta> generateDiffs(String pattern, String source, boolean trimmedLines) {
    List<String> patternList = Arrays.asList(StringUtils.split(pattern, NEW_LINE));
    List<String> sourceList = Arrays.asList(StringUtils.split(source, NEW_LINE));
    if (trimmedLines) {
      patternList = Lists.transform(patternList, TRIM_FUNCTION);
      sourceList = Lists.transform(sourceList, TRIM_FUNCTION);
    }
    Patch patch = DiffUtils.diff(patternList, sourceList);
    List<Delta> deltas = patch.getDeltas();
    return addFullSource(deltas, patternList, sourceList);
  }

  protected ResultDelta processDelta(Delta delta) {
    String originalLines = StringUtils.join(delta.getOriginal().getLines(), NEW_LINE);
    String revisedLines = StringUtils.join(delta.getRevised().getLines(), NEW_LINE);
    String originalChunkHtml;
    String revisedChunkHtml;

    if (Delta.TYPE.CHANGE.equals(delta.getType())) {
      LinkedList<Diff> diffList = diffMatchPatch.diff_main(originalLines, revisedLines);
      diffMatchPatch.diff_cleanupSemantic(diffList);

      LinkedList<Diff> originalDiffList = Lists.newLinkedList();
      LinkedList<Diff> revisedDiffList = Lists.newLinkedList();

      for (Diff diff : diffList) {
        switch (diff.operation) {
          case DELETE:
            originalDiffList.add(diff);
            break;
          case EQUAL:
            originalDiffList.add(diff);
            revisedDiffList.add(diff);
            break;
          case INSERT:
            revisedDiffList.add(diff);
            break;
          default:
            break;
        }
      }
      originalChunkHtml = diffMatchPatch.diff_prettyHtml(originalDiffList);
      revisedChunkHtml = diffMatchPatch.diff_prettyHtml(revisedDiffList);
    } else {
      originalChunkHtml = StringEscapeUtils.escapeHtml4(originalLines);
      revisedChunkHtml = StringEscapeUtils.escapeHtml4(revisedLines);
    }
    return buildDelta(originalChunkHtml, revisedChunkHtml, delta);
  }

  private ResultChunk getNoChangesResultChunk(List<String> linesList, int position) {
    String prettyHtml = StringEscapeUtils.escapeHtml4(StringUtils.join(linesList, NEW_LINE));
    return new ResultChunk(position, prettyHtml);
  }

  private ResultDelta buildDelta(String originalChunkHtml, String revisedChunkHtml, Delta delta) {
    String localOriginalChunkHtml = originalChunkHtml;
    String localRevisedChunkHtml = revisedChunkHtml;
    int originalLinesNo = delta.getOriginal().getLines().size();
    int revisedLinesNo = delta.getRevised().getLines().size();

    int sizeDiff = Math.abs(originalLinesNo - revisedLinesNo);

    if (sizeDiff > 0) {
      if (delta.getType().equals(Delta.TYPE.CHANGE)) {
        // I don't know why, but it have to be like that.
        sizeDiff++;
      }
      String alignment = StringUtils.repeat(BR_TAG, sizeDiff);
      if (originalLinesNo > revisedLinesNo) {
        localRevisedChunkHtml += alignment;
      } else if (originalLinesNo < revisedLinesNo) {
        localOriginalChunkHtml += alignment;
      }
    }

    int originalPosition = delta.getOriginal().getPosition();
    ResultChunk original = new ResultChunk(originalPosition, localOriginalChunkHtml);
    int revisedPosition = delta.getRevised().getPosition();
    ResultChunk revised = new ResultChunk(revisedPosition, localRevisedChunkHtml);
    return new ResultDelta(TYPE.valueOf(delta.getType().name()), original, revised);
  }

  private List<ResultDelta> addFullSource(List<Delta> deltaList, List<String> originalList,
      List<String> revisedList) {
    List<ResultDelta> deltaListWithSource = new ArrayList<>();
    int lastPositionOriginal = 0;
    int lastPositionRevised = 0;

    for (Delta delta : deltaList) {
      // original
      int sizeOriginal = delta.getOriginal().getLines().size();
      int positionOriginal = delta.getOriginal().getPosition();
      List<String> originalSubList = originalList.subList(lastPositionOriginal, positionOriginal);
      ResultChunk originalChunk = getNoChangesResultChunk(originalSubList, lastPositionOriginal);
      lastPositionOriginal = sizeOriginal + positionOriginal;
      // revised
      int sizeRevised = delta.getRevised().getLines().size();
      int positionRevised = delta.getRevised().getPosition();
      List<String> revisedSubList = revisedList.subList(lastPositionRevised, positionRevised);
      ResultChunk revisedChunk = getNoChangesResultChunk(revisedSubList, lastPositionRevised);
      lastPositionRevised = sizeRevised + positionRevised;
      ResultDelta resultDelta = new ResultDelta(TYPE.NO_CHANGE, originalChunk, revisedChunk);
      deltaListWithSource.add(resultDelta);
      deltaListWithSource.add(processDelta(delta));
    }
    if ((originalList.size() > lastPositionOriginal || revisedList.size() > lastPositionRevised)
        && !deltaList.isEmpty()) {
      ResultDelta resultDelta = prepareNoChangeResultDelta(originalList, revisedList,
          lastPositionOriginal, lastPositionRevised);
      deltaListWithSource.add(resultDelta);
    }
    return deltaListWithSource;
  }

  private ResultDelta prepareNoChangeResultDelta(List<String> originalList,
      List<String> revisedList, int lastPositionOriginal, int lastPositionRevised) {
    int originalListSize = originalList.size();
    int revisedListSize = revisedList.size();
    List<String> originalSubList = originalList.subList(lastPositionOriginal, originalListSize);
    ResultChunk originalChunk = getNoChangesResultChunk(originalSubList, lastPositionOriginal);
    List<String> revisedSubList = revisedList.subList(lastPositionRevised, revisedListSize);
    ResultChunk revisedChunk = getNoChangesResultChunk(revisedSubList, lastPositionRevised);
    return new ResultDelta(TYPE.NO_CHANGE, originalChunk, revisedChunk);
  }
}
