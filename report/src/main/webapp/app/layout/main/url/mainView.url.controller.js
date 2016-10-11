/*
 * Automated Exploratory Tests
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
  return ['$rootScope', '$stateParams', '$uibModal', 'metadataAccessService', 'viewModeService',
          'notesService', 'patternsService',
          MainViewUrlController];

  function MainViewUrlController($rootScope, $stateParams, $uibModal, metadataAccessService,
                                 viewModeService, notesService, patternsService) {
    var vm = this;

    $rootScope.$on('metadata:changed', updateUrlView);
    $('[data-toggle="popover"]').popover({
       placement: 'bottom'
     });

    updateUrlView();

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function updateUrlView() {
      vm.cases = getUrlCases($stateParams.test, $stateParams.url);
      vm.urlName = $stateParams.url;
      vm.displayCommentModal = displayCommentModal;
      vm.updateCurrentCase = updateCurrentCase;
      vm.acceptCase = acceptCase;
      vm.revertCase = revertCase;
    }

    function getUrlCases(testName, urlName) {
      var urlSteps = metadataAccessService.getUrlSteps(testName, urlName);
      var cases = [];
      _.forEach(urlSteps, function (step) {
        _.forEach(step.comparators, function (comparator, index) {
          var currentCase = {};
          currentCase.comparator = comparator;
          currentCase.displayName = getCaseDisplayName(step, comparator);
          var stepResult = comparator.stepResult;
          currentCase.showAcceptButton =
              stepResult && stepResult.rebaseable && stepResult.status === 'FAILED';
          currentCase.showRevertButton = comparator.hasNotSavedChanges;
          currentCase.index = index;
          currentCase.stepIndex = step.index;
          currentCase.status = getCaseStatus(step, comparator);

          if (currentCase.status === 'PROCESSING_ERROR') {
            currentCase.errors = getCaseErrors(step, comparator);
            currentCase.getTemplate = function () {
              return 'app/layout/main/url/errors/processingError.html';
            };
          } else {
            currentCase.getTemplate = function () {
              return 'app/layout/main/url/reports/' + comparator.type + '.html';
            };
          }
          cases.push(currentCase);
        });
      });
      return cases;
    }

    function getCaseDisplayName(step, comparator) {
      var displayName = comparator.type;
      if (step.parameters && step.parameters.name) {
        displayName += ' ' + step.parameters.name;
      } else if (comparator.parameters && comparator.parameters.comparator) {
        displayName += ' ' + comparator.parameters.comparator;
      }
      return displayName;
    }

    function getCaseStatus(step, comparator) {
      var status;
      if (comparator.stepResult) {
        status = comparator.stepResult.status ? comparator.stepResult.status : step.stepResult.status;
      } else {
        status = step.stepResult ? step.stepResult.status : 'PROCESSING_ERROR';
      }
      return status;
    }

    function getCaseErrors(step, comparator) {
      var errors;
      if (comparator.stepResult) {
        errors = comparator.stepResult.errors ? comparator.stepResult.errors : step.stepResult.errors;
      } else {
        errors = step.stepResult ? step.stepResult.errors : 'Unknown error';
      }
      return errors;
    }

    function updateCurrentCase(currentCase) {
      console.log('currentCase:', currentCase);
      vm.currentCase = currentCase;
    }

    function acceptCase(currentCase) {
      patternsService.updateCase($stateParams.test, $stateParams.url, currentCase.stepIndex,
                                 currentCase.index);
    }

    function revertCase(currentCase) {
      patternsService.revertCase($stateParams.test, $stateParams.url, currentCase.stepIndex,
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

  }
});
