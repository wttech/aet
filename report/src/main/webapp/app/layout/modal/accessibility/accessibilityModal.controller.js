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
define(['angularAMD', 'endpointConfiguration'], function (angularAMD) {
  'use strict';
  angularAMD.controller('accessibilityModalController', AccessibilityModalController);

  function AccessibilityModalController($scope, $http, $uibModalInstance, model, $stateParams, $window, endpointConfiguration) {

    var vm = this;
    init();

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function init() {
      vm.report = {
        logLevel: 'ERROR',
        format: 'xlsx'
      }
      vm.generateReport = generateReport;
      vm.cancelReport = cancelReport;
      vm.model = model;
    }

    function generateReport() {
      const { company, correlationId, name: suite, project } = model;
      const baseUrl = endpointConfiguration.getEndpoint().getUrl;
      const downloadUrl = 'accessibility/report?' +
      'company=' + company +
      '&project=' + project +
      '&suite=' + suite +
      '&correlationId=' + correlationId +
      '&type=' + vm.report.logLevel +
      '&extenstion=' + vm.report.format;

      $window.open(baseUrl + downloadUrl, "_blank");

      $uibModalInstance.close();
    }

    function cancelReport() {
      $uibModalInstance.close();
    }
  }
});
