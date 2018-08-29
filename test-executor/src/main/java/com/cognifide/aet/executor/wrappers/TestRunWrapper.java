package com.cognifide.aet.executor.wrappers;

import com.cognifide.aet.communication.api.messages.MessageType;
import com.cognifide.aet.communication.api.metadata.Test;

public class TestRunWrapper implements Run {

  private Test test;

  public TestRunWrapper(Test test) {
    this.test = test;
  }

  @Override
  public MessageType getMessageType() {
    return MessageType.RERUN_TEST;
  }

  @Override
  public Test getObjectToRun() {
    return test;
  }
}
