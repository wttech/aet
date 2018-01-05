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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import difflib.ChangeDelta;
import difflib.Chunk;
import difflib.DeleteDelta;
import difflib.Delta;
import difflib.InsertDelta;
import org.junit.Before;
import org.junit.Test;

public class DiffParserTest {

  private DiffParser tested;

  @Before
  public void setUp() {
    tested = getTested();
  }

  @Test
  public void testProcessChangeDelta() {
    String[] originalLines = {"<html>", "Some text", "</html>"};
    String[] revisedLines = {"<html>", "Another text", "</html>"};
    Delta delta = new ChangeDelta(new Chunk(0, originalLines), new Chunk(0, revisedLines));
    ResultDelta resultDelta = tested.processDelta(delta);
    assertThat(
        resultDelta.getOriginal().getPrettyHtml(),
        is("<span>&lt;html&gt;&para;<br></span><del style=\"background:#ffe6e6;\">Some</del><span> text&para;<br>&lt;/html&gt;</span>"));
    assertThat(
        resultDelta.getRevised().getPrettyHtml(),
        is("<span>&lt;html&gt;&para;<br></span><ins style=\"background:#e6ffe6;\">Another</ins><span> text&para;<br>&lt;/html&gt;</span>"));
  }

  @Test
  public void testProcessDeleteDelta() {
    String[] originalLines = {"<html>", "Some text", "</html>"};
    String[] revisedLines = {};
    Delta delta = new DeleteDelta(new Chunk(0, originalLines), new Chunk(0, revisedLines));
    ResultDelta resultDelta = tested.processDelta(delta);
    assertThat(resultDelta.getOriginal().getPrettyHtml(),
        is("&lt;html&gt;\nSome text\n&lt;/html&gt;"));
    assertThat(resultDelta.getRevised().getPrettyHtml(), is("<br><br><br>"));
  }

  @Test
  public void testProcessInsertDelta() {
    String[] originalLines = {};
    String[] revisedLines = {"<html>", "Another text", "</html>"};
    Delta delta = new InsertDelta(new Chunk(0, originalLines), new Chunk(0, revisedLines));
    ResultDelta resultDelta = tested.processDelta(delta);
    assertThat(resultDelta.getOriginal().getPrettyHtml(), is("<br><br><br>"));
    assertThat(resultDelta.getRevised().getPrettyHtml(),
        is("&lt;html&gt;\nAnother text\n&lt;/html&gt;"));
  }

  private DiffParser getTested() {
    return new DiffParser();
  }

}
