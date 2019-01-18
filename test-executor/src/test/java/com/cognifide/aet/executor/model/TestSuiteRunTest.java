package com.cognifide.aet.executor.model;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestSuiteRunTest {

  @Test
  public void constructor_whenOverridingNameAndDomain_expectUpdatedValues() {
    String company = "company";
    String project = "project";
    String name = "abcd";
    String domain = "ijkl";
    TestSuiteRun baseSuiteRun = new TestSuiteRun(name, company, project, domain, Collections.emptyList());

    String newName = "efgh";
    String newDomain = "mnop";
    TestSuiteRun updatedSuiteRun = new TestSuiteRun(baseSuiteRun, newName, newDomain, Collections.emptyList());
    String correlationRegex = String.format("^%s-%s-%s-[0-9]*$", company, project, newName);

    assertThat(updatedSuiteRun.getName(), equalTo(newName));
    assertThat(updatedSuiteRun.getDomain(), equalTo(newDomain));
    assertThat(updatedSuiteRun.getCorrelationId(), updatedSuiteRun.getCorrelationId().matches(correlationRegex));
  }

}