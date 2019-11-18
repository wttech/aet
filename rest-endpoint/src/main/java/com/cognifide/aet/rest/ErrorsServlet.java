package com.cognifide.aet.rest;

import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.DBKey;
import com.cognifide.aet.vs.MetadataDAO;
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
