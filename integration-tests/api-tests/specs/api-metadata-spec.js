'use strict';

//jasmine specs/api-metadata-spec.js

const aetsApi = require('../api/api.js');
const testDataJson = require('../data/test_data/api-data.json');
const chakram = require('chakram');
const _ = require("lodash");
const chakramExpect = chakram.expect;
const jasmineExpect = expect;

const HTTP_200_OK = 200;
const getMetadataByCorrelationIdData = testDataJson.getMetadataByCorrelationId;
const getMetadataByCorrelationIdJsonSchemaPath = require(getMetadataByCorrelationIdData.jsonSchemaPath);
const notExistingCorrelationIdParametersData = getMetadataByCorrelationIdData.errorPath;
const getMetadataBySuiteNameData = testDataJson.getMetadataBySuiteName;
const getMetadataBySuiteNameJsonSchemaPath = require(getMetadataBySuiteNameData.jsonSchemaPath);
const notExistingSuiteParametersData = getMetadataBySuiteNameData.errorPath;
const getMetadataBySuiteNameAndVersionData = testDataJson.getMetadataBySuiteNameAndVersion;
const getMetadataBySuiteNameAndVersionJsonSchemaPath = require(getMetadataBySuiteNameAndVersionData.jsonSchemaPath);
let lastReportVersion;

describe('Get and update metadata - tests', () => {

    describe('Get metadata by correlationId - validation', () => {

        it('JSON Schema validation', () => {
            return aetsApi.getMetadata(getMetadataBySuiteNameData.parameters).then((response) => {
                getMetadataByCorrelationIdData.parameters.correlationId = response.body.correlationId;
            }).then(() => {
                return aetsApi.getMetadata(getMetadataByCorrelationIdData.parameters).then((response) => {
                    chakramExpect(response).to.have.status(HTTP_200_OK);
                    chakramExpect(response).to.have.schema(getMetadataByCorrelationIdJsonSchemaPath);
                    jasmineExpect(response.body.correlationId).toEqual(getMetadataByCorrelationIdData.parameters.correlationId);
                });
            });
        });

        // FIXME: Api returns wrong error message, turn test on after fix
        xit('Error message validation', () => {
            return aetsApi.getMetadata(notExistingCorrelationIdParametersData.parameters).then((response) => {
                chakramExpect(response).to.have.status(notExistingCorrelationIdParametersData.code);
                jasmineExpect(response.body).toEqual(notExistingCorrelationIdParametersData.errorMessage);
            });
        });
    });

    describe('Get metadata by suite name - validation', () => {

        it('JSON Schema validation', () => {
            return aetsApi.getMetadata(getMetadataBySuiteNameData.parameters).then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
                chakramExpect(response).to.have.schema(getMetadataBySuiteNameJsonSchemaPath);
            });
        });

        // FIXME: Api returns wrong error message, turn test on after fix
        xit('Error message validation', () => {
            return aetsApi.getMetadata(notExistingSuiteParametersData.parameters).then((response) => {
                chakramExpect(response).to.have.status(notExistingSuiteParametersData.code);
                jasmineExpect(response.body).toEqual(notExistingSuiteParametersData.errorMessage);
            });
        });
    });

    describe('Get metadata by suite name and version - validation', () => {

        beforeAll(() => {
            return aetsApi.getMetadata(getMetadataBySuiteNameData.parameters).then((response) => {
                lastReportVersion = response.body.version;
            })
        });

        it('JSON Schema validation for last report version', () => {
            getMetadataBySuiteNameAndVersionData.parameters.version = lastReportVersion;
            return aetsApi.getMetadata(getMetadataBySuiteNameAndVersionData.parameters).then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
                chakramExpect(response).to.have.schema(getMetadataBySuiteNameAndVersionJsonSchemaPath);
            });
        });

        // FIXME: Api returns wrong error message, turn test on after fix
        xit('Error message validation - no existing report version', () => {
            getMetadataBySuiteNameAndVersionData.parameters.version = lastReportVersion + 1;
            return aetsApi.getMetadata(getMetadataBySuiteNameAndVersionData.parameters).then((response) => {
                chakramExpect(response).to.have.status(getMetadataBySuiteNameAndVersionData.errorCode);
                jasmineExpect(response.body).toEqual(getMetadataBySuiteNameAndVersionData.errorMessage);
            });
        });
    });

    xdescribe('Update suite metadata - validation', () => {

        //TODO: Test to be created, documentation link: https://github.com/Cognifide/aet/wiki/WebAPI#update-suite-metadata

    });
});