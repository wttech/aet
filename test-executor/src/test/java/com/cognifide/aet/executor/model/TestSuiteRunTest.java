package com.cognifide.aet.executor.model;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class TestSuiteRunTest {

  @Test
  public void constructor_whenOverridingNameAndDomain_expectUpdatedValues() {
    TestSuiteRun baseSuiteRun = new TestSuiteRun("abcd", "copmany", "project", "ijkl", Collections.emptyList());

    String newName = "efgh";
    String newDomain = "mnop";
    TestSuiteRun updatedSuiteRun = new TestSuiteRun(baseSuiteRun, newName, newDomain, Collections.emptyList());

    assertThat(updatedSuiteRun.getName(), equalTo(newName));
    assertThat(updatedSuiteRun.getDomain(), equalTo(newDomain));
    assertThat(updatedSuiteRun.getCorrelationId(), containsString(newName));
  }

}