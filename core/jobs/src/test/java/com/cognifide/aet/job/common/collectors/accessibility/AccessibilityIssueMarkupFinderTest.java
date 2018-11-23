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
  public void testSingleIssueNoOffset() {
    String html ="<a/>";
    issues.add(new AccessibilityIssue(IssueType.ERROR,"","","<a/>",""));
    issues = new AccessibilityIssueMarkupFinder(html,issues).getIssuesWithPositions();
    verifySingleIssue(issues.get(0),1,1);
  }

  @Test
  public void testSingleIssueColOffset(){
    String html="xxxx<a/>";
    issues.add(new AccessibilityIssue(IssueType.ERROR,"","","<a/>",""));
    issues = new AccessibilityIssueMarkupFinder(html,issues).getIssuesWithPositions();
    verifySingleIssue(issues.get(0),1,5);
  }

  @Test
  public void testSingleIssueLineColOffset(){
    String html="\nxx\n\nxx<a/>xx";
    issues.add(new AccessibilityIssue(IssueType.ERROR,"","","<a/>",""));
    issues = new AccessibilityIssueMarkupFinder(html,issues).getIssuesWithPositions();
    verifySingleIssue(issues.get(0),4,3);
  }

  @Test
  public void testOneMarkupDifferentTypeIssues(){
    String html="\n\nx<a/>";
    issues.add(new AccessibilityIssue(IssueType.ERROR,"","","<a/>",""));
    issues.add(new AccessibilityIssue(IssueType.NOTICE,"","","<a/>",""));
    issues = new AccessibilityIssueMarkupFinder(html,issues).getIssuesWithPositions();
    verifySingleIssue(issues.get(0),3,2);
    verifySingleIssue(issues.get(1),3,2);
  }

  @Test
  public void testManySameIssues(){
    String html="\nxx\n\nxx<a/>xx\n\n\n<a/>xxx";
    issues.add(new AccessibilityIssue(IssueType.ERROR,"","","<a/>",""));
    issues.add(new AccessibilityIssue(IssueType.ERROR,"","","<a/>",""));
    issues = new AccessibilityIssueMarkupFinder(html,issues).getIssuesWithPositions();
    verifySingleIssue(issues.get(0),4,3);
    verifySingleIssue(issues.get(1),7,1);
  }

  @Test
  public void testNestedIssues(){
    String html="\nx\nxxx\n<div id=\"footer\"><a href=\"test\"></a></div>";
    issues.add(new AccessibilityIssue(IssueType.ERROR,"","","<div id=\"footer\"><a href=\"test\"></a></div>",""));
    issues.add(new AccessibilityIssue(IssueType.ERROR,"","","<a href=\"test\"></a>",""));
    issues = new AccessibilityIssueMarkupFinder(html,issues).getIssuesWithPositions();
    verifySingleIssue(issues.get(0),4,1);
    verifySingleIssue(issues.get(1),4,18);
  }

  public void verifySingleIssue(AccessibilityIssue issue, int expectedLineNumber, int expectedColumnNumber){
    assertEquals(expectedLineNumber,issue.getLineNumber());
    assertEquals(expectedColumnNumber,expectedColumnNumber);
  }

}