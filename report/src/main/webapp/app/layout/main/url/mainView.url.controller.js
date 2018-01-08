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
  return ['$rootScope', '$stateParams', '$uibModal', 'metadataAccessService',
    'viewModeService',
    'notesService', 'patternsService', 'userSettingsService', 'caseFactory',
    MainViewUrlController];

  function MainViewUrlController($rootScope, $stateParams, $uibModal,
      metadataAccessService,
      viewModeService, notesService, patternsService, userSettingsService,
      caseFactory) {
    var vm = this;

    $rootScope.$on('metadata:changed', updateUrlView);
    $('[data-toggle="popover"]').popover({
      placement: 'bottom'
    });

    setupUrlView();

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function setupUrlView() {
      vm.displayCommentModal = displayCommentModal;
      vm.acceptCase = acceptCase;
      vm.revertCase = revertCase;

      vm.toggleMask = toggleMask;
      vm.toggleFullSource = toggleFullSource;
      vm.cases = getUrlCases($stateParams.test, $stateParams.url);
      vm.urlName = $stateParams.url;
    }

    function updateUrlView() {
      _.forEach(vm.cases, function (testcase) {
        testcase.update();
      });
    }

    function getUrlCases(testName, urlName) {
      var urlSteps = metadataAccessService.getUrlSteps(testName, urlName);
      var cases = [];
      _.forEach(urlSteps, function (step) {
        _.forEach(step.comparators, function (comparator, index) {
          cases.push(caseFactory.getCase(step, comparator, index));
        });
      });
      return cases;
    }

    function acceptCase(currentCase) {
      patternsService.updateCase($stateParams.test, $stateParams.url,
          currentCase.step.index,
          currentCase.index);
    }

    function revertCase(currentCase) {
      patternsService.revertCase($stateParams.test, $stateParams.url,
          currentCase.step.index,
          currentCase.index);
    }

    function displayCommentModal(currentCase) {
      $uibModal.open({
        animation: true,
        templateUrl: 'app/layout/modal/note/noteModal.view.html',
        controller: 'noteModalController',
        controllerAs: 'noteModal',
        resolve: {
          model: function () {
            return currentCase;
          },
          viewMode: function () {
            return viewModeService.COMPARATOR;
          },
          notesService: function () {
            return notesService;
          }
        }
      });
    }

    function toggleMask() {
      $rootScope.maskVisible = userSettingsService.toggleScreenshotMask();
    }

    function toggleFullSource() {
      $rootScope.fullSourceVisible = userSettingsService.toggleFullSource();
    }

  }
});
