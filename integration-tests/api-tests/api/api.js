'use strict';

const envData = require('../data/env.json');
const qs = require('querystring');
const chakram = require('chakram');

const domain = envData.env.local.AET;
const port = envData.env.local.port;
const metadata = '/api/metadata';
const artifact = '/api/artifact';
const history = '/api/history';
const lock = '/lock';
const communicationSettings = '/configs/communicationSettings';

function Api() {

    this.getMetadata = (queryParams) => {
        return sendGet(metadata, queryParams);
    };

    this.getArtifact = (queryParams) => {
        return sendGet(artifact, queryParams);
    };

    this.getHistory = (queryParams) => {
        return sendGet(history, queryParams);
    };

    this.getLock = (params) => {
        const fullUrl = `${domain}:${port}${lock}/${params}`;
        return chakram.get(fullUrl, {
            headers: {
                'Cache-Control': 'no-cache',
            }
        });
    };

    this.setLock = (params, data) => {
        const fullUrl = `${domain}:${port}${lock}/${params}`;
        return chakram.post(fullUrl, data, {
            headers: {
                'Cache-Control': 'no-cache',
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
    };

    this.getCommunicationSettings = () => {
        return sendGet(communicationSettings);
    };

    const sendGet = (apiPath, queryParams = null) => {
        const serviceUrl = `${domain}:${port}${apiPath}`;
        let fullUrl;
        (queryParams !== null) ? fullUrl = `${serviceUrl}?${qs.stringify(queryParams)}` : fullUrl = `${serviceUrl}`;
        return chakram.get(fullUrl, {
            headers: {
                'Cache-Control': 'no-cache',
            }
        });
    };
}

module.exports = new Api();
