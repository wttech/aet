package com.cognifide.aet.rest;

import com.cognifide.aet.communication.api.metadata.*;
import com.cognifide.aet.vs.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.cognifide.aet.rest.Helper.isValidCorrelationId;
import static com.cognifide.aet.rest.Helper.responseAsJson;

@Component(immediate = true)
public class ErrorsServlet extends BasicDataServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorsServlet.class);

    @Reference
    private MetadataDAO metadataDAO;
    @Reference
    private ArtifactsDAO artifactsDAO;

    @Reference
    private transient HttpService httpService;

    @Override
    protected void process(DBKey dbKey, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String correlationId = req.getParameter(Helper.CORRELATION_ID_PARAM);
        String testName = req.getParameter(Helper.TEST_RERUN_PARAM);

        Suite suite;
        try {
            if (isValidCorrelationId(correlationId)) {
                suite = metadataDAO.getSuite(dbKey, correlationId);
            } else {
                resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                resp.getWriter()
                    .write(responseAsJson(GSON, "Invalid correlationId of suite was specified."));
                return;
            }
        } catch (StorageException e) {
            LOGGER.error("Failed to get suite", e);
            resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
            resp.getWriter().write(responseAsJson(GSON, "Failed to get suite %s", e.getMessage()));
            return;
        }

        if (suite != null) {
            Optional<Test> test = suite.getTests().stream()
                .filter(t -> t.getName().equals(testName)).findFirst();
            if (test.isPresent()) {
                List<Artifact> artifacts = new ArrayList<>();
                String errorType = Helper.getErrorTypeFromRequest(req);
                for(Url url : test.get().getUrls()) {
                    if(errorType != null) {
                        Optional<Step> step = url.getSteps().stream()
                            .filter(s -> s.getName().equals(errorType)).findFirst();
                        if(!step.isPresent())
                            continue;
                        processStep(step.get(), dbKey, artifacts);
                    } else {
                        for(Step step : url.getSteps())
                            processStep(step, dbKey, artifacts);
                    }
                }

                resp.setContentType(Helper.APPLICATION_JSON_CONTENT_TYPE);
                resp.getWriter().write(GSON.toJson(artifacts));
            } else
                createNotFoundTestResponse(resp, testName, dbKey);
        } else
            createNotFoundSuiteResponse(resp, correlationId, dbKey);
    }

    private void processStep(Step step, DBKey dbKey, List<Artifact> artifacts) {
        for(Comparator comparator : step.getComparators()) {
            artifacts.add(artifactsDAO.getArtifact(dbKey, comparator.getStepResult().getArtifactId()));
        }
    }

    private void createNotFoundTestResponse(HttpServletResponse response, String testName, DBKey dbKey)
        throws IOException {
        response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        response.setContentType(Helper.APPLICATION_JSON_CONTENT_TYPE);
        response.getWriter().write(
            responseAsJson(GSON, "Unable to get test with name: %s for %s", testName, dbKey.toString())
        );
    }

    private void createNotFoundSuiteResponse(HttpServletResponse response, String correlationId,
                                             DBKey dbKey) throws IOException {
        response.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
        response.setContentType(Helper.APPLICATION_JSON_CONTENT_TYPE);
        response.getWriter().write(
            responseAsJson(GSON, "Unable to get Suite Metadata with correlationId: %s for %s",
                correlationId, dbKey.toString())
        );
    }

    @Override
    protected HttpService getHttpService() {
        return httpService;
    }

    @Override
    protected void setHttpService(HttpService httpService) {
        this.httpService = httpService;
    }

    @Activate
    public void start() {
        register(Helper.getErrorsPath());
    }

    @Deactivate
    public void stop() {
        unregister(Helper.getErrorsPath());
    }
}
