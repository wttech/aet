import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class WaitForImageModifier implements CollectorJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitForImageModifier.class);

    static final String NAME = "wait-for-image";

    private final WebDriver webDriver;

    private static final String CSS_PARAMETER = "css";
    private static final String XPATH_PARAMETER = "xpath";
    private static final String TIMEOUT_PARAMETER = "timeout";

    private String webElementCssLocator;
    private String webElementXpathLocator;
    private int timeoutInMilliseconds;

    WaitForImageModifier(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public CollectorStepResult collect() throws ProcessingException {
        CollectorStepResult result;
        final By locator;
        final WebElement webElement;
        try {
            FluentWait<WebDriver> wait = new FluentWait<>(webDriver).withTimeout(timeoutInMilliseconds,
                    TimeUnit.MILLISECONDS).pollingEvery(500, TimeUnit.MILLISECONDS)
                    .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
            if (webElementCssLocator != null) {
                locator = By.cssSelector(webElementCssLocator);
            } else {
                locator = By.xpath(webElementXpathLocator);
            }
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver webDriver) {
                    return (Boolean) ((JavascriptExecutor) webDriver).executeScript("return arguments[0].complete",
                            webDriver.findElement(locator));
                }
            });
            result = CollectorStepResult.newModifierResult();

        } catch (Exception e) {
            final String message =
                    String.format("Failed to wait for image to be loaded with provided locator. Error: %s",
                            e.getMessage());
            result = CollectorStepResult.newProcessingErrorResult(message);
            LOGGER.warn(message, e);

        }
        return result;
    }

    @Override
    public void setParameters(Map<String, String> parameters) throws ParametersException {
        if (parameters.containsKey(CSS_PARAMETER)) {
            webElementCssLocator = parameters.get(CSS_PARAMETER);
        } else if (parameters.containsKey(XPATH_PARAMETER)) {
            webElementXpathLocator = parameters.get(XPATH_PARAMETER);
        } else {
            throw new ParametersException("Xpath or CSS locator has to be provided for wait-for-image modifier.");
        }

        if (!parameters.containsKey(TIMEOUT_PARAMETER)) {
            throw new ParametersException("Timeout has to be provided for wait-for-image modifier.");
        }
        timeoutInMilliseconds = Integer.parseInt(parameters.get(TIMEOUT_PARAMETER));
    }
}
