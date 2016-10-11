/*
 * Automated Exploratory Tests
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
define(['angularAMD', 'artifactsService'], function (angularAMD) {
  'use strict';
  angularAMD.factory('caseFactory', CaseFactoryService);

  /**
   * Service responsible for producing case that is displayed on report.
   */
  function CaseFactoryService($rootScope, artifactsService) {
    var service = {
      getCase: getCase
    };

    return service;

    function getCase(step, comparator, index) {
      return new BasicCaseModel(step, comparator, index, artifactsService);
    }

    /***************************************
     ***********  Private methods  *********
     ***************************************/

  }

  /**
   * Represents case model that is created from:
   * @param step - step object in which case exists.
   * @param comparator - case comparator model.
   * @param index - index of comparator in step
   * @param artifactsService
   */
  function BasicCaseModel(step, comparator, index, artifactsService) {
    var caseModel = {
      getPatternUrl: getPatternUrl,
      getPatternArtifact: getPatternArtifact,
      getDataUrl: getDataUrl,
      getDataArtifact: getDataArtifact,
      getResultUrl: getResultUrl,
      getResultArtifact: getResultArtifact
    };

    setup();

    return caseModel;

    function getPatternUrl() {
      return caseModel.step.pattern ? artifactsService.getArtifactUrl(caseModel.step.pattern) : null;
    }

    function getPatternArtifact () {
      //TODO
      return {};
    }

    function getDataUrl() {
      var url = null,
          stepResult = caseModel.step.stepResult;
      if (stepResult) {
        url = stepResult.artifactId ? artifactsService.getArtifactUrl(stepResult.artifactId) : null;
      }
      return url;
    }

    function getDataArtifact() {
      //TODO
      return {};
    }

    function getResultUrl(){
      var url = null,
          stepResult = caseModel.comparator.stepResult;
      if (stepResult) {
        url = stepResult.artifactId ? artifactsService.getArtifactUrl(stepResult.artifactId) : null;
      }
      return url;
    }

    function getResultArtifact() {
      //TODO
      return {};
    }

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function setup() {
      caseModel.comparator = comparator;
      caseModel.step = step;

      caseModel.displayName = getCaseDisplayName(step, comparator);
      var stepResult = comparator.stepResult;
      caseModel.showAcceptButton = stepResult && stepResult.rebaseable && stepResult.status === 'FAILED';
      caseModel.showRevertButton = comparator.hasNotSavedChanges;
      caseModel.index = index;
      caseModel.status = getCaseStatus(step, comparator);

      if (caseModel.status === 'PROCESSING_ERROR') {
        caseModel.errors = getCaseErrors(step, comparator);
        caseModel.getTemplate = function () {
          return 'app/layout/main/url/errors/processingError.html';
        };
      } else {
        caseModel.getTemplate = function () {
          return 'app/layout/main/url/reports/' + comparator.type + '.html';
        };
      }
    }

    function getCaseDisplayName(step, comparator) {
      var displayName = comparator.type;
      if (step.parameters && step.parameters.name) {
        displayName += ' ' + step.parameters.name;
      } else if (comparator.parameters && comparator.parameters.comparator) {
        displayName += ' ' + comparator.parameters.comparator;
      }
      return displayName;
    }

    function getCaseStatus(step, comparator) {
      var status;
      if (comparator.stepResult) {
        status = comparator.stepResult.status ? comparator.stepResult.status : step.stepResult.status;
      } else {
        status = step.stepResult ? step.stepResult.status : 'PROCESSING_ERROR';
      }
      return status;
    }

    function getCaseErrors(step, comparator) {
      var errors;
      if (comparator.stepResult) {
        errors = comparator.stepResult.errors ? comparator.stepResult.errors : step.stepResult.errors;
      } else {
        errors = step.stepResult ? step.stepResult.errors : 'Unknown error';
      }
      return errors;
    }

  }
});
