'use strict';

module.exports = (function pdjWait() {
    var api = {},
        until = protractor.ExpectedConditions;

    api.timeouts = {
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
    api.forTextToBePresentInElement = function(element, text, name, timeout) {
        return wait(until.textToBePresentInElementValue(element, text), name, 'was not clickable', timeout);
    };

    /**
     * Waits until DOM element is clickable
     * @param element - DOM element
     * @param name - human readable name of the element
     * @param timeout - you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns promise
     */
    api.forElementToBeClickable = function(element, name, timeout) {
        return wait(until.elementToBeClickable(element), name, 'was not clickable', timeout);
    };

    /**
     * Waits until DOM element is present
     * @param element - DOM element
     * @param name - human readable name of the element
     * @param timeout - you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns promise
     */
    api.forElementToBePresent = function(element, name, timeout) {
        return wait(until.presenceOf(element), name, 'was not present', timeout);
    };

    /**
     * Wait for new browser window
     * @param numOfWindows - number of browser windows which should be opened
     * @param timeout - you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns promise
     */
    api.forWindowToBeOpened = function(numOfWindows, timeout) {
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
    api.forElementToBeVisible = function(element, name, timeout) {
        return wait(until.visibilityOf(element), name, 'was not visible', timeout);
    };

    /**
     * Waits until DOM element is invisible
     * @param element - DOM element
     * @param name - human readable name of the element
     * @param timeout - you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
     * @returns promise
     */
    api.forElementToBeInvisible = function(element, name, timeout) {
        return wait(until.invisibilityOf(element), name, 'was not invisible', timeout);
    };

  /**
   * Waits until browser loads the page of provided title
   * @param pageTitle page title
   * @param timeout you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
   * @returns {boolean} true when page has been loaded properly, otherwise false
   */
    api.forPageToLoad = function(pageTitle, timeout) {
      return wait(() =>
          browser.getTitle().then((title) =>
            title === pageTitle),
        pageTitle, 'page was not loaded', timeout);
    };

  /**
   * Waits until browser loads the asset of provided src
   * @param assetTitle asset title
   * @param timeout you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
   * @returns {boolean} true when page has been loaded properly, otherwise false
   */
    api.forAssetToLoad = function(assetTitle, assetSrc, timeout, type) {
      const assetElement = element(by.css(type + '[src*="' + assetSrc + '"]'));
      return wait(() => api.forElementToBePresent(assetElement, assetTitle, timeout));
    };

    function constructErrorMessage(name, timeout, toBeAction) {
        return 'Element "' + name + '" ' + toBeAction + ' within ' + (timeout / 1000) + 's';
    }

    function wait(condition, name, toBeAction, timeout) {
        var defaultTimeout = api.timeouts[timeout] || timeout || api.timeouts.medium,
            msg = constructErrorMessage(name, defaultTimeout, toBeAction);
        return browser.wait(condition, defaultTimeout, msg);
    }

    return api;
})();
