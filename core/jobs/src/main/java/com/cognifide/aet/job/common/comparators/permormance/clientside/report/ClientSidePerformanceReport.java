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
package com.cognifide.aet.job.common.comparators.permormance.clientside.report;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.HashMap;

public class ClientSidePerformanceReport implements Serializable {

  private static final long serialVersionUID = 8807680088768380353L;

  private String v;

  @SerializedName("w")
  private String totalSize;

  @SerializedName("o")
  private String overallScore;

  private String prettyOverallScore;

  @SerializedName("u")
  private String url;

  @SerializedName("r")
  private String totalRequests;

  private String i;

  @SerializedName("g")
  private HashMap<String, YSlowRule> resultSet;

  @SerializedName("w_c")
  private String totalSizePrimed;

  @SerializedName("r_c")
  private String totalRequestsPrimed;

  private HashMap<String, Asset> stats;

  @SerializedName("stats_c")
  private HashMap<String, Asset> statsCached;

  public String getV() {
    return v;
  }

  public void setV(String v) {
    this.v = v;
  }

  public String getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(String totalSize) {
    this.totalSize = totalSize;
  }

  public String getOverallScore() {
    return overallScore;
  }

  public void setOverallScore(String overallScore) {
    this.overallScore = overallScore;
  }

  public String getPrettyOverallScore() {
    return prettyOverallScore;
  }

  public void setPrettyOverallScore(String prettyOverallScore) {
    this.prettyOverallScore = prettyOverallScore;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTotalRequests() {
    return totalRequests;
  }

  public void setTotalRequests(String totalRequests) {
    this.totalRequests = totalRequests;
  }

  public String getI() {
    return i;
  }

  public void setI(String i) {
    this.i = i;
  }

  public HashMap<String, YSlowRule> getResultSet() {
    return resultSet;
  }

  public void setResultSet(HashMap<String, YSlowRule> resultSet) {
    this.resultSet = resultSet;
  }

  public String getTotalSizePrimed() {
    return totalSizePrimed;
  }

  public void setTotalSizePrimed(String totalSizePrimed) {
    this.totalSizePrimed = totalSizePrimed;
  }

  public String getTotalRequestsPrimed() {
    return totalRequestsPrimed;
  }

  public void setTotalRequestsPrimed(String totalRequestsPrimed) {
    this.totalRequestsPrimed = totalRequestsPrimed;
  }

  public HashMap<String, Asset> getStats() {
    return stats;
  }

  public void setStats(HashMap<String, Asset> stats) {
    this.stats = stats;
  }

  public HashMap<String, Asset> getStatsCached() {
    return statsCached;
  }

  public void setStatsCached(HashMap<String, Asset> statsCached) {
    this.statsCached = statsCached;
  }
}
