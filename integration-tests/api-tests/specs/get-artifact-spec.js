'use strict';

//jasmine specs/get-artifact-spec.js

const aetsApi = require('../api/api.js');
const helper = require('./helpers/spec-helper.js');
const testDataJson = require('../data/test_data/api-data.json');
const chakram = require('chakram');
const chakramExpect = chakram.expect;
const jasmineExpect = expect;

describe('Get artifact by artifact Id - validation', () => {

    const HTTP_200_OK = 200;
    const HTTP_404_NOT_FOUND = 404;
    const HTTP_400_BAD_REQUEST = 400;

    const parametersSuiteName = testDataJson.getMetadataBySuiteName.parameters;
    const parametersArtifactId = testDataJson.getArtifactByArtifactId.parameters;
    const errorMessageJson = testDataJson.getMetadataByCorrelationId.errorMessage;
    const parametersCorrelationId = testDataJson.getMetadataByCorrelationId.parameters;
    parametersCorrelationId.correlationId = "no_existing_correlationId";

    it('Status validation', () => {
        return aetsApi.getMetadata(parametersSuiteName).then((response) => {
            parametersArtifactId.id = helper.recursiveKeySearch("artifactId", response.body)[1];
        }).then(() => {
            return aetsApi.getArtifact(parametersArtifactId).then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
            });
        });
    });

    it('Error message validation', () => {
        return aetsApi.getMetadata(parametersCorrelationId).then((response) => {
            chakramExpect(response).to.have.status(HTTP_400_BAD_REQUEST);
            jasmineExpect(response.body).toEqual(errorMessageJson);
        });
    });
});