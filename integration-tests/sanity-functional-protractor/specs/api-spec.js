'use strict';

//npm test -- --specs specs/api-spec.js

const PublishPage = require('../page_objects/abstract-po.js');
const AetsApi = require('../page_objects/api-po.js');
const testDataJson = require('../data/test_data/api-data.json');
const chakram = require('chakram');
const chakramExpect = chakram.expect;
const jasmineExpect = expect;

describe('AET reports web application - Rest API tests', () => {

    const HTTP_200_OK = 200;
    const HTTP_404_NOT_FOUND = 404;
    const HTTP_400_BAD_REQUEST = 400;

    describe('Get metadata by suite name api - validation', () => {

        it('JSON Schema validation', () => {
            const parametersSuiteName = testDataJson.get_metadata_by_suite_name.parameters;
            const jsonSchemaPath = require(testDataJson.get_metadata_by_suite_name.json_schema_path);
            return AetsApi.getMetadata(parametersSuiteName).then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
                chakramExpect(response).to.have.schema(jsonSchemaPath);
            });
        });

        it('Error message validation', () => {
            const parametersSuiteName = testDataJson.get_metadata_by_suite_name.parameters;
            const parametersNoExistingSuiteName = clone(parametersSuiteName);
            parametersNoExistingSuiteName.suite = "no_existing_suite"
            const errorMessageJson = testDataJson.get_metadata_by_suite_name.error_message;
            return AetsApi.getMetadata(parametersNoExistingSuiteName).then((response) => {
                chakramExpect(response).to.have.status(HTTP_400_BAD_REQUEST);
                jasmineExpect(response.body).toEqual(errorMessageJson);
            });
        });
    });
    describe('Get metadata by correlationId - validation', () => {

        it('JSON Schema validation', () => {
            const parametersSuiteName = testDataJson.get_metadata_by_suite_name.parameters;
            const parametersCorrelationId = testDataJson.get_metadata_by_correlationId.parameters;
            const jsonSchemaPath = require(testDataJson.get_metadata_by_correlationId.json_schema_path);
            return AetsApi.getMetadata(parametersSuiteName).then((response) => {
                parametersCorrelationId.correlationId = response.body.correlationId;
            }).then(() => {
                return AetsApi.getMetadata(parametersCorrelationId).then((response) => {
                    chakramExpect(response).to.have.status(HTTP_200_OK);
                    chakramExpect(response).to.have.schema(jsonSchemaPath);
                    jasmineExpect(response.body.correlationId).toEqual(parametersCorrelationId.correlationId);
                });
            });
        });

        it('Error message validation', () => {
            const parametersCorrelationId = clone(testDataJson.get_metadata_by_correlationId.parameters);
            parametersCorrelationId.correlationId = "no_existing_correlationId"
            const errorMessageJson = testDataJson.get_metadata_by_correlationId.error_message;
            return AetsApi.getMetadata(parametersCorrelationId).then((response) => {
                chakramExpect(response).to.have.status(HTTP_400_BAD_REQUEST);
                jasmineExpect(response.body).toEqual(errorMessageJson);
            });
        });
    });

    describe('Get artifact by artifact Id - validation', () => {

        it('Status validation', () => {
            const parametersSuiteName = testDataJson.get_metadata_by_suite_name.parameters;
            const parametersArtifactId = testDataJson.get_artifact_by_artifact_Id.parameters;
            return AetsApi.getMetadata(parametersSuiteName).then((response) => {
                parametersArtifactId.id = recursiveKeySearch("artifactId", response.body)[1];
            }).then(() => {
                return AetsApi.getArtifact(parametersArtifactId).then((response) => {
                    chakramExpect(response).to.have.status(HTTP_200_OK);
                });
            });
        });

        it('Error message validation', () => {
            const errorMessageJson = testDataJson.get_metadata_by_correlationId.error_message;
            const parametersCorrelationId = clone(testDataJson.get_metadata_by_correlationId.parameters);
            parametersCorrelationId.correlationId = "no_existing_correlationId"
            return AetsApi.getMetadata(parametersCorrelationId).then((response) => {
                chakramExpect(response).to.have.status(HTTP_400_BAD_REQUEST);
                jasmineExpect(response.body).toEqual(errorMessageJson);
            });
        });
    });
});

this.jsonSearchForKey = (jsonObj, keyName) => {
    for (let key in jsonObj) {
        if (key == keyName) return key;
        else this.jsonSearchForKey(key, keyName);
    }
};

const recursiveKeySearch = (key, data) => {
    let results = [];
    if (data.constructor === Array) {
        for (var i = 0, len = data.length; i < len; i++) {
            results = results.concat(recursiveKeySearch(key, data[i]));
        }
    }
    else if ((data !== null) && (typeof(data) == "object")) {
        for (let dataKey in data) {
            if (key === dataKey) {
                results.push(data[key]);
            }
            results = results.concat(recursiveKeySearch(key, data[dataKey]));
        }
    }
    return results;
};

const clone = (object) => {
    return JSON.parse(JSON.stringify(object));
};