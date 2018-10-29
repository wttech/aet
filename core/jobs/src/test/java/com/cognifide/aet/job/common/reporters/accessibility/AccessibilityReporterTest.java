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
package com.cognifide.aet.job.common.reporters.accessibility;

import static com.cognifide.aet.job.common.reporters.accessibility.AccessibilityReporter.ACCESSIBILITY_TYPE_NAME;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue;
import com.cognifide.aet.job.common.collectors.accessibility.AccessibilityIssue.IssueType;
import com.cognifide.aet.job.common.comparators.accessibility.report.AccessibilityReport;
import com.cognifide.aet.job.common.reporters.Bug;
import com.cognifide.aet.job.common.reporters.ReportIssue;
import com.cognifide.aet.job.common.reporters.accessibility.format.AccessibilityFormatter;
import com.cognifide.aet.job.common.reporters.accessibility.format.bug.AccessibilityBug;
import com.cognifide.aet.job.common.reporters.dto.ReporterDto;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccessibilityReporterTest {

  private static final String SUITE_CORRELATION_ID = "foo";

  private static final String ACCESSIBILITY_REPORT_ARTIFACT_ID = "foo2";

  @Mock
  private Suite suite;

  @Mock
  private com.cognifide.aet.communication.api.metadata.Test test;

  @Mock
  private Url url;

  @Mock
  private Step step;

  @Mock
  private Comparator comparator;

  @Mock
  private ComparatorStepResult stepResult;

  @Mock
  private AccessibilityReport report;

  @Mock
  private AccessibilityIssue issue1;

  @Mock
  private AccessibilityIssue issue2;

  @Mock
  private AccessibilityBug bug1;

  @Mock
  private AccessibilityBug bug2;

  @Mock
  private DBKey dbKey;

  @Mock
  private MetadataDAO metadataDAO;

  @Mock
  private ReporterDto reporterDto;

  @Mock
  private ArtifactsDAO artifactsDAO;

  @Mock
  private AccessibilityFormatter formatter;

  @InjectMocks
  private AccessibilityReporter reporter;

  @Captor
  private ArgumentCaptor<List<Bug>> captor;

  @Before
  public void setup() throws Exception {
    when(metadataDAO.getSuite(any(), any())).thenReturn(suite);
    when(suite.getTests()).thenReturn(Collections.singletonList(test));
    when(test.getUrls()).thenReturn(Collections.singleton(url));
    when(url.getSteps()).thenReturn(Collections.singletonList(step));
    when(step.getComparators()).thenReturn(Collections.singleton(comparator));
    when(step.getName()).thenReturn(ACCESSIBILITY_TYPE_NAME);
    when(comparator.getStepResult()).thenReturn(stepResult);
    when(comparator.getName()).thenReturn(ACCESSIBILITY_TYPE_NAME);
    when(stepResult.getArtifactId()).thenReturn(ACCESSIBILITY_REPORT_ARTIFACT_ID);

    when(artifactsDAO.getJsonFormatArtifact(any(), any(), any())).thenReturn(report);
    when(issue1.getType()).thenReturn(IssueType.ERROR);
    when(issue2.getType()).thenReturn(IssueType.WARN);
  }

  @Test
  public void accessibilityReporter_shouldReportAll_whenManyRequested() {
    when(report.getNonExcludedIssues()).thenReturn(Arrays.asList(issue1, issue2));
    when(formatter.format(Arrays.asList(issue1, issue2))).thenReturn(Arrays.asList(bug1, bug2));

    reporter.report(dbKey, SUITE_CORRELATION_ID);

    verify(formatter).format(Arrays.asList(issue1, issue2));
    verify(reporterDto).report(captor.capture());

    assertEquals(2, captor.getValue().size());
  }

  @Test
  public void accessibilityReporter_shouldReportOne_whenOneRequested() {
    when(report.getNonExcludedIssues()).thenReturn(Collections.singletonList(issue1));
    when(formatter.format(Collections.singletonList(issue1)))
        .thenReturn(Collections.singletonList(bug1));

    reporter.report(dbKey, SUITE_CORRELATION_ID);

    verify(formatter).format(Collections.singletonList(issue1));
    verify(reporterDto).report(captor.capture());

    assertEquals(1, captor.getValue().size());
  }

  @Test
  public void accessibilityReporter_shouldNotReport_whenNoneRequested() {
    when(report.getNonExcludedIssues()).thenReturn(Collections.emptyList());
    when(formatter.format(Collections.emptyList())).thenReturn(Collections.emptyList());

    List<ReportIssue> reportedBugs = reporter.report(dbKey, SUITE_CORRELATION_ID);

    verify(formatter).format(Collections.emptyList());
    verifyNoMoreInteractions(reporterDto);

    assertEquals(0, reportedBugs.size());
  }

  @Test
  public void accessibilityReporter_shouldNotReport_whenOnlyNoticesInReport() {
    when(issue1.getType()).thenReturn(IssueType.NOTICE);
    when(issue2.getType()).thenReturn(IssueType.NOTICE);
    when(report.getNonExcludedIssues()).thenReturn(Arrays.asList(issue1, issue2));
    when(formatter.format(Collections.emptyList())).thenReturn(Collections.emptyList());

    List<ReportIssue> reportedBugs = reporter.report(dbKey, SUITE_CORRELATION_ID);

    verify(formatter).format(Collections.emptyList());
    verifyNoMoreInteractions(reporterDto);

    assertEquals(0, reportedBugs.size());
  }
}