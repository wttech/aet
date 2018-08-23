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
define([], function () {
  'use strict';
  return ['$rootScope', '$uibModal', 'suiteInfoService', 'metadataAccessService', 'historyService',
    ToolbarTopController
  ];

  function ToolbarTopController($rootScope, $uibModal, suiteInfoService,
    metadataAccessService, historyService) {
    var vm = this;

    $rootScope.$on('metadata:changed', updateToolbar);
    $('[data-toggle="popover"]').popover({
      placement: 'bottom'
    });

    historyService.fetchHistory(suiteInfoService.getInfo().version, function() {
      var currentVersion = suiteInfoService.getInfo().version;
      getPreviousVersion(currentVersion);
      getNextVersion(currentVersion);
      updateToolbar();
    });

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function updateToolbar() {
      vm.showSuiteHistory = function () {
        displayHistoryModal();
      };
      vm.suiteInfo = suiteInfoService.getInfo();
      vm.suiteStatistics = metadataAccessService.getSuite();
      if (vm.suiteStatistics.patternCorrelationId) {
        vm.pattern = {
          name: vm.suiteStatistics.patternCorrelationId,
          url: 'report.html?company=' + vm.suiteStatistics.company +
            '&project=' + vm.suiteStatistics.project +
            '&correlationId=' + vm.suiteStatistics.patternCorrelationId
        };
      }
    }

    function getPreviousVersion(currentVersion) {
      vm.previousVersion = historyService.getPreviousVersion(currentVersion);
    }

    function getNextVersion(currentVersion) {
      vm.nextVersion = historyService.getNextVersion(currentVersion);
    }

    function displayHistoryModal() {
      $uibModal.open({
        animation: true,
        templateUrl: 'app/layout/modal/history/historyModal.view.html',
        controller: 'historyModalController',
        controllerAs: 'historyModal',
        resolve: {
          model: function () {
            return vm.model;
          },
          viewMode: function () {
            return vm.viewMode;
          },
        }
      });
    }
  }
});