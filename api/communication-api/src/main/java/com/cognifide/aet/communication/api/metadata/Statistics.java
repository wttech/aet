package com.cognifide.aet.communication.api.metadata;

import java.io.Serializable;

public class Statistics implements Serializable {

  private static final long serialVersionUID = -5219905173862829626L;
  private long startTimestamp;
  private long endTimestamp;

  public long getStartTimestamp() {
    return startTimestamp;
  }

  public void setStartTimestamp(long startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  public long getEndTimestamp() {
    return endTimestamp;
  }

  public void setEndTimestamp(long endTimestamp) {
    this.endTimestamp = endTimestamp;
  }
}
