'use strict';

module.exports = (function test() {
  var api = {};

  api.setupEventListener = function(callback)
  {

    window.addEventListener("beforeunload", () => {
      callback('Page has been unloaded')
      return true;
    })
    setTimeout(
      () => {
        callback(false)
      }, 5000)
  };

  api.eventCallback = function(state)
  {
    console.log("Page has been unloaded or not", state)
  };

  return api;
})();
