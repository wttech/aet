package com.cognifide.aet.communication.api.wrappers;

import com.cognifide.aet.communication.api.messages.MessageType;
import com.cognifide.aet.communication.api.metadata.Suite;

public class SuiteRunWrapper implements Run {
  private Suite suite;

  public SuiteRunWrapper(Suite suite) {
    this.suite = suite;
  }

  @Override
  public MessageType getMessageType() {
    return MessageType.RUN;
  }

  @Override
  public Suite getObjectToRun(){
    return suite;
  }

  @Override
  public String getCorrelationId(){
    return suite.getCorrelationId();
  }

  @Override
  public String getCompany(){
    return suite.getCompany();
  }

  @Override
  public String getName() {
    return suite.getName();
  }

  @Override
  public String getSuiteIdentifier() {
    return suite.getSuiteIdentifier();
  }

  @Override
  public String getProject(){
    return suite.getProject();
  }

}
