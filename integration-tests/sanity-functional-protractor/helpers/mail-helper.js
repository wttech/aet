'use strict';
var notifier = require('mail-notifier');

module.exports = (function() {

    var initialized = false,
        uniqueStrings = [],
        api = {},
        credentials;

    function onEnd() {
        n.start();
    }

    function onMail(mail) {
        console.log('Email for: ' + credentials.user + ', has been received: ' + mail.subject);
        uniqueStrings.forEach(function(entry, index) {
            if (mail.html.indexOf(entry.uniqueString) >= 0) {
                uniqueStrings.splice(index, 1);
                entry.deferred.fulfill(mail);
            }
        });
    }

    api.initialize = function(imapCredentials) {
        if (!initialized) {
            credentials = imapCredentials;
            console.log("Mail listener initialised for " + credentials.user);
            initialized = true;
            notifier(imapCredentials)
                .on('end', onEnd)
                .on('mail', onMail)
                .start();
        }
        return this;
    };

    api.listenForEmail = function(uniqueString) {
        var deferred = protractor.promise.defer();
        uniqueStrings.push({
            uniqueString: uniqueString,
            deferred: deferred
        });
        return deferred.promise;
    };

    return api;
})();
