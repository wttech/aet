'use strict';

module.exports = (function WindowHelper() {

  var api = {};

  /**
   * Swtiches browser context to the window, which is specified by given widowNumber.
   * Browser windows order is dependent from the widow opening order
   * @param windowNumber window number to which context should be switched to
   */
  api.switchWindowTo = function (windowNumber) {
    return browser.getAllWindowHandles().then(function (handles) {
      return browser.switchTo().window(handles[windowNumber]);
    });
  };

  return api;
})();
