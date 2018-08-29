'use strict';

//npm test -- --specs specs/filtering-feature.js

const PublishPage = require('../page_objects/abstract-po.js');
const ReportPage = require('../page_objects/report-page-po.js');
const testDataJson = require('../data/test_data/filtering-data.json');
const testCasesData = testDataJson.test_data;

describe('AET reports web application - filtering results validation', () => {

    beforeAll(() => {
        PublishPage.open("reportHome");
    });

    testCasesData.forEach((testCase, idx) => {
        it(idx + 1 + `. Test case with test data:
      - Filtering phrase: ${testCase.search_phrase}
      - Expected no. of total results 
      - Expected statistics: ${testCase.statistics}`, () => {
            ReportPage.fillInSearchText(testCase.search_phrase).then(() => {
                expect(ReportPage.getCurrentNoOfTests()).toEqual(parseInt(this.getNoOfResultsFromTestData(testCase.statistics)));
                expect(ReportPage.getStatistics()).toEqual(testCase.statistics);
            });
        });
    });
});

this.getNoOfResultsFromTestData = (statistics) => {
    return statistics.slice(0, statistics.indexOf(' '));
}


