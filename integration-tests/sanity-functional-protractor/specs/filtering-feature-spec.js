'use strict';

//npm test -- --specs specs/filtering-feature-spec.js

const publishPage = require('../page_objects/abstract-po.js');
const ReportPage = require('../page_objects/report-page-po.js');
const testDataJson = require('../data/test_data/filtering-data.json');
const testCasesData = testDataJson.testData;

describe('AET reports web application - filtering results validation', () => {

    beforeAll(() => {
        publishPage.open("reportHome");
    });

    testCasesData.forEach((testCase, idx) => {
        it(idx + 1 + `. Test case with test data:
      - Filtering phrase: ${testCase.searchPhrase}
      - Expected no. of total results 
      - Expected statistics: ${testCase.statistics}`, () => {
            return ReportPage.fillInSearchText(testCase.searchPhrase).then(() => {
                expect(ReportPage.getCurrentNoOfTests()).toEqual(getNoOfResultsFromTestData(testCase.statistics));
                expect(ReportPage.getStatistics()).toEqual(testCase.statistics);
            });
        });
    });
});

function getNoOfResultsFromTestData(statistics) {
    return parseInt(statistics.slice(0, statistics.indexOf(' ')));
}


