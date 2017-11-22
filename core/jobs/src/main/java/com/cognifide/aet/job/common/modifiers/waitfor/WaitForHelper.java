package com.cognifide.aet.job.common.modifiers.waitfor;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class WaitForHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitForHelper.class);

    public static By determineLocatorStrategy(String webElementCssLocator, String webElementXpathLocator) {
        By locator;
        if (webElementCssLocator != null) {
            locator = By.cssSelector(webElementCssLocator);
        } else {
            locator = By.xpath(webElementXpathLocator);
        }
        return locator;
    }

    public static CollectorStepResult waitForExpectedCondition(
            WebDriver webDriver,
            int timeoutInSeconds,
            ExpectedCondition expectedCondition) {
        CollectorStepResult result;
        FluentWait<WebDriver> wait = new FluentWait<>(webDriver).withTimeout(timeoutInSeconds,
                TimeUnit.SECONDS).pollingEvery(500, TimeUnit.MILLISECONDS)
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

        wait.until(expectedCondition);
        result = CollectorStepResult.newModifierResult();
        return result;
    }
}
