package com.cognifide.aet.job.common.modifiers.waitfor;

import com.cognifide.aet.communication.api.metadata.CollectorStepResult;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class WaitForHelper {

    public static CollectorStepResult waitForExpectedCondition(
            WebDriver webDriver,
            long timeoutInSeconds,
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
