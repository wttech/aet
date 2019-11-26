package com.cognifide.aet.models;

import java.util.List;

public class StatusCodeResult extends Error {

  private List<StatusCode> statusCodes;
  private List<StatusCode> filteredStatusCodes;
  private List<StatusCode> excludedStatusCodes;
}
