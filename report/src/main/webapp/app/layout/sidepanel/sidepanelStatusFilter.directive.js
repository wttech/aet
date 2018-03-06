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
  angularAMD.directive('aetSidepanelStatusFilter',
      ['$rootScope', '$timeout', SidepanelStatusFilterDirective]);

  function SidepanelStatusFilterDirective($rootScope, $timeout) {
    return {
      restrict: 'AE',
      link: init,
      controller: function ($scope) {
        $scope.clearFilters = clearActiveFilters;
      }
    };

    function init(scope, $element) {
      if (!$rootScope.activeFilters) {
        $rootScope.activeFilters = [];
      }
      $element.popover({
        placement: 'bottom',
        trigger: 'click',
        content: function () {
          return $element.find('.dropdown-menu').html();
        },
        html: true
      }).on('click', onFilterLabelClick)
      .parent().on('click', 'input', onFilterChoice);
    }

    function onFilterLabelClick() {
      if (!_.isEmpty($('.filters .popover'))) {
        var $popoverContent = $('.popover-content');

        $rootScope.activeFilters.forEach(function (filter) {
          $popoverContent
          .find('p[data-attribute="' + filter + '"]')
          .siblings()
          .prop('checked', ' ');
        });
      }
    }

    function onFilterChoice(event) {
      $timeout(function () {
        updateActiveFilters(extractActiveFilters(event));
      });
    }

    function extractActiveFilters(event) {
      var activeFilters = angular.copy($rootScope.activeFilters);
      var $elem = $(event.target),
          $link = $elem.siblings('p'),
          toggleStatus = $link.data('attribute');

      if (_.indexOf(activeFilters, toggleStatus) !== -1) {
        _.pull(activeFilters, toggleStatus);
      } else {
        activeFilters.push(toggleStatus);
      }
      return activeFilters;
    }

    function clearActiveFilters() {
      $timeout(function () {
        updateActiveFilters([]);
      });
    }

    function updateActiveFilters(currentFilters) {
      $rootScope.activeFilters = currentFilters;
      updateFiltersLabel($rootScope.activeFilters);
      $rootScope.$apply();
      $rootScope.$broadcast('filter:applied');
    }

    function updateFiltersLabel(activeFilters) {
      var titleElement = $('.filter-list-title p '),
          iconApply = $('.filter-list-title .apply-filters'),
          iconCancel = $('.filter-list-title .clear-filters');

      if (!_.isEmpty(activeFilters)) {
        var labelText = '';
        activeFilters.forEach(function (filter, index) {
          labelText += ' ' + filter.toLowerCase();
          if (activeFilters.length - 1 > index) {
            labelText += ', ';
          }
        });
        titleElement.html('Filters: ' + labelText);
        iconApply.hide();
        iconCancel.show();
      } else {
        titleElement.html('No filters applied');
        iconApply.show();
        iconCancel.hide();
      }
    }
  }
});
