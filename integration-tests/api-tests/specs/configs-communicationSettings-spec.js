'use strict';

//jasmine specs/configs-communicationSettings-spec.js

const aetsApi = require('../api/api.js');
const testDataJson = require('../data/test_data/api-data.json');
const _ = require("lodash");
const chakram = require('chakram');
const chakramExpect = chakram.expect;

const HTTP_200_OK = 200;
const getCommunicationSettingsJsonSchemaPath = require(testDataJson.getCommunicationSettings.jsonSchemaPath);


describe('Get communication settings - validation', () => {

    it('Status code and JSON schema validation', () => {
        return aetsApi.getCommunicationSettings().then((response) => {
            chakramExpect(response).to.have.status(HTTP_200_OK);
            chakramExpect(response).to.have.schema(getCommunicationSettingsJsonSchemaPath);
        });
    });
});