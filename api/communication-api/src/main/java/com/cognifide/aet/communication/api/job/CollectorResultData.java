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
import com.cognifide.aet.communication.api.metadata.Url;
import com.google.common.base.MoreObjects;
import java.io.Serializable;

/**
 * Model which stores collection phase results.
 */
public final class CollectorResultData implements Serializable {

  private static final long serialVersionUID = 2290282611303131656L;

  private final JobStatus status;

  private final ProcessingError processingError;

  private final Url urlAfterCollect;

  private final String requestMessageId;

  private final String testName;

  private CollectorResultData(JobStatus status, ProcessingError processingError,
      Url urlAfterCollect, String requestMessageId, String testName) {
    this.status = status;
    this.processingError = processingError;
    this.urlAfterCollect = urlAfterCollect;
    this.requestMessageId = requestMessageId;
    this.testName = testName;
  }

  /**
   * @param urlAfterCollect - url with data after collect phase.
   * @param processingError - error that caused collector failure.
   * @param requestJMSMessageID - id of jms message that invoked collection execution.
   * @param testName - name of the test that collection is part of.
   * @return new instance of CollectorResultData with {@link JobStatus#ERROR error} status.
   */
  public static CollectorResultData createErrorResult(Url urlAfterCollect,
      ProcessingError processingError,
      String requestJMSMessageID, String testName) {
    return new CollectorResultData(JobStatus.ERROR, processingError, urlAfterCollect,
        requestJMSMessageID, testName);
  }

  /**
   * @param urlAfterCollect - url with data after collect phase.
   * @param requestJMSMessageID - id of jms message that invoked collection execution.
   * @param testName - name of the test that collection is part of.
   * @return new instance of CollectorResultData with {@link JobStatus#SUCCESS success} status.
   */
  public static CollectorResultData createSuccessResult(Url urlAfterCollect,
      String requestJMSMessageID, String testName) {
    return new CollectorResultData(JobStatus.SUCCESS, null, urlAfterCollect, requestJMSMessageID,
        testName);
  }

  /**
   * @return status of collector work.
   */
  public JobStatus getStatus() {
    return status;
  }

  /**
   * @return error that caused collector failure.
   */
  public ProcessingError getProcessingError() {
    return processingError;
  }

  /**
   * @return url with collection results.
   */
  public Url getUrl() {
    return urlAfterCollect;
  }

  /**
   * @return id of jms message that invoked collection execution.
   */
  public String getRequestMessageId() {
    return requestMessageId;
  }

  /**
   * @return name of the test that collection is part of.
   */
  public String getTestName() {
    return testName;
  }


  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("testName", testName)
        .add("urlAfterCollect", urlAfterCollect)
        .add("status", status)
        .add("processingError", processingError)
        .add("requestMessageId", requestMessageId)
        .toString();
  }
}
