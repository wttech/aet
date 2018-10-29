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
package com.cognifide.aet.rest;

import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.job.common.reporters.ReporterService;
import com.cognifide.aet.job.common.reporters.factory.ReportType;
import com.cognifide.aet.vs.DBKey;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ReporterServlet extends BasicDataServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReporterServlet.class);

  @Reference
  private ReporterService reporterService;

  @Reference
  private transient HttpService httpService;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("POST: " + req.toString());
    }
    resp.setContentType("application/json");

    try {
      DBKey dbKeyFromRequest = Helper.getDBKeyFromRequest(req);

      reporterService.report(dbKeyFromRequest, getSuite(req), getReporters(req));

      resp.setStatus(HttpURLConnection.HTTP_OK);
    } catch (ValidatorException e) {
      LOGGER.error("Invalid json provided by client: {}", e.getIssues(), e);
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
    } finally {
      resp.flushBuffer();
    }
  }

  private String getSuite(HttpServletRequest req) {
    throw new NotImplementedException();
  }

  private Set<ReportType> getReporters(HttpServletRequest req) {
    throw new NotImplementedException();
  }

  @Override
  protected void process(DBKey dbKey, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setStatus(HttpURLConnection.HTTP_BAD_METHOD);
  }

  @Override
  protected HttpService getHttpService() {
    return this.httpService;
  }

  @Override
  protected void setHttpService(HttpService httpService) {
    this.httpService = httpService;
  }

  @Activate
  public void start() {
    register(Helper.getReportersPath());

  }

  @Deactivate
  public void stop() {
    unregister(Helper.getReportersPath());
  }
}
