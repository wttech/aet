'use strict';

//jasmine specs/api-history-spec.js

const aetsApi = require('../api/api.js');
const testDataJson = require('../data/test_data/api-data.json');
const _ = require("lodash");
const chakram = require('chakram');
const chakramExpect = chakram.expect;
const jasmineExpect = expect;

const HTTP_200_OK = 200;
const getCorrelationIdsAndVersionsBySuiteNameData = testDataJson.getCorrelationIdsAndVersionsBySuiteName;
const getCorrelationIdsAndVersionsBySuiteNameJsonSchemaPath = require(getCorrelationIdsAndVersionsBySuiteNameData.jsonSchemaPath);
const getCorrelationIdsAndVersionsBySuiteNameNoExistingSuiteData = getCorrelationIdsAndVersionsBySuiteNameData.errorPath;


describe('Get correlationIds and versions by suite name - validation', () => {

    it('Status code and JSON schema validation', () => {
        return aetsApi.getHistory(getCorrelationIdsAndVersionsBySuiteNameData.parameters)
            .then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
                chakramExpect(response).to.have.schema(getCorrelationIdsAndVersionsBySuiteNameJsonSchemaPath);
            });
    });

    // FIXME: Api returns wrong status code (404 instead of 400), turn test on after fix
    xit('Error message validation', () => {
        return aetsApi.getHistory(getCorrelationIdsAndVersionsBySuiteNameNoExistingSuiteData.parameters)
            .then((response) => {
                chakramExpect(response).to.have.status(getCorrelationIdsAndVersionsBySuiteNameNoExistingSuiteData.code);
                jasmineExpect(response.body).toEqual(getCorrelationIdsAndVersionsBySuiteNameNoExistingSuiteData.errorMessage);
            });
    });
});