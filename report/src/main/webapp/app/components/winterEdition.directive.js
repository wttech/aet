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
  // custom prefix for custom directive 'aet' see: http://bit.ly/29U2YFf
  angularAMD.directive('aetWinterEdition', WinterEdition);

  function WinterEdition() {
    return {
      restrict: 'AE',
      link: linkFunc
    };

    function linkFunc($scope) {
      var today = new Date();
      var CHRISTMAS_DATE = new Date(today.getFullYear(), 3, 15);

      var isChristmas = isInDateRange(today, CHRISTMAS_DATE, 14);

      if (isChristmas) {
        $scope.statusClasses.passed = 'fa-tree';
        $scope.statusClasses.failed = 'fa-gift';
        $scope.statusClasses.warning = 'fa-bell';
        $scope.statusClasses.rebased = 'fa-snowflake';
        $scope.statusClasses.unrebased = 'fa-eraser';
      }
    }

    function isInDateRange(today, eventDate, dayMargin) {
      var eventStartDate = new Date(eventDate);
      var eventEndDate = new Date(eventDate);

      eventStartDate.setDate(eventStartDate.getDate()-dayMargin);
      eventEndDate.setDate(eventEndDate.getDate()+dayMargin);

      return today >= eventStartDate && today <= eventEndDate;
    }
  }
});
