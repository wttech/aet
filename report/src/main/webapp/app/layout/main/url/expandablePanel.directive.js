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
  angularAMD.directive('aetExpandablePanel', expandablePanelDirective);

  function expandablePanelDirective($timeout) {

    return {
      restrict: 'A',
      link: addCollapseListeners
    };

    function addCollapseListeners(scope, element) {
      // uses '$timeout' in order to have 'element' ready
      $timeout(function () {
        var icon, onShown, onHidden, panel;

        icon = $(element).find('i');
        onShown = function () {
          icon.removeClass('glyphicon-chevron-down').addClass(
              'glyphicon-chevron-up');
        };
        onHidden = function () {
          icon.removeClass('glyphicon-chevron-up').addClass(
              'glyphicon-chevron-down');
        };

        // see also http://getbootstrap.com/javascript/#collapse-events
        panel = $(element).parent();
        panel.on('shown.bs.collapse', onShown);
        panel.on('hidden.bs.collapse', onHidden);
      });
    }
  }
});
