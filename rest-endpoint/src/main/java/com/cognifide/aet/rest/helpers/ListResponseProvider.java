/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.rest.helpers;

import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.StorageException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListResponseProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListResponseProvider.class);

    private static final String COMPANY_PARAM = "company";

    private static final String PROJECT_PARAM = "project";

    private static final String NAME_PARAM = "name";

    private final MetadataDAO metadataDAO;

    private final String reportDomain;

    private final FreeMarkerConfigurationManager templateConfiguration;

    public ListResponseProvider(MetadataDAO metadataDAO, String reportDomain, FreeMarkerConfigurationManager templateConfiguration) {
        this.metadataDAO = metadataDAO;
        this.reportDomain = reportDomain;
        this.templateConfiguration = templateConfiguration;
    }

    public void processVersionListRequest(HttpServletRequest req, HttpServletResponse resp) {

        String company = req.getParameter(COMPANY_PARAM);
        String project = req.getParameter(PROJECT_PARAM);
        String name = req.getParameter(NAME_PARAM);

        try {
            List data = createData(company, project, name);
            if (!data.isEmpty() || (StringUtils.isEmpty(company)
                    && StringUtils.isEmpty(project) && StringUtils.isEmpty(name)) ) {
                Map root = new HashMap();
                root.put("data", data);
                root.put("size", data.size());
                root.put("reportDomain", reportDomain);
                root.put(PROJECT_PARAM, project);
                root.put(COMPANY_PARAM, company);
                root.put(NAME_PARAM, name);

                Template template = templateConfiguration.getConfiguration()
                        .getTemplate(chooseTemplate(company, project, name));

                template.process(root, resp.getWriter());
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (StorageException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("Database error", e);
        } catch (TemplateException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("error while getting template for request: '{}' and response: '{}'", req,
                    resp, e);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("error while getting response writer for request: '{}' and response: '{}'", req,
                    resp, e);
        }
    }


    private List createData(String company, String project, String name) throws StorageException {
        if (StringUtils.isNotEmpty(company) && StringUtils.isNotEmpty(project) && StringUtils.isNotEmpty(name)) {
            return metadataDAO.listSuiteVersions(new SimpleDBKey(company, project), name);
        } else if (StringUtils.isNotEmpty(company) && StringUtils.isNotEmpty(project)) {
            return metadataDAO.listDistinctSuitesNames(new SimpleDBKey(company, project));
        } else {
            return new ArrayList(metadataDAO.getProjects(null));
        }
    }

    private String chooseTemplate(String company, String project, String name) {
        if (company != null && project != null && name != null) {
            return "versionList.ftl";
        } else if (company != null && project != null) {
            return "suiteList.ftl";
        } else{
            return "projectList.ftl";
        }
    }
}
