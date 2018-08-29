'use strict';

const AbstractPage = require('./abstract-po.js');
const qs = require('querystring')
const chakram = require('chakram');
const port = 8181;
const getMetadata = '/api/metadata';
const getArtifact = '/api/artifact';

function RestAPI() {

    this.getMetadata = (queryParams) => {
        const serviceUrl = `${AbstractPage.getDomain()}:${port}${getMetadata}`;
        const queryString = qs.stringify(queryParams);
        const fullUrl = `${serviceUrl}?${queryString}`;
        return chakram.get(fullUrl, {
            headers: {
                'Cache-Control': 'no-cache',
            }
        }).then((response) => {
            return response;
        });
    };

    this.getArtifact  = (queryParams) => {
        const serviceUrl = `${AbstractPage.getDomain()}:${port}${getArtifact}`;
        const queryString = qs.stringify(queryParams);
        const fullUrl = `${serviceUrl}?${queryString}`;
        return chakram.get(fullUrl, {
            headers: {
                'Cache-Control': 'no-cache',
            }
        }).then((response) => {
            return response;
        });
    };

}

RestAPI.prototype = AbstractPage;

module.exports = new RestAPI();
