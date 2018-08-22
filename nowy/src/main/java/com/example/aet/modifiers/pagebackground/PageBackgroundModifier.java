package com.example.aet.modifiers.pagebackground;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import java.util.Map;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageBackgroundModifier implements CollectorJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(PageBackgroundModifier.class);

  static final String NAME = "change-background";

  private static final String COLOR_PARAM = "color";
  private static final String EXAMPLE_CHANGE_BACKGROUND_MODIFICATION = "document.body.style.background = '%s';";

  private final WebDriver webDriver;
  private String color;

  PageBackgroundModifier(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  @Override
  public CollectorStepResult collect() throws ProcessingException {
    CollectorStepResult result;
    try {
      final String command = String.format(EXAMPLE_CHANGE_BACKGROUND_MODIFICATION, color);
      ((JavascriptExecutor) webDriver).executeScript(command);
      result = CollectorStepResult.newModifierResult();
    } catch (Exception e) {
      final String message = String
          .format("Can't execute JavaScript command. Error: %s ", e.getMessage());
      result = CollectorStepResult.newProcessingErrorResult(message);
      LOGGER.warn(message, e);
    }
    return result;
  }

  @Override
  public void setParameters(Map<String, String> params) throws ParametersException {
    if (!params.containsKey(COLOR_PARAM)) {
      throw new ParametersException(
          "'color' parameter must be provided for element modifier.");
    }
    color = params.get(COLOR_PARAM);
  }
}