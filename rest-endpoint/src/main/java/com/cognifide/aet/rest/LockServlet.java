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


import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class LockServlet extends HttpServlet {

  private static final long serialVersionUID = 638302620021335857L;

  private static final Logger LOGGER = LoggerFactory.getLogger(LockServlet.class);

  private static final String VALUE_PARAM = "value";

  private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";

  private static final Gson GSON = new Gson();

  @Reference
  private transient HttpService httpService;

  @Reference
  private LockService lockService;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("GET: " + req.toString());
    }
    resp.setContentType(APPLICATION_JSON_CONTENT_TYPE);

    String key = getKey(req);
    if (StringUtils.isBlank(key)) {
      resp.getWriter().write(GSON.toJson(getLocks()));
    } else {
      resp.getWriter().write(GSON.toJson(lockService.isLockPresent(key)));
    }
    resp.flushBuffer();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("POST: " + req.toString());
    }
    resp.setContentType(APPLICATION_JSON_CONTENT_TYPE);
    String value = req.getParameter(VALUE_PARAM);
    String key = getKey(req);
    if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
      resp.setStatus(404);
    } else if (lockService.trySetLock(key, value)) {
      resp.setStatus(200);
    } else {
      resp.setStatus(409);
    }
    resp.flushBuffer();
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("PUT: " + req.toString());
    }
    resp.setContentType(APPLICATION_JSON_CONTENT_TYPE);
    String value = req.getParameter(VALUE_PARAM);
    String key = getKey(req);
    if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
      resp.setStatus(404);
    } else {
      lockService.setLock(key, value);
    }
    resp.flushBuffer();
  }

  @Override
  public void init(ServletConfig config) throws ServletException {
    LOGGER.info("Initializing " + config.getServletName());
  }

  private String getLocks() {
    return lockService.getAllLocks().toString();
  }


  private String getKey(HttpServletRequest req) {
    return StringUtils.substringAfter(req.getRequestURI(), Helper.getLocksPath())
        .replace(Helper.PATH_SEPARATOR, "");
  }

  @Activate
  public void start() {
    LOGGER.debug("Registering servlet at " + Helper.getLocksPath());
    try {
      httpService.registerServlet(Helper.getLocksPath(), this, null,
          null);
    } catch (Exception e) {
      LOGGER.error("Failed to register servlet at " + Helper.getLocksPath(), e);
    }
  }

  @Deactivate
  public void stop() {
    httpService.unregister(Helper.getLocksPath());
    httpService = null;
  }

}
