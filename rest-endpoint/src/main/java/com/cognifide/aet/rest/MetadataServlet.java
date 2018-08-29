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

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class MetadataServlet extends BasicDataServlet {

  private static final long serialVersionUID = 7233205495217724069L;

  private static final Logger LOGGER = LoggerFactory.getLogger(MetadataServlet.class);

  private static final String FORMATTED_PARAM = "formatted";
  private static final Gson PRETTY_PRINT_GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final JsonParser JSON_PARSER = new JsonParser();

  @Reference
  private MetadataDAO metadataDAO;

  @Reference
  private LockService lockService;

  @Reference
  private transient HttpService httpService;

  @Override
  protected void process(DBKey dbKey, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String correlationId = req.getParameter(Helper.CORRELATION_ID_PARAM);
    String suiteName = req.getParameter(Helper.SUITE_PARAM);
    String suiteVersion = req.getParameter(Helper.VERSION_PARAM);
    String formatted = req.getParameter(FORMATTED_PARAM);
    resp.setCharacterEncoding("UTF-8");

    Suite suite;

    try {
      if (isValidCorrelationId(correlationId)) {
        suite = metadataDAO.getSuite(dbKey, correlationId);
      } else if (isValidName(suiteName)) {
        if (isValidVersion(suiteVersion)) {
          suite = metadataDAO.getSuite(dbKey, suiteName, suiteVersion);
        } else {
          suite = metadataDAO.getLatestRun(dbKey, suiteName);
        }
      } else {
        resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
        resp.getWriter()
            .write(responseAsJson("Neither valid correlationId or suite param was specified."));
        return;
      }
    } catch (StorageException e) {
      LOGGER.error("Failed to get suite", e);
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.getWriter().write(responseAsJson("Failed to get suite: %s", e.getMessage()));
      return;
    }

    if (suite != null) {
      String result = suite.toJson();
      if (BooleanUtils.toBoolean(formatted)) {
        result = PRETTY_PRINT_GSON.toJson(JSON_PARSER.parse(result).getAsJsonObject());
      }
      resp.setContentType("application/json");
      resp.getWriter().write(result);
    } else {
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.getWriter()
          .write(responseAsJson("Unable to get Suite Metadata for %s", dbKey.toString()));
    }
  }

  @Override
  protected HttpService getHttpService() {
    return this.httpService;
  }

  @Override
  protected void setHttpService(HttpService httpService) {
    this.httpService = httpService;
  }

  /***
   * Saves Suite parsed from json provided in post body.
   * It also increments Version of Suite.
   *
   * @param req  Request
   * @param resp Response
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("POST: " + req.toString());
    }
    resp.setContentType("application/json");
    try {
      updateSuite(req);
      resp.setStatus(HttpURLConnection.HTTP_OK);
    } catch (AETException e) {
      LOGGER.debug("Suite is locked.", e);
      resp.setStatus(HttpURLConnection.HTTP_CONFLICT);
      resp.getWriter().write(responseAsJson(e.getMessage()));
    } catch (JsonSyntaxException | JsonIOException jsonSyntaxException) {
      LOGGER.error("Invalid json provided by client", jsonSyntaxException);
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.getWriter().write(responseAsJson("Error: Invalid format of provided Json %s",
          jsonSyntaxException.getMessage()));
    } catch (ValidatorException validatorException) {
      LOGGER.error("Invalid json provided by client: {}", validatorException.getIssues(),
          validatorException);
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.getWriter().write(
          responseAsJson("Invalid Suite representation : %s", validatorException.getIssues()));
    } catch (StorageException e) {
      LOGGER.error("Failed to save suite", e);
      resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
      resp.getWriter()
          .write(responseAsJson("ERROR: Unable to save provided Suite. %s", e.getMessage()));
    } finally {
      resp.flushBuffer();
    }
  }

  private void updateSuite(HttpServletRequest req)
      throws StorageException, ValidatorException, AETException, IOException {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
    Suite suite = Suite.fromJson(reader);
    checkLock(suite.getSuiteIdentifier());
    checkAnotherSuitePattern(suite.getPatternCorrelationId());
    metadataDAO.updateSuite(suite);
  }

  private void checkAnotherSuitePattern(String patternCorrelationId) throws AETException {
    if (patternCorrelationId != null) {
      String msg = "Accepting changes not allowed as comparison was done against another suite: '";
      msg += patternCorrelationId + "'.";
      throw new AETException(msg);
    }
  }

  private void checkLock(String suiteIdentifier) throws AETException {
    if (lockService.isLockPresent(suiteIdentifier)) {
      throw new AETException("Suite modification is currently locked. Please try again later.");
    }
  }

  @Activate
  public void start() {
    register(Helper.getMetadataPath());

  }

  @Deactivate
  public void stop() {
    unregister(Helper.getMetadataPath());
  }
}
