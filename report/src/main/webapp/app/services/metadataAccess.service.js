/*
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
define(['angularAMD', 'metadataService'], function (angularAMD) {
  'use strict';
  angularAMD.factory('metadataAccessService', MetadataAccessService);

  /**
   * Service responsible for access to suite nodes.
   */
  function MetadataAccessService(metadataService) {
    var service = {
      getSuite: getSuite,
      getTest: getTest,
      getTests: getTests,
      getUrl: getUrl,
      getTestUrls: getTestUrls,
      getStep: getStep,
      getUrlSteps: getUrlSteps
    };

    return service;

    function getSuite() {
      return metadataService.getSuite();
    }

    function getTests() {
      return getSuite().tests;
    }

    function getTest(testName) {
      return metadataService.getTest(testName);
    }

    function getTestUrls(testName) {
      var test = getTest(testName);
      return test !== null ? test.urls : [];
    }

    function getUrl(testName, urlName) {
      return metadataService.getUrl(testName, urlName);
    }

    function getUrlSteps(testName, urlName) {
      var url = getUrl(testName, urlName);
      return url !== null ? url.steps : [];
    }

    function getStep(testName, urlName, stepIndex) {
      return metadataService.getStep(testName, urlName, stepIndex);
    }
  }
});
