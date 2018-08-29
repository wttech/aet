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

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.util.ValidatorProvider;
import com.cognifide.aet.vs.DBKey;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class BasicDataServlet extends HttpServlet {

  private static final long serialVersionUID = -5819760668750910009L;

  private static final Logger LOGGER = LoggerFactory.getLogger(BasicDataServlet.class);

  private static final Gson GSON = new Gson();

  /***
   * Returns JSON representation of Suite based correlationId or suite name
   * Returned test suite is always in newest version.
   *
   * @param req  Request
   * @param resp Response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("GET: " + req.toString());
    }

    DBKey dbKey;
    try {
      dbKey = Helper.getDBKeyFromRequest(req);
    } catch (ValidatorException e) {
      LOGGER.error("Validation problem!", e);
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.getWriter().write(responseAsJson("There were validation errors when parsing suite: %s",
          e.getAllViolationMessages()));
      return;
    }

    process(dbKey, req, resp);
  }

  void register(String servletPath) {
    LOGGER.debug("Registering servlet at ", servletPath);
    try {
      getHttpService().registerServlet(servletPath, this, null, null);
    } catch (Exception e) {
      LOGGER.error("Failed to register servlet at ", servletPath, e);
    }
  }

  void unregister(String servletPath) {
    getHttpService().unregister(servletPath);
    setHttpService(null);
  }

  boolean isValidName(String suiteName) {
    return ValidatorProvider.getValidator().validateValue(Suite.class, "name", suiteName).isEmpty();
  }

  boolean isValidVersion(String version) {
    boolean valid = false;
    if (version != null) {
      try {
        Integer.parseInt(version);
        valid = true;
      } catch (NumberFormatException ex) {
        // Ok, continue
      }
    }
    return valid;
  }

  boolean isValidCorrelationId(String correlationId) {
    return ValidatorProvider.getValidator()
        .validateValue(Suite.class, "correlationId", correlationId).isEmpty();
  }

  protected abstract void process(DBKey dbKey, HttpServletRequest req, HttpServletResponse resp)
      throws IOException;

  protected String responseAsJson(String format, Object... args) {
    return GSON.toJson(new ErrorMessage(format, args));
  }

  protected abstract HttpService getHttpService();

  protected abstract void setHttpService(HttpService httpService);

  private static class ErrorMessage {

    private final String message;

    ErrorMessage(String format, Object... args) {
      message = String.format(format, args);
    }

    public String getMessage() {
      return message;
    }
  }

}
