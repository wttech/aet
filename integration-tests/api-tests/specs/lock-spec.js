'use strict';

// This Spec locks test suite for 20s and should be executed last - please remember to put it at last place in jasmine.json file
//jasmine specs/lock-spec.js

const aetsApi = require('../api/api.js');
const testDataJson = require('../data/test_data/api-data.json');
const _ = require("lodash");
const chakram = require('chakram');
const chakramExpect = chakram.expect;

const HTTP_200_OK = 200;
const getLockForSuiteData = testDataJson.getLockForSuite;
const getLockForSuiteJsonSchemaPath = require(getLockForSuiteData.jsonSchemaPath);
const tryToSetLockData = testDataJson.tryToSetLock;
let mainSuiteCorrelationId;

describe('Get, set, update lock - tests', () => {

    describe('Get lock for suite - validation', () => {

        it('Status code and JSON schema validation', () => {
            aetsApi.getLock(getLockForSuiteData.parameters).then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
                chakramExpect(response).to.have.schema(getLockForSuiteJsonSchemaPath);
            });
        });
    });

    describe('Try to set lock - validation', () => {

        beforeAll((done) => {
            aetsApi.getMetadata(tryToSetLockData.mainSuiteParameters).then((response) => {
                mainSuiteCorrelationId = response.body.correlationId;
                done();
            })
        });

        it('Check suite status, if this test fails suite is probably already locked and test can\'t be perform. Try to wait 20s and run tests ones again', () => {
            aetsApi.getLock(getLockForSuiteData.parameters).then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
                chakramExpect(response.body).equals(false);

            });
        });

        it('set Lock', () => {
            aetsApi.setLock(getLockForSuiteData.parameters, `value=${mainSuiteCorrelationId}`).then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
            });
        });

        it('Check suite status', () => {
            aetsApi.getLock(getLockForSuiteData.parameters).then((response) => {
                chakramExpect(response).to.have.status(HTTP_200_OK);
                chakramExpect(response.body).equals(true);
            });
        });

        it('set Lock one more time ', () => {
            aetsApi.setLock(getLockForSuiteData.parameters, `value=${mainSuiteCorrelationId}`).then((response) => {
                chakramExpect(response).to.have.status(409);
            });
        });

    });

    xdescribe('Update heart beat - validation', () => {

        //TODO: Test to be created, documentation link: https://github.com/Cognifide/aet/wiki/WebAPI#update-heart-beat

    });
});
