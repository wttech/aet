'use strict';

const waitHelper = require("./wait-helper.js");

module.exports = (function inputHelper() {
  var api = {};

  /**
   * Waits for element to be visible. After it is element gets cleared and filled with text provided
   * @param textInputElement element that should be filled with given text
   * @param text string that should be typed in passed element, empty string used if undefined passed here
   * @param timeout you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
   * @param elementName human readable name of the element
   */
  api.fillTextInput = (textInputElement, text, elementName = 'Input element', timeout = 'medium') => {
    waitHelper.forElementToBeVisible(textInputElement, elementName, timeout)
      .then(() => {
        textInputElement.clear()
          .then(() => {
            textInputElement.sendKeys(text || '');
          });
      });
  };

  /**
   * Waits for element to be visible and cliks on it.
   * @param clickableElement element that should be clicked
   * @param elementName human readable name of the element
   * @param timeout you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
   */
  api.click = (clickableElement, elementName = 'Button element', timeout = 'medium') => {
    waitHelper.forElementToBeClickable(clickableElement, elementName, timeout)
      .then(() => {
        clickableElement.click();
      });
  };

  /**
   * Changes state of checkbox to given value. If value passe is equal to true then checkbox will be checked.
   * If value passed is not true then checkbox will be unchecked.
   * @param checkbox element to be filled as checked / unchecked
   * @param value boolean, true if checkbox should be selected, false otherwise
   * @param elementName human readable name of the element
   * @param timeout you can provide integer value (miliseconds), use one of the predefined values ('small', 'medium', 'long'). Defaults to 'medium' timeout equal to 10 seconds.
   */
  api.setCheckboxValue = (checkbox, value, elementName = 'Button element', timeout = 'medium') => {
    waitHelper.forElementToBeClickable(checkbox, elementName, timeout)
      .then(() => {
        checkbox
          .isSelected()
          .then(isSelected => {
            console.log('Is selected:' + isSelected);
            if (isSelected !== value) {
              checkbox.click();
            }
          });
      });
  };

  return api;
})();
