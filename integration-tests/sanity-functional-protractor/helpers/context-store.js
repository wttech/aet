'use strict';

/**
 * Context store is a simple key value storage that is persistent for duration of test.
 * Should be used whenever you would like to store some data and use it later in your journey.
 * This class allows developers i.e. to get rid of tightly coupled page objects. Instead of referencing previus page of
 * the journey you should add value to context store.
 */
function ContextStore() {

    var store = {};
    /**
     * Puts value into key-value store
     * @param key
     * @param value
     */
    this.put = function (key, value) {
        store[key] = value;
    };

    /**
     * Gets value from key-value store
     * @param key
     * @returns {*}
     */
    this.get = function (key) {
        return store[key];
    };
}

module.exports = new ContextStore();