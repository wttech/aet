/*
 * AET
 *
 * Copyright (C) 2021 Wunderman Thompson Technology
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
define(['angularAMD', 'endpointConfiguration'], function (angularAMD) {
  'use strict';
  angularAMD.controller('accessibilityModalController', AccessibilityModalController);

  function AccessibilityModalController($scope, $http, $uibModalInstance,
     model, $stateParams, $window, endpointConfiguration) {

    var vm = this;
    init();

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function init() {
      vm.report = {
        verbosityLevel: { error: true, warn: true },
        format: 'xlsx'
      };
      vm.generateReport = generateReport;
      vm.cancelReport = cancelReport;
      vm.canGenerateReport = canGenerateReport;
      vm.model = model;
    }

    function verbosityLevelToUrl() {
      var verbosityLevel = vm.report.verbosityLevel;
      return Object.keys(verbosityLevel)
        .filter(function (value) {
          return !!verbosityLevel[value];
        })
        .join(',');
    }

    function canGenerateReport() {
      return !!verbosityLevelToUrl();
    }

    function generateReport() {
      var baseUrl = endpointConfiguration.getEndpoint().getUrl;
      var verbosityToUrl = verbosityLevelToUrl();

      var downloadUrl = 'accessibility/report?' +
      'company=' + model.company +
      '&project=' + model.project +
      '&suite=' + model.name +
      '&correlationId=' + model.correlationId +
      '&verbosity=' + verbosityToUrl +
      '&extension=' + vm.report.format;

      $window.open(baseUrl + downloadUrl, '_blank');

      $uibModalInstance.close();
    }

    function cancelReport() {
      $uibModalInstance.close();
    }
  }
});
