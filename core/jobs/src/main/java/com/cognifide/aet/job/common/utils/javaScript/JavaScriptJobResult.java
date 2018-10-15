package com.cognifide.aet.job.common.utils.javaScript;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;

public class JavaScriptJobResult {

  private final CollectorStepResult collectorStepResult;
  private final Object executionResult;

  JavaScriptJobResult(
      CollectorStepResult collectorStepResult, Object executionResult) {
    this.collectorStepResult = collectorStepResult;
    this.executionResult = executionResult;
  }

  public CollectorStepResult getCollectorStepResult() {
    return collectorStepResult;
  }

  public Object getExecutionResult() {
    return executionResult;
  }
}
