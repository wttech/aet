'use strict';

const envData = require('../data/env.json');
const qs = require('querystring');
const chakram = require('chakram');
const getMetadata = '/api/metadata';
const getArtifact = '/api/artifact';
const domain = envData.env.local.AET;
const port = envData.env.local.port;

function Api() {

    this.getMetadata = (queryParams) => {
        return sendGet(getMetadata, queryParams)
    };

    this.getArtifact = (queryParams) => {
        return sendGet(getArtifact, queryParams)
    };

    const sendGet = (apiPath, queryParams) => {
        const serviceUrl = `${domain}:${port}${apiPath}`;
        const queryString = qs.stringify(queryParams);
        const fullUrl = `${serviceUrl}?${queryString}`;
        return chakram.get(fullUrl, {
            headers: {
                'Cache-Control': 'no-cache',
            }
        });
    };
}

module.exports = new Api();
