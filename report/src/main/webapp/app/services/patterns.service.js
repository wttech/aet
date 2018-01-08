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
define(['angularAMD', 'metadataService', 'metadataAccessService'],
    function (angularAMD) {
      'use strict';
      angularAMD.factory('patternsService', PatternsService);

      /**
       * Service responsible for controlling metadata patterns.
       */
      function PatternsService(metadataService, metadataAccessService) {
        var service = {
          updateSuite: updateSuite,
          revertSuite: revertSuite,
          updateTest: updateTest,
          revertTest: revertTest,
          updateUrl: updateUrl,
          revertUrl: revertUrl,
          updateCase: updateCase,
          revertCase: revertCase
        };

        return service;

        function updateSuite() {
          var suite = metadataAccessService.getSuite();
          _.forEach(suite.tests, function (test) {
            updateTest(test.name, false);
          });
          notifyMetadataUpdated(true);
        }

        function revertSuite() {
          var suite = metadataAccessService.getSuite();
          _.forEach(suite.tests, function (test) {
            revertTest(test.name, false);
          });
          notifyMetadataUpdated(true);
        }

        function updateTest(testName, shouldNotify) {
          var test = metadataAccessService.getTest(testName);
          _.forEach(test.urls, function (url) {
            updateUrl(testName, url.name, false);
          });
          notifyMetadataUpdated(shouldNotify);
        }

        function revertTest(testName, shouldNotify) {
          var test = metadataAccessService.getTest(testName);
          _.forEach(test.urls, function (url) {
            revertUrl(testName, url.name, false);
          });
          notifyMetadataUpdated(shouldNotify);
        }

        function updateUrl(testName, urlName, shouldNotify) {
          var url = metadataAccessService.getUrl(testName, urlName);
          _.filter(url.steps, isCollectorWithPattern).forEach(function (step) {
            _.forEach(step.comparators, function (comparator) {
              updateComparator(step, comparator, false);
            });
          });
          notifyMetadataUpdated(shouldNotify);
        }

        function revertUrl(testName, urlName, shouldNotify) {
          var url = metadataAccessService.getUrl(testName, urlName);
          _.filter(url.steps, isCollectorWithPattern).forEach(function (step) {
            _.forEach(step.comparators, function (comparator) {
              revertComparator(step, comparator, false);
            });
          });
          notifyMetadataUpdated(shouldNotify);
        }

        function updateCase(testName, urlName, stepIndex, comparatorIndex) {
          var step = metadataAccessService.getStep(testName, urlName,
              stepIndex),
              result = false;
          if (isCollectorWithPattern(step)) {
            result = updateComparator(step, step.comparators[comparatorIndex],
                true);
          }
          return result;
        }

        function revertCase(testName, urlName, stepIndex, comparatorIndex) {
          var step = metadataAccessService.getStep(testName, urlName,
              stepIndex),
              result = false;
          if (isCollectorWithPattern(step)) {
            result = revertComparator(step, step.comparators[comparatorIndex],
                true);
          }
          return result;
        }

        /***************************************
         ***********  Private methods  *********
         ***************************************/

        function updateComparator(step, comparator, shouldNotify) {
          var result = false;

          if (isCollectorWithPattern(step) && isStepComparatorRebaseable(
                  comparator)) {
            result = !comparator.stepResult.previousStatus;
            if (result) {
              step.updatePatternStatistics();
              step.oldPattern = step.pattern;
              step.pattern = step.stepResult.artifactId;
              comparator.hasNotSavedChanges = true;
            }

            notifyMetadataUpdated(shouldNotify);
          }
          return result;
        }

        function revertComparator(step, comparator, shouldNotify) {
          var result = false;

          if (isCollectorWithPattern(step) && isStepComparatorRebaseable(
                  comparator)) {
            result = comparator.stepResult.previousStatus === 'FAILED';
            if (result) {
              step.revertPatternStatistics();
              step.pattern = step.oldPattern;
              delete step.oldPattern;
              delete comparator.hasNotSavedChanges;
            }

            notifyMetadataUpdated(shouldNotify);
          }
          return result;
        }

        function isCollectorWithPattern(step) {
          return step && step.comparators && step.pattern;
        }

        function isStepComparatorRebaseable(comparator) {
          return comparator &&
              comparator.stepResult &&
              comparator.stepResult.rebaseable;
        }

        function notifyMetadataUpdated(shouldNotify) {
          if (shouldNotify) {
            metadataService.notifyMetadataChanged();
            metadataService.saveChangesLocally();
          }
        }
      }
    });
