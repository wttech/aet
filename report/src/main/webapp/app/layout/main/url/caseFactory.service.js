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
define(['angularAMD', 'artifactsService', 'suiteInfoService'],
    function (angularAMD) {
      'use strict';
      angularAMD.factory('caseFactory', CaseFactoryService);

      /**
       * Service responsible for producing case that is displayed on report.
       */
      function CaseFactoryService($rootScope, artifactsService,
          suiteInfoService) {
        var service = {
          getCase: getCase
        };

        return service;

        function getCase(step, comparator, index) {
          return new BasicCaseModel(step, comparator, index, artifactsService,
              suiteInfoService);
        }

      }

      /**
       * Represents case model that is created from:
       * @param step - step object in which case exists.
       * @param comparator - case comparator model.
       * @param index - index of comparator in step
       * @param artifactsService
       */
      function BasicCaseModel(step, comparator, index, artifactsService,
          suiteInfoService) {
        var caseModel = {
          result: {},
          collectorResult: {},
          update: update,
          getPatternUrl: getPatternUrl,
          getDataUrl: getDataUrl,
          getResultUrl: getResultUrl
        }, templateProvider = new ExtensionsTemplateProvider();

        setup();

        return caseModel;

        function hasPattern() {
          return caseModel.step && caseModel.step.pattern;
        }

        function getPatternUrl() {
          return hasPattern() ? artifactsService.getArtifactUrl(
              caseModel.step.pattern) : null;
        }

        function hasData() {
          return caseModel.step.stepResult &&
              caseModel.step.stepResult.artifactId;
        }

        function getDataUrl() {
          return hasData() ? artifactsService.getArtifactUrl(
              caseModel.step.stepResult.artifactId) : null;
        }

        function hasResult() {
          return caseModel.comparator.stepResult &&
              caseModel.comparator.stepResult.artifactId;
        }

        function getResultUrl() {
          return hasResult() ? artifactsService.getArtifactUrl(
              caseModel.comparator.stepResult.artifactId) : null;
        }

        function getResultArtifact() {
          caseModel.result = {};
          if (hasResult()) {
            artifactsService.getArtifact(
                caseModel.comparator.stepResult.artifactId)
            .then(function (data) {
              caseModel.result = data;
            })
            .catch(function (e) {
              console.log(e);
            });
          }
        }

        function update() {
          caseModel.displayName = getCaseDisplayName(step, comparator);
          var stepResult = comparator.stepResult;
          caseModel.showAcceptButton =
              stepResult &&
              stepResult.rebaseable &&
              stepResult.status === 'FAILED';
          if (suiteInfoService.getInfo().patternCorrelationId) {
            caseModel.usesCrossSuitePattern = true;
          }
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
              return templateProvider.getTemplateUrl(step, comparator);
            };
          }
        }

        /***************************************
         ***********  Private methods  *********
         ***************************************/

        function setup() {
          caseModel.comparator = comparator;
          caseModel.step = step;

          update();
          getResultArtifact();
          initializeCollectorResultArtifact();
        }

        function getCaseDisplayName(step, comparator) {
          var displayName = comparator.type;
          if (step.parameters && step.parameters.name) {
            displayName += ' ' + step.parameters.name;
          } else if (comparator.parameters &&
              comparator.parameters.comparator) {
            displayName += ' ' + comparator.parameters.comparator;
          }
          return displayName;
        }

        function getCaseStatus(step, comparator) {
          var status;
          if (comparator.stepResult) {
            status =
                comparator.stepResult.status ? comparator.stepResult.status
                    : step.stepResult.status;
          } else {
            status = step.stepResult ? step.stepResult.status
                : 'PROCESSING_ERROR';
          }
          return status;
        }

        function getCaseErrors(step, comparator) {
          var errors;
          if (comparator.stepResult) {
            errors =
                comparator.stepResult.errors ? comparator.stepResult.errors
                    : step.stepResult.errors;
          } else {
            errors = step.stepResult ? step.stepResult.errors : 'Unknown error';
          }
          return errors;
        }

        function initializeCollectorResultArtifact() {
          var collectorHasResult =
              caseModel.step.stepResult && caseModel.step.stepResult.artifactId,
              sourceCollector = caseModel.step.type === 'source',
              artifactId;

          if (collectorHasResult) {
            artifactId = caseModel.step.stepResult.artifactId;
            if (sourceCollector) {
              caseModel.collectorResult = artifactsService.getArtifactUrl(
                  artifactId);
            } else {
              console.log('Getting: ', artifactId);
              artifactsService.getArtifact(artifactId)
              .then(function (data) {
                caseModel.collectorResult = data;
              })
              .catch(function (e) {
                console.log(e);
              });
            }
          }
        }

      }

      /**
       * Provides path to template for each type.
       */
      function ExtensionsTemplateProvider() {
        var service = {
          getTemplateUrl: getTemplateUrl
        };

        return service;

        function getTemplateUrl(step, comparator) {
          var type = step.type,
              comparatorAlgorithm =
                  comparator.parameters ? comparator.parameters.comparator
                      : null,
              templateName;

          if (comparatorAlgorithm && comparatorAlgorithm !== type) {
            templateName =
                comparatorAlgorithm ? type + '_' + comparatorAlgorithm
                    : type;
          } else {
            templateName = type;
          }

          return 'app/layout/main/url/reports/' + templateName + '.html';
        }
      }

    });
