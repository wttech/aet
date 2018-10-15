package com.cognifide.aet.job.common.utils.javaScript;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaScriptJobExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(JavaScriptJobExecutor.class);

  private final JavascriptExecutor executor;
  private final String currentUrl;

  public JavaScriptJobExecutor(WebDriver webDriver) {
    executor = (JavascriptExecutor) webDriver;
    currentUrl = webDriver.getCurrentUrl();
  }

  public JavaScriptJobResult execute(String jsSnippet, Object... elements) {
    JavaScriptJobResult result;
    try {
      Object jsResult = executeJs(jsSnippet);
      result = new JavaScriptJobResult(CollectorStepResult.newModifierResult(), jsResult);
    } catch (Exception ex) {
      String message = handleJsException(jsSnippet, ex);
      result = new JavaScriptJobResult(CollectorStepResult.newProcessingErrorResult(message), null);
    }
    return result;
  }

  private Object executeJs(String jsSnippet) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Executing JavaScript command: {} on page: {}", jsSnippet, currentUrl);
    }
    return executor.executeScript(jsSnippet);

  }

  private String handleJsException(String jsSnippet, Exception ex) {
    String message = String
        .format("Can't execute JavaScript command. jsSnippet: \"%s\". Error: %s ",
            jsSnippet, ex.getMessage());
    LOGGER.warn(message, ex);
    return message;
  }

}
