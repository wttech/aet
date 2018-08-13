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

import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Component(label = "HistoryServlet", description = "Returns Suite's History", immediate = true)
public class HistoryServlet extends BasicDataServlet{

  private static final long serialVersionUID = 1774115103933538112L;

  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryServlet.class);

  private static final Gson PRETTY_PRINT_GSON = new GsonBuilder().setPrettyPrinting().create();

  @Reference
  private MetadataDAO metadataDAO;

  @Override
  protected void process(DBKey dbKey, HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String suiteName = req.getParameter(Helper.SUITE_PARAM);
    resp.setCharacterEncoding("UTF-8");
    List<String> correlationIds = null;
    try {
      if (isValidName(suiteName)) {
        correlationIds = metadataDAO.listCorrelationIds(dbKey, suiteName);
      }
    } catch (StorageException e) {
      LOGGER.error("Failed to get suite's history", e);
      resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
      resp.getWriter().write(responseAsJson("Failed to get history of suite: %s", e.getMessage()));
      return;
    }

    if (correlationIds != null && correlationIds.size() > 0) {
      JsonElement correlationIdsJson = PRETTY_PRINT_GSON.toJsonTree(correlationIds);
      String result = PRETTY_PRINT_GSON.toJson(correlationIdsJson.getAsJsonArray());
      resp.setContentType("application/json");
      resp.getWriter().write(result);
    } else {
      resp.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
      resp.getWriter()
          .write(responseAsJson("History not found for suite: %s %s", suiteName, dbKey.toString()));
    }
  }

  @Activate
  public void start(){
    register(Helper.getHistoryPath());
  }

  @Deactivate
  public void stop(){
    unregister(Helper.getHistoryPath());
  }
}
