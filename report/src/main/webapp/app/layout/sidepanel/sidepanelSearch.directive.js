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
define(['angularAMD'], function (angularAMD) {
  'use strict';
  angularAMD.directive('aetSidepanelSearch', SidepanelSearch);

  function SidepanelSearch($rootScope, $timeout) {
    return {
      restrict: 'AE',
      link: function (scope, $element) {
        $element.on('input', onInput);
      },
      controller: function ($scope) {
        $scope.clearSearch = clearSearchPhrase;
      }
    };

    function onInput() {
      $timeout(refreshSearchScope);
    }

    function clearSearchPhrase() {
      $timeout(function () {
        $rootScope.searchText = '';
        refreshSearchScope();
      });
    }

    function refreshSearchScope() {
      updateSearchIcon($rootScope.searchText);
      $rootScope.$apply();
      $rootScope.$broadcast('filter:applied');
    }

    function updateSearchIcon(searchPhrase) {
      var iconSearch = $('.sidepanel-search-section .glyphicon-search'),
          iconCancel = $('.sidepanel-search-section .glyphicon-remove');

      if (searchPhrase && searchPhrase.length > 0) {
        iconSearch.hide();
        iconCancel.show();
      } else {
        iconSearch.show();
        iconCancel.hide();
      }
    }
  }
});
