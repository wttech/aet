'use strict';

//jasmine specs/get-metadata-spec.js

const aetsApi = require('../api/api.js');
const helper = require('./helpers/spec-helper.js');
const testDataJson = require('../data/test_data/api-data.json');
const chakram = require('chakram');
const _ = require("lodash");
const chakramExpect = chakram.expect;
const jasmineExpect = expect;

describe('Get metadata - validation', () => {

    const HTTP_200_OK = 200;
    const HTTP_404_NOT_FOUND = 404;
    const HTTP_400_BAD_REQUEST = 400;

    const parametersSuiteName = testDataJson.getMetadataBySuiteName.parameters;
    const getMetadataBySuiteNameJsonSchemaPath = require(testDataJson.getMetadataBySuiteName.jsonSchemaPath);
    const parametersNoExistingSuiteName = _.clone(parametersSuiteName);
    parametersNoExistingSuiteName.suite = "no_existing_suite";
    const parametersCorrelationId = testDataJson.getMetadataByCorrelationId.parameters;
    const getMetadataByCorrelationIdJsonSchemaPath = require(testDataJson.getMetadataByCorrelationId.jsonSchemaPath);

    describe('Get metadata by suite name api - validation', () => {

        it('JSON Schema validation', () => {
            return aetsApi.getMetadata(parametersSuiteName).then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
                chakramExpect(response).to.have.schema(getMetadataBySuiteNameJsonSchemaPath);
            });
        });

        it('Error message validation', () => {
            const errorMessageJson = testDataJson.getMetadataBySuiteName.errorMessage;
            return aetsApi.getMetadata(parametersNoExistingSuiteName).then((response) => {
                chakramExpect(response).to.have.status(HTTP_400_BAD_REQUEST);
                jasmineExpect(response.body).toEqual(errorMessageJson);
            });
        });
    });
    describe('Get metadata by correlationId - validation', () => {

        it('JSON Schema validation', () => {
            return aetsApi.getMetadata(parametersSuiteName).then((response) => {
                parametersCorrelationId.correlationId = response.body.correlationId;
            }).then(() => {
                return aetsApi.getMetadata(parametersCorrelationId).then((response) => {
                    chakramExpect(response).to.have.status(HTTP_200_OK);
                    chakramExpect(response).to.have.schema(getMetadataByCorrelationIdJsonSchemaPath);
                    jasmineExpect(response.body.correlationId).toEqual(parametersCorrelationId.correlationId);
                });
            });
        });

        it('Error message validation', () => {
            const parametersCorrelationId = _.clone(testDataJson.getMetadataByCorrelationId.parameters);
            parametersCorrelationId.correlationId = "no_existing_correlationId";
            const errorMessageJson = testDataJson.getMetadataByCorrelationId.errorMessage;
            return aetsApi.getMetadata(parametersCorrelationId).then((response) => {
                chakramExpect(response).to.have.status(HTTP_400_BAD_REQUEST);
                jasmineExpect(response.body).toEqual(errorMessageJson);
            });
        });
    });
});