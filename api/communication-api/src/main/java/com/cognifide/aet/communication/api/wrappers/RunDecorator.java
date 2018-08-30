package com.cognifide.aet.communication.api.wrappers;

import com.cognifide.aet.communication.api.messages.MessageType;

public abstract class RunDecorator implements Run{
  protected Run decoratedRun;

  public RunDecorator(Run decoratedRun) {
    this.decoratedRun = decoratedRun;
  }

  @Override
  public MessageType getMessageType() {
    return decoratedRun.getMessageType();
  }
}
