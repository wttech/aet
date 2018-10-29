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
package com.cognifide.aet.job.common.reporters.accessibility.format;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import com.cognifide.aet.job.common.reporters.Bug;
import com.cognifide.aet.job.common.reporters.accessibility.format.bug.AccessibilityBug;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccessibilityFormatterTest {

  private static final String CODE_1 = "WCAG2AAA.Principle1.Guideline1_4.1_1_4.H25.1.NoTitleEl";

  private static final String URL_1 = "/url/1";

  private static final String MESSAGE_1 = "error message 1";

  private static final int LINE_NUMBER_1 = 1;

  private static final String CODE_2 = "WCAG2AAA.Principle2.Guideline2_4.2_4_2.H20.1.NoTitleEl";

  private static final String URL_2 = "/url/2";

  private static final String MESSAGE_2 = "error message 2";

  private static final int LINE_NUMBER_2 = 2;

  @Mock
  private AccessibilityIssue issue1;

  @Mock
  private AccessibilityIssue issue2;

  @Before
  public void setup() {
    when(issue1.getCode()).thenReturn(CODE_1);
    when(issue1.getUrl()).thenReturn(URL_1);
    when(issue1.getMessage()).thenReturn(MESSAGE_1);
    when(issue1.getLineNumber()).thenReturn(LINE_NUMBER_1);

    when(issue2.getCode()).thenReturn(CODE_2);
    when(issue2.getUrl()).thenReturn(URL_2);
    when(issue2.getMessage()).thenReturn(MESSAGE_2);
    when(issue2.getLineNumber()).thenReturn(LINE_NUMBER_2);
  }

  @Test
  public void shouldFormatBugsByCode_WhenDistinctIssuesSupplied() {
    List<Bug> bugs = new AccessibilityFormatter().format(Arrays.asList(issue1, issue2));

    assertEquals(2, bugs.size());
    AccessibilityBug bug1 = (AccessibilityBug) bugs.get(0);
    AccessibilityBug bug2 = (AccessibilityBug) bugs.get(1);

    assertEquals(bug1.getCases().size(), 1);
    assertEquals(bug1.getCases().get(0).getErrorMessage(), MESSAGE_1);
    assertEquals(bug1.getCases().get(0).getOccurences().get(0).getUrl(), URL_1);
    assertEquals(bug1.getCases().get(0).getOccurences().get(0).getLineNumbers().get(0),
        Integer.valueOf(LINE_NUMBER_1));
    assertEquals(bug2.getCases().size(), 1);
    assertEquals(bug2.getCases().get(0).getErrorMessage(), MESSAGE_2);
    assertEquals(bug2.getCases().get(0).getOccurences().get(0).getUrl(), URL_2);
    assertEquals(bug2.getCases().get(0).getOccurences().get(0).getLineNumbers().get(0),
        Integer.valueOf(LINE_NUMBER_2));
  }

  @Test
  public void shouldFormatBugsByLineNumbers_WhenDuplicateIssuesSupplied() {
    List<Bug> bugs = new AccessibilityFormatter().format(Arrays.asList(issue1, issue1));

    assertEquals(1, bugs.size());
    AccessibilityBug bug = (AccessibilityBug) bugs.get(0);

    assertEquals(bug.getCases().get(0).getOccurences().get(0).getLineNumbers().size(), 2);
  }

  @Test
  public void shouldFormatBugsByMessage_WhenSameCodesSupplied() {
    when(issue1.getCode()).thenReturn(CODE_1);
    when(issue2.getCode()).thenReturn(CODE_1);

    List<Bug> bugs = new AccessibilityFormatter().format(Arrays.asList(issue1, issue2));

    assertEquals(1, bugs.size());
    AccessibilityBug bug = (AccessibilityBug) bugs.get(0);

    assertEquals(bug.getCases().size(), 2);
  }
}