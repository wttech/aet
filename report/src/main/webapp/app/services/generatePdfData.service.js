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
define(['angularAMD', 'endpointConfiguration', 'metadataService'], function (angularAMD) {
  'use strict';
  angularAMD.factory('generatePdfDataService', generatePdfDataService);

  /**
   * Service responsible for creating and modifying data used in pdf report
   */
  function generatePdfDataService(metadataService) {
    var service = {
      getTestData: getTestData,
      getTestStats: getTestStats,
      getTestLabels: getTestLabels,
      getTestColors: getTestColors,
      getTestsObject: getTestsObject,
      getCasesObject: getCasesObject,
      setObjectInitialParams: setObjectInitialParams,
      getCategorizedTestObject: getCategorizedTestObject
    };

    return service;

    function getTestData() {
      var testsStatistics = metadataService.getTestStatistics();
      var testsByCategories = [];
      for (var i = 0; i < testsStatistics.length; i++) {
        if (!hasCategoryProperty(testsByCategories, testsStatistics[i])) {
          switch (testsStatistics[i].result) {
            case 'failed':
              {
                testsByCategories.push({
                  category: testsStatistics[i].category,
                  failed: 1,
                  warning: 0,
                  passed: 0,
                  rebased: 0
                });
                break;
              }
            case 'warning':
              {
                testsByCategories.push({
                  category: testsStatistics[i].category,
                  failed: 0,
                  warning: 1,
                  passed: 0,
                  rebased: 0
                });
                break;
              }
            case 'passed':
              {
                testsByCategories.push({
                  category: testsStatistics[i].category,
                  failed: 0,
                  warning: 0,
                  passed: 1,
                  rebased: 0
                });
                break;
              }
            case 'rebased':
              {
                testsByCategories.push({
                  category: testsStatistics[i].category,
                  failed: 0,
                  warning: 0,
                  passed: 0,
                  rebased: 1
                });
                break;
              }
            default:
              {
                console.log('An error occurred');
              }
          }
        } else {
          var indexOf = findIndex(testsByCategories, testsStatistics[i].category);
          switch (testsStatistics[i].result) {
            case 'failed':
              {
                testsByCategories[indexOf].failed++;
                break;
              }
            case 'warning':
              {
                testsByCategories[indexOf].warning++;
                break;
              }
            case 'passed':
              {
                testsByCategories[indexOf].passed++;
                break;
              }
            case 'rebased':
              {
                testsByCategories[indexOf].rebased++;
                break;
              }
            default:
              {
                console.log('An error occurred');
              }
          }
        }
      }
      return testsByCategories;
    }

    function getTestStats(testsByCategories) {
      var testStats = {
        failed: 0,
        passed: 0,
        warning: 0,
        rebased: 0
      }

      testsByCategories.forEach(function (cat) {
        testStats.failed += cat.failed;
        testStats.passed += cat.passed;
        testStats.warning += cat.warning;
        testStats.rebased += cat.rebased;
      });
      return testStats;
    }

    function getTestColors() {
      var testColors = {
        failed: '#bb5a5a',
        warning: '#f0ad4e',
        passed: '#6f9f00',
        rebased: '#0097fe',
      };
      return testColors;
    }

    function getTestLabels() {
      var testLabels = {
        failed: 'Failed',
        warning: 'Warning',
        passed: 'Passed',
        rebased: 'Rebased',
      };
      return testLabels;
    }

    function getTestsObject(testStats, testColors, testLabels) {
      var tests = {
        stats: {
          failed: testStats.failed,
          warning: testStats.warning,
          passed: testStats.passed,
          rebased: testStats.rebased,
        },
        colors: testColors,
        labels: testLabels,
        parameters: {
          startingX: 100,
          startingY: 430,
          width: 400,
          height: 180,
          chartName: 'Tests Statistics - Total: ' + 0,
          maxValue: 0,
          numOfYAxisPoints: 0,
          step: 0,
          rowHeight: 40,
          axisValues: [],
          columnMargin: 0,
          columnWidth: 0,
          columnStartingPoint: 0,
          columnHeight: 0,
          offsetY: 0,
        }
      };
      return tests;
    }

    function getCasesObject(testStats, testColors, testLabels) {
      var cases = {
        stats: {
          failed: testStats.failed,
          warning: testStats.warning,
          passed: testStats.passed,
          rebased: testStats.rebased,
        },
        colors: testColors,
        labels: testLabels,
        parameters: {
          startingX: 100,
          startingY: 210,
          width: 400,
          height: 180,
          totalTests: 0,
          chartName: '',
          maxValue: 0,
          numOfYAxisPoints: 0,
          step: 0,
          rowHeight: 40,
          axisValues: [],
          columnMargin: 0,
          columnWidth: 0,
          columnStartingPoint: 0,
          columnHeight: 0,
          offsetY: 0,
        }
      };
      return cases;
    }

    function getCategorizedTestObject(testData, testColors, testLabels) {
      var test = {
        stats: {
          failed: testData.failed,
          warning: testData.warning,
          passed: testData.passed,
          rebased: testData.rebased,
        },
        colors: testColors,
        labels: testLabels,
        parameters: {
          startingX: 100,
          startingY: 100,
          width: 400,
          height: 180,
          totalTests: 0,
          chartName: '',
          maxValue: 0,
          numOfYAxisPoints: 0,
          step: 0,
          rowHeight: 40,
          axisValues: [],
          columnMargin: 0,
          columnWidth: 0,
          columnStartingPoint: 0,
          columnHeight: 0,
          offsetY: 0,
        }
      };
      return test;
    }

    function setObjectInitialParams(obj, testName) {
      obj.parameters.totalTests = parseInt(obj.stats.failed) + parseInt(obj.stats.warning) + parseInt(obj.stats.passed) + parseInt(obj.stats.rebased);
      obj.parameters.chartName = testName + obj.parameters.totalTests;
      return obj;
    }

    /***************************************
    ***********  Private methods  *********
    ***************************************/

    function hasCategoryProperty(obj1, obj2) {
      for (var i = 0; i < obj1.length; i++) {
        if (obj1[i].category === obj2.category) {
          return true;
        }
      }
      return false;
    }

    function findIndex(obj1, obj2) {
      for (var i = 0; i < obj1.length; i++) {
        if (obj1[i].category === obj2) {
          return i;
        }
      }
    }
  }
});