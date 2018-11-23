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
package com.cognifide.aet.job.common.collectors.accessibility;

import static org.junit.Assert.assertEquals;

import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue.IssueType;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccessibilityIssueMarkupFinderTest {

  private List<AccessibilityIssue> issues;

  @Before
  public void setUp() {
    issues = new ArrayList<>();
  }

  @Test
  public void testOneLineSingleIssue() {
    String html ="<a/>";
    issues.add(new AccessibilityIssue(IssueType.ERROR,"","","<a/>",""));
    issues = new AccessibilityIssueMarkupFinder(html,issues).getIssuesWithPositions();
    verifySingleIssue(issues.get(0),1,1);
  }

  @Test
  public void testOneLineSingleIssueOffset(){
    String html="xxxx<a/>";
    issues.add(new AccessibilityIssue(IssueType.ERROR,"","","<a/>",""));
    issues = new AccessibilityIssueMarkupFinder(html,issues).getIssuesWithPositions();
    verifySingleIssue(issues.get(0),1,5);
  }

  public void verifySingleIssue(AccessibilityIssue issue, int expectedLineNumber, int expectedColumnNumber){
    assertEquals(expectedLineNumber,issue.getLineNumber());
    assertEquals(expectedColumnNumber,expectedColumnNumber);
  }

}