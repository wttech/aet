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
package com.cognifide.aet.xunit;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.communication.api.metadata.CollectorStepResult.Status;
import com.cognifide.aet.communication.api.metadata.Comparator;
import com.cognifide.aet.communication.api.metadata.ComparatorStepResult;
import com.cognifide.aet.communication.api.metadata.Step;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.Test;
import com.cognifide.aet.communication.api.metadata.Url;
import com.cognifide.aet.xunit.model.Failure;
import com.cognifide.aet.xunit.model.Testcase;
import com.cognifide.aet.xunit.model.Testsuite;
import com.cognifide.aet.xunit.model.Testsuites;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MetadataToXUnitConverter {

  private static final EnumSet<CollectorStepResult.Status> COLLECTORS_STEPS = EnumSet.of(
      CollectorStepResult.Status.COLLECTED,
      CollectorStepResult.Status.DUPLICATES_PATTERN,
      CollectorStepResult.Status.PROCESSING_ERROR
  );

  private static final EnumSet<ComparatorStepResult.Status> COMPARATOR_FAILURE_STATUSES = EnumSet
      .of(
          ComparatorStepResult.Status.FAILED,
          ComparatorStepResult.Status.PROCESSING_ERROR
      );

  private static final String NAME_KEY = "name";

  private final Suite suite;

  private int xUnitTestsTotal = 0;

  private int xUnitFailedTotal = 0;

  public MetadataToXUnitConverter(final Suite suite) {
    this.suite = suite;
  }

  public Testsuites convert() {
    final Testsuites xUnitRoot = new Testsuites();
    xUnitRoot.setName(suite.getName());

    final List<Testsuite> xUnitRootTestsuite = xUnitRoot.getTestsuite();
    for (Test test : suite.getTests()) {
      convertTestToXUnitSuite(test, xUnitRootTestsuite);
    }

    xUnitRoot.setTests(String.valueOf(xUnitTestsTotal));
    xUnitRoot.setFailures(String.valueOf(xUnitFailedTotal));
    return xUnitRoot;
  }

  private void convertTestToXUnitSuite(Test test, List<Testsuite> xUnitSuites) {
    final Testsuite xUnitTestsuite = new Testsuite();
    xUnitTestsuite.setName(test.getName());

    final List<Testcase> xUnitCases = xUnitTestsuite.getTestcase();
    for (Url url : test.getUrls()) {
      convertStepsToXUnitCase(url, xUnitCases);
    }

    xUnitTestsuite.setTests(String.valueOf(xUnitCases.size()));
    xUnitSuites.add(xUnitTestsuite);
  }

  private int convertStepsToXUnitCase(Url url, List<Testcase> xUnitCases) {
    for (Step step : url.getSteps()) {
      traverseSteps(step, url.getName(), xUnitCases);
    }
    return xUnitCases.size();
  }

  private void traverseSteps(Step step, String urlName, List<Testcase> xUnitCases) {
    final CollectorStepResult stepResult = step.getStepResult();
    final CollectorStepResult.Status stepStatus;
    if (stepResult == null) {
      stepStatus = Status.PROCESSING_ERROR;
    } else {
      stepStatus = stepResult.getStatus();
    }
    final Set<Comparator> comparators = step.getComparators();
    if (comparators != null && COLLECTORS_STEPS.contains(stepStatus)) {
      for (Comparator comparator : comparators) {
        final Testcase xUnitCase = new Testcase();
        CaseNameBuilder caseNameBuilder = new CaseNameBuilder(comparator.getType())
            .withStepParameters(step.getParameters())
            .withComparatorParameters(comparator.getParameters())
            .withUrlName(urlName);
        xUnitCase.setName(caseNameBuilder.build());
        addFailures(step, stepStatus, comparator, xUnitCase);
        xUnitTestsTotal++;
        xUnitCases.add(xUnitCase);
      }
    }

  }

  private void addFailures(Step step, CollectorStepResult.Status stepStatus, Comparator comparator,
      Testcase xUnitCase) {
    final List<Failure> xUnitCaseFailure = xUnitCase.getFailure();

    if (stepStatus == CollectorStepResult.Status.PROCESSING_ERROR) {
      final CollectorStepResult stepResult = step.getStepResult();
      final List<String> errors;
      if (stepResult == null) {
        errors = Collections.singletonList("Fatal error");
      } else {
        errors = stepResult.getErrors();
      }

      final Failure failure = buildFailure(errors, stepStatus.name());
      xUnitCaseFailure.add(failure);
      xUnitFailedTotal++;

    } else {
      final ComparatorStepResult.Status comparatorStatus = comparator.getStepResult().getStatus();
      if (COMPARATOR_FAILURE_STATUSES.contains(comparatorStatus)) {
        final Failure failure = buildFailure(comparator.getStepResult().getErrors(),
            comparatorStatus.name());
        xUnitCaseFailure.add(failure);
        xUnitFailedTotal++;
      }
    }
  }

  private Failure buildFailure(List<String> errors, String type) {
    final Failure failure = new Failure();
    if (!errors.isEmpty()) {
      failure.setMessage(errors.toString());
    }
    failure.setType(type);
    return failure;
  }

  private final class CaseNameBuilder {

    private final StringBuilder name;

    CaseNameBuilder(String comparatorType) {
      this.name = new StringBuilder(comparatorType);
    }

    CaseNameBuilder withUrlName(String urlName) {
      name.append(" on: ")
          .append(urlName);
      return this;
    }

    CaseNameBuilder withStepParameters(Map<String, String> stepParameters) {
      if (stepParameters != null && stepParameters.containsKey(NAME_KEY)) {
        name.append(" named '")
            .append(stepParameters.get(NAME_KEY))
            .append("'");
      }
      return this;
    }

    CaseNameBuilder withComparatorParameters(Map<String, String> comparatorParameters) {
      if (comparatorParameters != null) {
        if (comparatorParameters.containsKey(NAME_KEY)) {
          name.append(" for '")
              .append(comparatorParameters.get(NAME_KEY))
              .append("'");
        }
        if (comparatorParameters.containsKey(Comparator.COMPARATOR_PARAMETER)) {
          name.append(" (")
              .append(comparatorParameters.get(Comparator.COMPARATOR_PARAMETER))
              .append(")");
        }
      }
      return this;
    }

    String build() {
      return name.toString();
    }

  }
}
