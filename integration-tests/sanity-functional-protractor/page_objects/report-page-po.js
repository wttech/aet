'use strict';

const waitHelper = require('../helpers/wait-helper');

function ReportPage() {

    this.elements = {
        searchInput: element(by.xpath("//input[@placeholder='Search...']")),
        statistics: element(by.css(".pull-right.stats")),
        testLinks: element.all(by.css("a.test-name.is-visible")),
        passedTestLinks: element.all(by.css("a.test-name.passed.is-visible")),
        conditionalyPassedTestLinks: element.all(by.css("a.test-name.conditionallyPassed.is-visible")),
        failedTestLinks: element.all(by.css("a.test-name.failed.is-visible")),
        warnedTestLinks: element.all(by.css("a.test-name.warning.is-visible"))
    };

    this.fillInSearchText = (phrase) => {
        return waitHelper.forElementToBeVisible(this.elements.searchInput, 'Search Input', 'medium')
            .then(() => this.elements.searchInput.clear())
            .then(() => this.elements.searchInput.sendKeys(phrase));
    };

    this.getStatistics = () => {
        return this.elements.statistics.getText();
    };

    this.getCurrentNoOfTests = () => {
        return this.elements.testLinks.count();
    };

    this.getCurrentNoOfPassedTests = () => {
        return this.elements.passedTestLinks.count();
    };

    this.getCurrentNoOfConditionalyPassedTests = () => {
        return this.elements.conditionalyPassedTestLinks.count();
    };

    this.getCurrentNoOfFailedTests = () => {
        return this.elements.failedTestLinks.count();
    };

    this.getCurrentNoOfWarnedTests = () => {
        return this.elements.warnedTestLinks.count();
    };

    this.getTestLinksTitles = () => {
        return waitHelper.forElementToBeVisible(this.elements.testLinks.last(), 'Test Links', 'medium')
            .then(() => {
                const linksTitles = [];
                this.elements.testLinks.each(testLinks => {
                    testLinks.getText().then((title) => {
                        linksTitles.push(title);
                    });
                });
                return linksTitles;
            })
    }
}

module.exports = new ReportPage();
