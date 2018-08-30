package com.cognifide.aet.communication.api.wrappers;

import com.cognifide.aet.communication.api.messages.MessageType;
import com.cognifide.aet.communication.api.metadata.Url;

public class UrlRunWrapper implements Run {

  private Url url;

  public UrlRunWrapper(Url url) {
    this.url = url;
  }

  @Override
  public MessageType getMessageType() {
    return MessageType.RERUN_URL;
  }

  @Override
  public Url getObjectToRun() {
    return url;
  }
}
