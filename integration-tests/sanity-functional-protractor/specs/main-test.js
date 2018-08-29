'use strict';

//npm test -- --specs specs/main-test.js

const PublishPage = require('../page_objects/abstract-po.js');
const ReportPage = require('../page_objects/report-page-po.js');
let expectedSuccess, expectedFailed, expectedWarnings, expectedTotal;
let currentNoOfPassedTests, currentNoOfConditionalyPassedTests, currentNoOfFailedTests, currentNoOfWarnedTests;

describe('AET reports web application - main report results validation', () => {

    beforeAll((done) => {
        PublishPage.open("reportHome");
        this.countExpectedTestsResults()
            .then(() => this.countActualTestsResults())
            .then(() => done());
    });

    it('Report results validation: passed and conditionaly passed tests', () => {
        expect(currentNoOfPassedTests + currentNoOfConditionalyPassedTests).toEqual(expectedSuccess);
    });

    it('Report results validation: warned tests', () => {
        expect(currentNoOfWarnedTests).toEqual(expectedWarnings);
    });

    it('Report results validation: failed test', () => {
        expect(currentNoOfFailedTests).toEqual(expectedFailed);
    });

    it('Report results validation: total no. of tests', () => {
        expect(currentNoOfFailedTests + currentNoOfWarnedTests + currentNoOfPassedTests + currentNoOfConditionalyPassedTests).toEqual(expectedTotal);
    });
});

this.countExpectedTestsResults = () => {
    return ReportPage.getTestLinksTitles().then((linksTitles) => {
        let linksFirstLetters = [];
        linksTitles.forEach(title => {
            linksFirstLetters.push(title.slice(0, 1));
        });
        expectedTotal = linksFirstLetters.length;
        expectedSuccess = linksFirstLetters.filter((x) => x === "S").length;
        expectedFailed = linksFirstLetters.filter((x) => x === "F").length;
        expectedWarnings = linksFirstLetters.filter((x) => x === "W").length;
    })
};

this.countActualTestsResults = () => {
    return ReportPage.getCurrentNoOfPassedTests()
        .then((tmp) => {
            currentNoOfPassedTests = tmp;
            return ReportPage.getCurrentNoOfConditionalyPassedTests();
        })
        .then((tmp) => {
            currentNoOfConditionalyPassedTests = tmp;
            return ReportPage.getCurrentNoOfFailedTests();
        })
        .then((tmp) => {
            currentNoOfFailedTests = tmp;
            return ReportPage.getCurrentNoOfWarnedTests();
        })
        .then((tmp) => {
            currentNoOfWarnedTests = tmp;
            return true;
        });
};


