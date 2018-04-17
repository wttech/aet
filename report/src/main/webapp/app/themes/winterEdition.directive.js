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
      var WINTER_THEME = {
        name: 'winter',
        statusClasses: {
          passed: 'fa-tree',
          failed: 'fa-gift',
          warning: 'fa-bell',
          rebased: 'fa-snowflake',
          unrebased: 'fa-eraser'
        }
      };
      var TODAY = new Date();
      var CHRISTMAS_DATE = new Date(TODAY.getFullYear(), 11, 25);
      var DAY_MARGIN = 14;
      var isChristmas = isDateWithinMargin(TODAY, CHRISTMAS_DATE, DAY_MARGIN);

      if (isChristmas) {
        _.merge($scope.theme, WINTER_THEME);
        snowFall.snow(document.body, {flakeCount: 200, maxSpeed: 20});
      }
    }

    function isDateWithinMargin(today, eventDate, dayMargin) {
      var eventStartDate = new Date(eventDate);
      var eventEndDate = new Date(eventDate);

      eventStartDate.setDate(eventStartDate.getDate()-dayMargin);
      eventEndDate.setDate(eventEndDate.getDate()+dayMargin);

      return today >= eventStartDate && today <= eventEndDate;
    }
  }
});
