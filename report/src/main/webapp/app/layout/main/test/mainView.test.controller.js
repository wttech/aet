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
  return ['$rootScope', '$stateParams', 'metadataAccessService',
    MainViewTestController];

  function MainViewTestController($rootScope, $stateParams,
      metadataAccessService) {
    var vm = this;

    $rootScope.$on('metadata:changed', updateTestView);
    $('[data-toggle="popover"]').popover({
      placement: 'bottom'
    });

    updateTestView();

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function updateTestView() {
      vm.urls = metadataAccessService.getTestUrls($stateParams.test);
      vm.testName = $stateParams.test;
    }
  }
});
