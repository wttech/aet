'use strict';

function waitHelper() {

    var until = protractor.ExpectedConditions;

    this.timeouts = {
        small: 5000,
        medium: 10000,
        long: 30000,
        veryLong: 60000
    };

    /**
     * Waits until text is present in element value
     * @param element - DOM element
     * @param text - text to wait for
     * @param name - human readable name of the element
     * @param timeout - you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns promise
     */
    this.forTextToBePresentInElement = (element, text, name, timeout) => {
        return wait(until.textToBePresentInElementValue(element, text), name, 'was not clickable', timeout);
    };

    /**
     * Waits until DOM element is clickable
     * @param element - DOM element
     * @param name - human readable name of the element
     * @param timeout - you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns promise
     */
    this.forElementToBeClickable = (element, name, timeout) => {
        return wait(until.elementToBeClickable(element), name, 'was not clickable', timeout);
    };

    /**
     * Waits until DOM element is present
     * @param element - DOM element
     * @param name - human readable name of the element
     * @param timeout - you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns promise
     */
    this.forElementToBePresent = (element, name, timeout) => {
        return wait(until.presenceOf(element), name, 'was not present', timeout);
    };

    /**
     * Wait for new browser window
     * @param numOfWindows - number of browser windows which should be opened
     * @param timeout - you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns promise
     */
    this.forWindowToBeOpened = (numOfWindows, timeout) => {
        return wait(() =>
                browser.getAllWindowHandles().then((winHandles) =>
                    winHandles.length > (numOfWindows - 1)),
            'new browser', 'was not opened', timeout);
    };

    /**
     * Waits until DOM element is visible
     * @param element - DOM element
     * @param name - human readable name of the element
     * @param timeout - you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns promise
     */
    this.forElementToBeVisible = (element, name, timeout) => {
        return wait(until.visibilityOf(element), name, 'was not visible', timeout);
    };

    /**
     * Waits until DOM element is invisible
     * @param element - DOM element
     * @param name - human readable name of the element
     * @param timeout - you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns promise
     */
    this.forElementToBeInvisible = (element, name, timeout) => {
        return wait(until.invisibilityOf(element), name, 'was not invisible', timeout);
    };

    /**
     * Waits until browser loads the page of provided title
     * @param pageTitle page title
     * @param timeout you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns {boolean} true when page has been loaded properly, otherwise false
     */
    this.forPageToLoad = (pageTitle, timeout) => {
        return wait(until.titleIs(pageTitle), timeout);
    };

    /**
     * Waits until browser loads the asset of provided src
     * @param assetTitle asset title
     * @param timeout you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns {boolean} true when page has been loaded properly, otherwise false
     */
    this.forAssetToLoad = (assetTitle, assetSrc, timeout, type) => {
        const assetElement = element(by.css(type + '[src*="' + assetSrc + '"]'));
        return wait(() => this.forElementToBePresent(assetElement, assetTitle, timeout));
    };

    const constructErrorMessage = (name, timeout, toBeAction) => {
        return 'Element "' + name + '" ' + toBeAction + ' within ' + (timeout / 1000) + 's';
    };

    const wait = (condition, name, toBeAction, timeout) => {
        var defaultTimeout = this.timeouts[timeout] || timeout || this.timeouts.medium,
            msg = constructErrorMessage(name, defaultTimeout, toBeAction);
        return browser.wait(condition, defaultTimeout, msg);
    };
}

module.exports = new waitHelper();
