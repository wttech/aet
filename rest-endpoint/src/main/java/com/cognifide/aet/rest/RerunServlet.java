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
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.StorageException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.HttpURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Component(label = "Rerun Servlet", immediate = true)
public class RerunServlet extends BasicDataServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(RerunServlet.class);
  private static final Gson PRETTY_PRINT_GSON = new GsonBuilder().setPrettyPrinting().create();

  @Override
  protected void process(DBKey dbKey, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String result = PRETTY_PRINT_GSON.toJson("dupa");
    response.setContentType("application/json");
    response.getWriter().write(result);

  }

  @Activate
  public void start(){
    register(Helper.getRerunPath());
  }

  @Deactivate
  public void stop(){
    unregister(Helper.getRerunPath());
  }
}
