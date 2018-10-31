'use strict';

//jasmine specs/api-artifact-spec.js

const aetsApi = require('../api/api.js');
const helper = require('./helpers/spec-helper.js');
const testDataJson = require('../data/test_data/api-data.json');
const chakram = require('chakram');
const chakramExpect = chakram.expect;
const jasmineExpect = expect;

const HTTP_200_OK = 200;
const getArtifactByArtifactIdData = testDataJson.getArtifactByArtifactId;
const getMetadataBySuiteNameData = testDataJson.getMetadataBySuiteName;
const NoExistingSuiteData = testDataJson.getArtifactByArtifactId.errorPath;

describe('Get artifact by artifact Id - validation', () => {

    it('Status code and JSON schema validation', () => {
        return aetsApi.getMetadata(getMetadataBySuiteNameData.parameters).then((response) => {
            getArtifactByArtifactIdData.parameters.id = helper.recursiveKeySearch("artifactId", response.body)[1];
        }).then(() => {
            return aetsApi.getArtifact(getArtifactByArtifactIdData.parameters).then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
            });
        });
    });

    // FIXME: Api returns wrong code (500 instead of 400), turn test on after fix
    xit('Error message validation', () => {
        return aetsApi.getArtifact(NoExistingSuiteData.parameters).then((response) => {
            chakramExpect(response).to.have.status(NoExistingSuiteData.code);
            jasmineExpect(response.body).toEqual(NoExistingSuiteData.errorMessage);
        });
    });
});