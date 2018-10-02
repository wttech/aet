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
package com.cognifide.aet.job.common.comparators.w3chtml5.wrapper;

import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import nu.validator.json.Serializer;
import nu.validator.messages.JsonMessageEmitter;
import nu.validator.messages.MessageEmitterAdapter;
import nu.validator.servlet.imagereview.ImageCollector;
import nu.validator.source.SourceCode;
import nu.validator.validation.SimpleDocumentValidator;
import nu.validator.xml.SystemErrErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class NuValidatorWrapper {

  private static final String SCHEMA_URL = "http://s.validator.nu/html5-rdfalite.rnc";

  private SimpleDocumentValidator validator;

  private MessageEmitterAdapter errorHandler;

  private ByteArrayOutputStream out;

  private boolean errorsOnly;

  public String validate(InputStream sourceStream) throws ProcessingException {
    try {
      setupAndValidate(sourceStream);
      return out.toString(StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new ProcessingException("Exception while validating!", e);
    }
  }

  private void setupAndValidate(InputStream sourceStream) throws ProcessingException {
    System.setProperty("nu.validator.datatype.warn", "true");
    out = new ByteArrayOutputStream();
    validator = createValidatorWithDisabledLanguageDetection();
    try {
      setup();
      validator.checkHtmlInputSource(new InputSource(sourceStream));
      end();
    } catch (SAXException | IOException e) {
      throw new ProcessingException(e.getMessage(), e);
    }
  }

  private SimpleDocumentValidator createValidatorWithDisabledLanguageDetection() {
    boolean initializeLog4j = true;
    boolean logUrls = true;
    boolean enableLanguageDetection = false;
    return new SimpleDocumentValidator(initializeLog4j, logUrls, enableLanguageDetection);
  }

  private void setup() throws ProcessingException, SAXException {
    setErrorHandler();
    errorHandler.setHtml(true);
    errorHandler.start(null);
    try {
      validator.setUpMainSchema(SCHEMA_URL, new SystemErrErrorHandler());
    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    }
    validator.setUpValidatorAndParsers(errorHandler, false, false);
  }

  private void end() throws SAXException {
    errorHandler
        .end("Document checking completed. No errors found.", "Document checking completed.", null);
  }

  private void setErrorHandler() {
    SourceCode sourceCode = validator.getSourceCode();
    ImageCollector imageCollector = new ImageCollector(sourceCode);

    errorHandler = new MessageEmitterAdapter(null, sourceCode, false, imageCollector, 0, true, new
        JsonMessageEmitter(new Serializer(out), null));
    errorHandler.setErrorsOnly(errorsOnly);
  }

  public void setErrorsOnly(boolean errorsOnly) {
    this.errorsOnly = errorsOnly;
  }

}
