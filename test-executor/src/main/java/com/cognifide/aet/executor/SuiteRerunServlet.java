package com.cognifide.aet.executor;

import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.SimpleDBKey;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.CharEncoding;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpStatus;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Component(label = "SuiteRerunServlet", description = "Executes received test", immediate = true)
public class SuiteRerunServlet extends HttpServlet {

  private static final long serialVersionUID = -4708227978736783811L;

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteRerunServlet.class);
  private static final String SERVLET_PATH = "/suite-rerun";
  private static final String SUITE_PARAM = "suite";
  private static final String NAME_PARAM = "name";
  private static final String TEST_PARAM = "rerun";
  private static final String COMPANY_PARAM = "company";
  private static final String PROJECT_PARAM = "project";
  private static final String VERSION_PARAM = "version";

  @Reference
  private HttpService httpService;

  @Reference
  private SuiteExecutor suiteExecutor;

  @Reference
  private MetadataDAO metadataDAO;


  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    //ToDo
    if (ServletFileUpload.isMultipartContent(request)) {
      Map<String, String> requestData = getRequestData(request);
      final String suite = requestData.get(SUITE_PARAM);
      final String company = requestData.get(COMPANY_PARAM);
      final String project = requestData.get(PROJECT_PARAM);
      final String version = requestData.get(VERSION_PARAM);

      SimpleDBKey simpleDBKey = new SimpleDBKey(company, project);
      try{
        metadataDAO.listSuites(simpleDBKey)
      }catch(Exception e){
        // ToDo
      }

//      if (StringUtils.isNotBlank(suite)) {
//        HttpSuiteExecutionResultWrapper resultWrapper = suiteExecutor
//            .execute(suite, name, domain, null, null);
//        final SuiteExecutionResult suiteExecutionResult = resultWrapper.getExecutionResult();
//        Gson gson = new Gson();
//
//        String responseBody = gson.toJson(suiteExecutionResult);
//
//        if (resultWrapper.hasError()) {
//          response.sendError(resultWrapper.getStatusCode(),
//              suiteExecutionResult.getErrorMessage());
//        } else {
//          response.setStatus(HttpStatus.SC_OK);
//          response.setContentType("application/json");
//          response.setCharacterEncoding(CharEncoding.UTF_8);
//          response.getWriter().write(responseBody);
//        }
//      } else {
//        response.sendError(HttpStatus.SC_BAD_REQUEST, "Request does not contain the test suite");
//      }
    } else {
      response.sendError(HttpStatus.SC_BAD_REQUEST, "Request content is incorrect");
    }
  }

  @Activate
  public void start() {
    try {
      httpService.registerServlet(SERVLET_PATH, this, null, null);
    } catch (ServletException | NamespaceException e) {
      LOGGER.error("Failed to register servlet at " + SERVLET_PATH, e);
    }
  }

  @Deactivate
  public void stop() {
    httpService.unregister(SERVLET_PATH);
    httpService = null;
  }

  private Map<String, String> getRequestData(HttpServletRequest request) {
    Map<String, String> requestData = new HashMap<>();

    ServletFileUpload upload = new ServletFileUpload();
    try {
      FileItemIterator itemIterator = upload.getItemIterator(request);
      while (itemIterator.hasNext()) {
        FileItemStream item = itemIterator.next();
        InputStream itemStream = item.openStream();
        String value = Streams.asString(itemStream, CharEncoding.UTF_8);
        requestData.put(item.getFieldName(), value);
      }
    } catch (FileUploadException | IOException e) {
      LOGGER.error("Failed to process request", e);
    }

    return requestData;
  }
}
