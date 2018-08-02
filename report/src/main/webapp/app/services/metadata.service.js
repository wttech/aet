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
define(['angularAMD', 'metadataCacheService', 'metadataEndpointService'],
    function (angularAMD) {
      'use strict';
      angularAMD.factory('metadataService', MetadataService);

      /**
       * Service responsible for statistics computation over fetched suite serving metadata nodes.
       */
      function MetadataService($rootScope, metadataCacheService,
          metadataEndpointService) {
        var service = {
              ingestData: ingestData,
              getSuite: getSuite,
              getTest: getTest,
              getUrl: getUrl,
              getStep: getStep,
              notifyMetadataChanged: notifyMetadataChanged,
              saveChangesLocally: saveChangesLocally,
              discardLocalChanges: discardLocalChanges,
              commitLocalChanges: commitLocalChanges
            },
            suite,
            testsByName,
            urlsByTestAndName;

        return service;

        function ingestData(dataFromService) {
          var suiteData = getLastModifiedVersion(dataFromService);

          var decoratedSuite = new StatisticsDecorator(suiteData);
          testsByName = {};
          urlsByTestAndName = {};

          _.forEach(suiteData.tests, function (test) {
            var decoratedTest = new StatisticsDecorator(test, decoratedSuite);
            _.forEach(test.urls, function (url) {
              var decoratedUrl = new StatisticsDecorator(url, decoratedTest);
              _.forEach(url.steps, function (step) {
                var decoratedStep = new StatisticsDecorator(step, decoratedUrl);
                _.forEach(step.comparators, function (comparator) {
                  decoratedStep.updateStatistics(comparator.stepResult);
                });
              });
              urlsByTestAndName[getKeyByTestAndUrlName(test.name,
                  url.name)] = decoratedUrl.get();
            });
            testsByName[test.name] = decoratedTest.get();
          });
          suite = decoratedSuite.get();
          $rootScope.metadataLoaded = true;
        }

        function getSuite() {
          return suite;
        }

        function getTest(testName) {
          return testsByName[testName];
        }

        function getUrl(testName, urlName) {
          return urlsByTestAndName[getKeyByTestAndUrlName(testName, urlName)];
        }

        function getStep(testName, urlName, stepIndex) {
          var step = null;
          var url = getUrl(testName, urlName);
          if (url && url.steps[stepIndex]) {
            step = url.steps[stepIndex];
          }
          return step;
        }

        function notifyMetadataChanged() {
          $rootScope.$emit('metadata:changed');
        }

        function saveChangesLocally() {
          metadataCacheService.put(suite);
        }

        function discardLocalChanges() {
          metadataCacheService.clearVersion(suite);
        }

        function commitLocalChanges() {
          metadataEndpointService.saveMetadata(suite).then(
              function () {
                $rootScope.metadataSaveInProgress = false;
                $rootScope.$emit('metadata:saveSuccess');
                metadataCacheService.clearVersion(suite);
                alert('Suite changes were saved successfully!');
                window.location.reload();
              }).catch(function (data) {
            var errMsg = 'Error while saving metadata: [' + data + ']';
            $rootScope.metadataSaveInProgress = false;
            $rootScope.$emit('metadata:saveFailed');
            console.error(errMsg);
            alert(errMsg);
          });
          $rootScope.metadataSaveInProgress = true;
          $rootScope.$emit('metadata:savingInProgress');
        }

        /***************************************
         ***********  Private methods  *********
         ***************************************/

        function getKeyByTestAndUrlName(testName, urlName) {
          return urlName + '@' + testName;
        }

        function getLastModifiedVersion(dataFromService) {
          var cachedData = metadataCacheService.getLatest(
              dataFromService.company, dataFromService.project,
              dataFromService.name);
          if (cachedData === null) {
            cachedData = dataFromService;
          } else {
            if (dataFromService.version > cachedData.version) {
              var oldSuite = {
                version: cachedData.version,
                correlationId: cachedData.correlationId
              };
              $rootScope.$emit('metadata:unsavedChangesDetected', oldSuite);
              cachedData = dataFromService;
            }
          }
          return cachedData;
        }

      }

      /**
       * Decorator pattern that counts all statuses for metadata tree nodes.
       * @param objectToDecorate - object that should be decorated.
       * @param decoratedParent - decorated object parent (wrapped into decorator), that child statistics should be passed to.
       */
      function StatisticsDecorator(objectToDecorate, decoratedParent) {
        var decoratedObject = objectToDecorate,
            decoratedParentReference = decoratedParent,
            decorator = {
              get: getDecoratedObject,
              passed: passed,
              failed: failed,
              warning: warning,
              rebased: rebased,
              conditionallyPassed: conditionallyPassed,
              updateStatistics: updateStatistics,
              updatePatternStatistics: updatePatternStatistics,
              revertPatternStatistics: revertPatternStatistics,
              updatePatternsToAccept: updatePatternsToAccept,
              updateAcceptedPatterns: updateAcceptedPatterns
            };

        setup();

        return decorator;

        function setup() {
          decoratedObject.total = 0;
          decoratedObject.failed = 0;
          decoratedObject.warning = 0;
          decoratedObject.passed = 0;
          decoratedObject.rebased = 0;
          decoratedObject.conditionallyPassed = 0;
          decoratedObject.patternsToAccept = 0;
          decoratedObject.acceptedPatterns = 0;
          decoratedObject.getStatus = getStatus;
          decoratedObject.updatePatternStatistics = updatePatternStatistics;
          decoratedObject.revertPatternStatistics = revertPatternStatistics;
        }

        function getStatus() {
          var status = 'passed';
          if (decoratedObject.failed > 0) {
            status = 'failed';
          } else if (decoratedObject.rebased > 0) {
            status = 'rebased';
          } else if (decoratedObject.warning > 0) {
            status = 'warning';
          } else if (decoratedObject.conditionallyPassed > 0) {
            status = 'conditionallyPassed';
          }
          return status;
        }

        function updatePatternStatistics() {
          decoratedObject.failed--;
          decoratedObject.rebased++;
          if (decoratedParentReference &&
              decoratedParentReference.updatePatternStatistics) {
            decoratedParentReference.updatePatternStatistics();
          }
          if (decoratedObject.comparators) {
            var acceptedPatterns = 0;
            _.forEach(decoratedObject.comparators, function (comparator) {
              if (hasStepComparatorChangesToAccept(comparator)) {
                comparator.stepResult.previousStatus = comparator.stepResult.status;
                comparator.stepResult.status = 'REBASED';
                acceptedPatterns++;
              }
            });
            updateAcceptedPatterns(acceptedPatterns);
          }
        }

        function revertPatternStatistics() {
          decoratedObject.failed++;
          decoratedObject.rebased--;
          if (decoratedParentReference &&
              decoratedParentReference.revertPatternStatistics) {
            decoratedParentReference.revertPatternStatistics();
          }
          if (decoratedObject.comparators) {
            var revertedPatterns = 0;
            _.forEach(decoratedObject.comparators, function (comparator) {
              if (hasStepComparatorAcceptedChanges(comparator)) {
                comparator.stepResult.status = comparator.stepResult.previousStatus;
                comparator.stepResult.previousStatus = null;
                revertedPatterns--;
              }
            });
            updateAcceptedPatterns(revertedPatterns);
          }
        }

        function hasStepComparatorChangesToAccept(comparator) {
          return comparator && comparator.stepResult &&
              comparator.stepResult.rebaseable &&
              comparator.stepResult.status === 'FAILED';
        }

        function hasStepComparatorAcceptedChanges(comparator) {
          return comparator && comparator.stepResult &&
              comparator.stepResult.rebaseable  &&
              comparator.stepResult.status === 'REBASED';
        }

        function getDecoratedObject() {
          return decoratedObject;
        }

        function passed() {
          decoratedObject.total++;
          decoratedObject.passed++;
          if (decoratedParentReference && decoratedParentReference.passed) {
            decoratedParentReference.passed();
          }
        }

        function failed() {
          decoratedObject.total++;
          decoratedObject.failed++;
          if (decoratedParentReference && decoratedParentReference.failed) {
            decoratedParentReference.failed();
          }
        }

        function warning() {
          decoratedObject.total++;
          decoratedObject.warning++;
          if (decoratedParentReference && decoratedParentReference.warning) {
            decoratedParentReference.warning();
          }
        }

        function rebased() {
          decoratedObject.total++;
          decoratedObject.rebased++;
          if (decoratedParentReference && decoratedParentReference.rebased) {
            decoratedParentReference.rebased();
          }
        }

        function conditionallyPassed() {
          decoratedObject.total++;
          decoratedObject.conditionallyPassed++;
          if (decoratedParentReference && decoratedParentReference.conditionallyPassed) {
            decoratedParentReference.conditionallyPassed();
          }
        }

        function updatePatternsToAccept(numberOfPatternsToAccept) {
          decoratedObject.patternsToAccept += numberOfPatternsToAccept;
          if (decoratedParentReference &&
              decoratedParentReference.updatePatternsToAccept) {
            decoratedParentReference.updatePatternsToAccept(
                numberOfPatternsToAccept);
          }
        }

        function updateAcceptedPatterns(numberOfAcceptedPatterns) {
          decoratedObject.acceptedPatterns += numberOfAcceptedPatterns;
          if (decoratedParentReference &&
              decoratedParentReference.updateAcceptedPatterns) {
            decoratedParentReference.updateAcceptedPatterns(
                numberOfAcceptedPatterns);
          }
        }

        function updateStatistics(stepResult) {
          if (stepResult && stepResult.status) {
            switch (stepResult.status) {
              case 'PASSED':
                passed();
                break;
              case 'CONDITIONALLY_PASSED':
                conditionallyPassed();
                break;
              case 'FAILED':
                // if `rebaseable` comparator, increment acceptable patterns counter
                if (stepResult.rebaseable) {
                  updatePatternsToAccept(1);
                }
                failed();
                break;
              case 'WARNING':
                warning();
                break;
              case 'REBASED':
                rebased();
                if (stepResult.previousStatus) {
                  updatePatternsToAccept(1);
                  updateAcceptedPatterns(1);
                }
                break;
              case 'PROCESSING_ERROR':
                failed();
                break;
              default:
                failed();
            }
          } else {
            failed();
          }
        }
      }
    });
