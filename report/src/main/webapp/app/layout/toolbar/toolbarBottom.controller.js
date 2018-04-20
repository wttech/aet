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
  return ['$scope', '$rootScope', '$uibModal', '$stateParams',
    'patternsService', 'metadataAccessService', 'notesService',
    'viewModeService', 'suiteInfoService',
    ToolbarBottomController];

  function ToolbarBottomController($scope, $rootScope, $uibModal, $stateParams,
      patternsService, metadataAccessService, notesService, viewModeService,
      suiteInfoService) {
    var vm = this;

    // disables accept button if compared against another suite patterns
    if (suiteInfoService.getInfo().patternCorrelationId) {
      vm.usesCrossSuitePattern = true;
    }
    vm.showAcceptButton = patternsMayBeUpdated;
    vm.showRevertButton = patternsMarkedForUpdateMayBeReverted;
    vm.displayCommentModal = displayCommentModal;
    vm.scrollSidepanel = scrollSidepanel;

    $rootScope.$on('metadata:changed', updateToolbar);
    $scope.$watch('viewMode', function() {
        setTimeout(function() {
          $('[data-toggle="popover"]').not('.pre-initialized-popover').popover({
                placement: 'bottom'
          });
        }, 0);
    });

    $('[data-toggle="popover"]').popover({
      placement: 'bottom'
    }).addClass('pre-initialized-popover');

    updateToolbar();

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function updateToolbar() {
      vm.viewMode = viewModeService.get();
      switch (vm.viewMode) {
        case viewModeService.SUITE:
          setupSuiteToolbarModel();
          break;
        case viewModeService.TEST:
          setupTestToolbarModel();
          break;
        case viewModeService.URL:
          setupUrlToolbarModel();
          break;
        default:
          setupSuiteToolbarModel();
      }
    }

    function patternsMayBeUpdated() {
      var result = false;
      if (vm.model) {
        var patternsToAcceptLeft =
            vm.model.patternsToAccept - vm.model.acceptedPatterns;
        result = patternsToAcceptLeft > 0;
      }
      if (vm.usesCrossSuitePattern) {
        result = false;
      }
      return result;

    }

    function patternsMarkedForUpdateMayBeReverted() {
      var result = false;
      if (vm.model) {
        result =
            vm.model.acceptedPatterns > 0 &&
            vm.model.acceptedPatterns <= vm.model.patternsToAccept;
      }
      if (vm.usesCrossSuitePattern) {
        result = false;
      }
      return result;
    }

    function displayCommentModal() {
      $uibModal.open({
        animation: true,
        templateUrl: 'app/layout/modal/note/noteModal.view.html',
        controller: 'noteModalController',
        controllerAs: 'noteModal',
        resolve: {
          model: function () {
            return vm.model;
          },
          viewMode: function () {
            return vm.viewMode;
          },
          notesService: function () {
            return notesService;
          }
        }
      });
    }

    function scrollSidepanel(findParentGroup) {
        var $currentElement = $('a.test-name.is-active, a.test-url.is-active');
        var $reportGroup = $currentElement.closest('.aside-report.is-visible');
        var $parentElement = $reportGroup.find('.test-name');

        if (findParentGroup) {
            $parentElement.click();
            performScroll($parentElement);
            $reportGroup.addClass('is-expanded');
        } else {
            $reportGroup.addClass('is-expanded');
            performScroll($currentElement);
        }
    }

    function performScroll (element) {
        element[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});
    }

    /***************************************
     ***********  SUITE VIEW PART  *********
     ***************************************/
    function setupSuiteToolbarModel() {
      vm.model = metadataAccessService.getSuite();
      vm.updatePatterns = function () {
        patternsService.updateSuite();
      };
      vm.revertAcceptedPatterns = function () {
        patternsService.revertSuite();
      };
    }

    /***************************************
     ***********  TEST VIEW PART  *********
     ***************************************/
    function setupTestToolbarModel() {
      var testName = $stateParams.test;
      vm.model = metadataAccessService.getTest(testName);
      vm.updatePatterns = function () {
        patternsService.updateTest(vm.model.name, true);
      };
      vm.revertAcceptedPatterns = function () {
        patternsService.revertTest(vm.model.name, true);
      };
    }

    /***************************************
     ***********  URL VIEW PART  *********
     ***************************************/
    function setupUrlToolbarModel() {
      var testName = $stateParams.test;

      vm.model = metadataAccessService.getUrl($stateParams.test,
          $stateParams.url);
      vm.model.testGroupName = metadataAccessService.getTest(testName).name;
      vm.updatePatterns = function () {
        patternsService.updateUrl($stateParams.test, $stateParams.url, true);
      };
      vm.revertAcceptedPatterns = function () {
        patternsService.revertUrl($stateParams.test, $stateParams.url, true);
      };
    }
  }
});
