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
package com.cognifide.aet.communication.api.job;

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.metadata.Comparator;
import com.google.common.base.MoreObjects;
import java.io.Serializable;

/**
 * Model which stores comparison phase results.
 */
public class ComparatorResultData implements Serializable {

  private static final long serialVersionUID = -1152354275730001870L;

  private JobStatus status;

  private ProcessingError processingError;

  private Comparator comparisonResult;

  private final String testName;

  private final String urlName;

  private final Integer stepIndex;

  private ComparatorResultData(Builder builder) {
    status = builder.status;
    processingError = builder.processingError;
    comparisonResult = builder.comparisonResult;
    testName = builder.testName;
    urlName = builder.urlName;
    stepIndex = builder.stepIndex;
  }

  public static Builder newBuilder(String testName, String urlName, Integer stepIndex) {
    return new Builder(testName, urlName, stepIndex);
  }

  /**
   * @return status of comparison work.
   */
  public JobStatus getStatus() {
    return status;
  }

  /**
   * @return error that caused comparator failure.
   */
  public ProcessingError getProcessingError() {
    return processingError;
  }

  /**
   * @return results of comparison
   */
  public Comparator getComparisonResult() {
    return comparisonResult;
  }

  /**
   * @return unique within suite name of a test where comparison was performed.
   */
  public String getTestName() {
    return testName;
  }

  /**
   * @return name of url which was target of comparison.
   */
  public String getUrlName() {
    return urlName;
  }

  /**
   * @return index of step in url collection steps.
   */
  public Integer getStepIndex() {
    return stepIndex;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("urlName", urlName)
        .add("testName", testName)
        .add("stepIndex", stepIndex)
        .add("status", status)
        .add("processingError", processingError)
        .add("comparisonResult", comparisonResult)
        .toString();
  }

  public static final class Builder {

    private JobStatus status;
    private ProcessingError processingError;
    private Comparator comparisonResult;
    private final String testName;
    private final String urlName;
    private final Integer stepIndex;

    private Builder(String testName, String urlName, Integer stepIndex) {
      this.testName = testName;
      this.urlName = urlName;
      this.stepIndex = stepIndex;
    }

    public Builder withStatus(JobStatus val) {
      status = val;
      return this;
    }

    public Builder withProcessingError(ProcessingError val) {
      processingError = val;
      return this;
    }

    public Builder withComparisonResult(Comparator val) {
      comparisonResult = val;
      return this;
    }

    public ComparatorResultData build() {
      return new ComparatorResultData(this);
    }
  }
}
