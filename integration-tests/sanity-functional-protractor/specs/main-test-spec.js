'use strict';

//npm test -- --specs specs/main-test-spec.js

const publishPage = require('../page_objects/abstract-po.js');
const reportPage = require('../page_objects/report-page-po.js');
let expectedSuccess, expectedFailed, expectedWarnings, expectedTotal;
let currentNoOfPassedTests, currentNoOfConditionalyPassedTests, currentNoOfFailedTests, currentNoOfWarnedTests;

describe('AET reports web application - main report results validation', () => {

    beforeAll((done) => {
        publishPage.open("reportHome");
        countExpectedTestsResults()
            .then(() => countActualTestsResults())
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

function countExpectedTestsResults() {
    return reportPage.getTestLinksTitles()
        .then((linksTitles) => {
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

function countActualTestsResults() {
    return reportPage.getCurrentNoOfPassedTests()
        .then((tmp) => {
            currentNoOfPassedTests = tmp;
            return reportPage.getCurrentNoOfConditionalyPassedTests();
        })
        .then((tmp) => {
            currentNoOfConditionalyPassedTests = tmp;
            return reportPage.getCurrentNoOfFailedTests();
        })
        .then((tmp) => {
            currentNoOfFailedTests = tmp;
            return reportPage.getCurrentNoOfWarnedTests();
        })
        .then((tmp) => {
            currentNoOfWarnedTests = tmp;
            return true;
        });
};


