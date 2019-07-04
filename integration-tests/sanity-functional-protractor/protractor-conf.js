'use strict';

var HtmlScreenshotReporter = require('protractor-jasmine2-screenshot-reporter');
var SpecReporter = require('jasmine-spec-reporter').SpecReporter;
var JasmineReporter = require('jasmine-reporters');


var htmlReporter = new HtmlScreenshotReporter({
    dest: 'reports',
    cleanDestination: true,
    showSummary: true,
    showQuickLinks: true,
    reportTitle: 'Functional Tests Report',
    showConfiguration: false
});
var specReporter = new SpecReporter();
var jasmineReporter = new JasmineReporter.JUnitXmlReporter({
    consolidateAll: false,
    savePath: 'reports/junit'
});

exports.config = {
    framework: 'jasmine2',
    capabilities: {
        browserName: 'chrome',
        shardTestFiles: true,
        maxInstances: 2
    },

    // Spec patterns are relative to the location of the spec file. They may
    // include glob patterns.
    suites: {
        reportTests: [
            'specs/main-test-spec.js',
            'specs/filtering-feature-spec.js'
        ]
    },

    allScriptsTimeout: 1800000,
    directConnect: true,
    // Options to be passed to Jasmine-node.
    jasmineNodeOpts: {
        showColors: true,
        isVerbose: true,
        defaultTimeoutInterval: 45000,
        includeStackTrace: true,
    },

    // Setup the report before any tests start
    beforeLaunch: function () {
        return new Promise(function (resolve) {
            htmlReporter.beforeLaunch(resolve);
        });
    },
    // Close the report after all tests finish
    afterLaunch: function (exitCode) {
        return new Promise(function (resolve) {
            htmlReporter.afterLaunch(resolve.bind(this, exitCode));
        });
    },
    // // Assign the test reporter to each running instance
    onPrepare: function () {
        var env = jasmine.getEnv();
        env.addReporter(htmlReporter);
        env.addReporter(specReporter);
        env.addReporter(jasmineReporter);

        browser.manage().timeouts().pageLoadTimeout(45000);
        browser.driver.manage().window().maximize();
        browser.ignoreSynchronization = true;
    },

    params: {
        env: 'local'
    }

};
