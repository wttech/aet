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
 define(['angularAMD', 'endpointConfiguration', 'suiteInfoService' , 'caseFactory'], function (angularAMD) {
  'use strict';
  angularAMD.factory('rerunService', rerunService);

  /**
   * Service responsible for fetching suite's history
   */
  function rerunService($rootScope, $http, endpointConfiguration, suiteInfoService, caseFactory) {
    $rootScope.endpointUrl = endpointConfiguration.getEndpoint().getUrl;
    var service = {
      getRerunStatistics: getRerunStatistics,
      rerunSuite: rerunSuite,
      rerunTest: rerunTest,
      rerunURL: rerunURL,
    };

    return service;

    function getRerunStatistics(msg) {
      var statsArray = msg;
      statsArray = statsArray.replace(/:::/, ',');
      statsArray = statsArray.replace(/\d{2}:\d{2}:\d{2}\.\d{3}/g, '');
      statsArray = statsArray.replace(/[a-zA-z]|:/g, '');
      statsArray = statsArray.replace(/\s/g, '');
      statsArray = statsArray.split(',');
      return statsArray;
    }

    function rerunSuite() {
      $rootScope.rerunInProgress = true;
      var suiteInfo = suiteInfoService.getInfo();
      var rerunParams = 'company=' + suiteInfo.company + '&' + 'project=' + suiteInfo.project + '&' +
        'suite=' + suiteInfo.name;
      var url = 'http://aet-vagrant:8181/suite-rerun?' + rerunParams;
      $http.post(url, {}).then(function successCallback(response) {
        $rootScope.rerunMsg = 'Suite rerun initialized';
        $rootScope.rerunInProgressSuccessful = true;
        $rootScope.rerunProgress = 0;
        checkRerunStatus(response.data.statusUrl);
      }, function errorCallback(response) {
        $rootScope.rerunMsg = response.statusText;
      });
    }

    function rerunTest(testName) {
      $rootScope.rerunInProgress = true;
      var suiteInfo = suiteInfoService.getInfo();
      var rerunParams = 'company=' + suiteInfo.company + '&' + 'project=' + suiteInfo.project + '&' +
        'suite=' + suiteInfo.name + '&' + 'testName=' + testName;
      var url = 'http://aet-vagrant:8181/suite-rerun?' + rerunParams;
      $http.post(url, {}).then(function successCallback(response) {
        $rootScope.rerunMsg = 'Test rerun initialized';
        $rootScope.rerunProgress = 0;
        $rootScope.rerunInProgressSuccessful = true;
        checkRerunStatus(response.data.statusUrl);
      }, function errorCallback(response) {
        $rootScope.rerunMsg = response.statusText;
      });
    }

    function rerunURL(testName, testUrl) {
      $rootScope.rerunInProgress = true;
      var suiteInfo = suiteInfoService.getInfo();
      var rerunParams = 'company=' + suiteInfo.company + '&' + 'project=' + suiteInfo.project + '&' +
        'suite=' + suiteInfo.name + '&' + 'testUrl=' + testUrl + '&' + 'testName=' + testName;
      var url = 'http://aet-vagrant:8181/suite-rerun?' + rerunParams;
      $http.post(url, {}).then(function successCallback(response) {
        $rootScope.rerunMsg = 'URL rerun initialized';
        $rootScope.rerunProgress = 0;
        $rootScope.rerunInProgressSuccessful = true;
        checkRerunStatus(response.data.statusUrl);
      }, function errorCallback(response) {
        $rootScope.rerunMsg = response.statusText;
      });
    }

    function checkRerunStatus(statusUrl) {
      var url = 'http://aet-vagrant:8181' + statusUrl;
      setTimeout(function () {
        $http.get(url, {}).then(function successCallback(response) {
          if (response.data.status === 'FINISHED') {
            var suiteInfo = suiteInfoService.getInfo();
            var linkParams = '?' + 'company=' + suiteInfo.company + '&' + 'project=' + suiteInfo.project + '&' +
              'suite=' + suiteInfo.name;
            linkParams = linkParams + '#' + window.location.href.split('#')[1];
            var linkToLatestSuite = location.protocol + '//' + location.host + location.pathname + linkParams;
            $rootScope.rerunMsg = 'Rerun completed. Page will now refresh.';
            $rootScope.rerunProgress = 100;
            if (window.location.href !== linkToLatestSuite) {
              window.location.assign(linkToLatestSuite);
            } else {
              window.location.reload();
            }
            return;
          } else if (response.data.status === 'PROGRESS') {
            var totalTests = response.data.progressLog.collectLog.toReceiveMessages;
            var completedTests = response.data.progressLog.collectLog.receivedMessagesSuccess;
            $rootScope.rerunProgress = ((completedTests / totalTests) * 90);
            $rootScope.rerunMsg = 'Rerun in progress.';
          } else {
            $rootScope.rerunMsg = 'Waiting for results.';
          }
          checkRerunStatus(statusUrl);
        }, function errorCallback(response) {
          $rootScope.rerunMsg = response.data.status;
          return;
        });
      }, 2000);
    }
    
  }
});