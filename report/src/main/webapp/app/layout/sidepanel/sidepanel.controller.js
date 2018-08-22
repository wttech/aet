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
  return ['$rootScope', '$scope', '$timeout', '$location', 'metadataAccessService',
    'metadataService', 'notesService', SidepanelController];

  function SidepanelController($rootScope, $scope, $timeout, $location,
      metadataAccessService, metadataService, notesService) {
    var vm = this;
    vm.thereAreChangesToSave = thereAreChangesToSave;
    vm.saveAllChanges = saveAllChanges;
    vm.discardAllChanges = discardAllChanges;

    $rootScope.$on('metadata:changed', updateNavigationTree);
    $rootScope.$on('filter:applied', updateTestsStats);
    $scope.$watch('sidepanel.tests', function() {
      $timeout(function() {
          var url = $location.url();
          var hrefSelector = 'a[href="#' + url + '"';
          var $element = $(hrefSelector);

          $element.closest('.aside-report.is-visible').addClass('is-expanded');
          $element[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});
      });
    });

    $('[data-toggle="popover"]').popover({
      placement: 'bottom'
    });
    updateNavigationTree();

    function thereAreChangesToSave() {
      return metadataAccessService.getSuite().acceptedPatterns > 0 ||
          notesService.unsavedNotesExist();
    }

    function saveAllChanges() {
      console.log('All changes will be saved!');
      metadataService.commitLocalChanges();
    }

    function discardAllChanges() {
      console.log('All changes will be discarded!');
      metadataService.discardLocalChanges();
      window.location.reload();
    }

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function updateNavigationTree() {
      vm.testsStats = {
        total: 0,
        conditionallyPassed: 0,
        failed: 0,
        warning: 0,
        passed: 0,
        rebased: 0
      };
      vm.tests = metadataAccessService.getTests();
      $timeout(updateTestsStats);
    }

    function updateTestsStats() {
      vm.testsStats.total = $('.test-name.is-visible').length;
      refreshTestsStatsValue('failed');
      refreshTestsStatsValue('warning');
      refreshTestsStatsValue('passed');
      refreshTestsStatsValue('rebased');
      refreshTestsStatsValue('conditionallyPassed');
      $scope.$apply();
    }

    function refreshTestsStatsValue(statusName) {
      vm.testsStats[statusName] =
          $('.test-name.' + statusName + '.is-visible').length;
    }
  }
});
