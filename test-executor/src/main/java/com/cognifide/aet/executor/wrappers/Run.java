package com.cognifide.aet.executor.wrappers;

import com.cognifide.aet.communication.api.messages.MessageType;
import java.io.Serializable;

public interface Run<T> extends Serializable {

  MessageType getMessageType();

  T getObjectToRun();

  default String getCorrelationId() {
    return null;
  }

  default String getProject() {
    return null;
  }

  default String getCompany() {
    return null;
  }

  default String getName() {
    return null;
  }

  default String getSuiteIdentifier() {
    return null;
  }
}
