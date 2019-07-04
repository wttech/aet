var SpecReporter = require('jasmine-spec-reporter').SpecReporter;
var JasmineReporter = require('jasmine-reporters');
var HtmlReporter = require('jasmine-pretty-html-reporter').Reporter;

var specReporter = new SpecReporter();

var jasmineReporter = new JasmineReporter.JUnitXmlReporter({
    consolidateAll: false,
    savePath: 'reports/junit'
});

var htmlReporter = new HtmlReporter({
    path: 'reports'
});

var env = jasmine.getEnv();
env.addReporter(htmlReporter);
env.addReporter(specReporter);
env.addReporter(jasmineReporter);