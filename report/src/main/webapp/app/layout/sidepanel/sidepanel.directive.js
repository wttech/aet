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
  angularAMD.directive('aetSidepanel',
      ['$rootScope', 'localStorageService', SidepanelDirective]);

  function SidepanelDirective($rootScope, localStorageService) {
    var EXPANDED_SIDEBAR_KEY_NAME = 'aet:expandedSidepanel';
    var INIT_SIDEPANEL_WIDTH = 350;
    var $sidepanel;
    var $content;
    var $toggleIcon;

    var onWindowResizeThrottled = _.throttle(onWindowResize, 100);

    return {
      restrict: 'AE',
      link: linkFunc
    };

    function linkFunc($scope, $element) {
      var newWidth = INIT_SIDEPANEL_WIDTH;
      var isSidepanelResized = false;

      $scope.sidebarExpanded = true;

      $rootScope.$on('$stateChangeSuccess', function() {
        $content = $element.find('.main');
        $sidepanel = $element.find('.aside');
        $toggleIcon = $element.find('.toolbar-toggle i');
      });

      $scope.$watch('sidebarExpanded', function(newValue, oldValue) {
        if (newValue !== oldValue) {
          toggleSidepanel();
        }
      });

      $element.on('mousedown', '.aside-resizer', function (e) {
        isSidepanelResized = true;

        e.preventDefault();
      });

      $element.on('mousemove', _.throttle(function (e) {
        if (isSidepanelResized) {
          newWidth = limitSidepanelSize(e.pageX);
          updateWidth(newWidth);
          e.preventDefault();
        }
      }, 10));

      $element.on('mouseup', function () {
        isSidepanelResized = false;
      });

      $element.on('click', '.toolbar-toggle', function () {
        $scope.sidebarExpanded = !$scope.sidebarExpanded;
        $scope.$apply();
      });

      $(window).on('resize', onWindowResizeThrottled);
    }

    function isExpanded() {
      return localStorageService.get(EXPANDED_SIDEBAR_KEY_NAME);
    }

    function toggleSidepanel() {
      if (isExpanded()) {
        close();
      } else {
        expand();
      }
    }

    function onWindowResize() {
      updateWidth(limitSidepanelSize($sidepanel.outerWidth()));
    }

    function expand() {
      $content.css('left', $sidepanel.outerWidth());
      $content.css('width', document.body.clientWidth - $sidepanel.outerWidth());
      $sidepanel.css('left', 0);

      $(window).on('resize', onWindowResizeThrottled);
      localStorageService.put(EXPANDED_SIDEBAR_KEY_NAME, true);
    }

    function close() {
      $content.css('left', 0);
      $content.css('width', '');
      $sidepanel.css('left', -$sidepanel.outerWidth());

      $(window).off('resize', onWindowResizeThrottled);
      localStorageService.put(EXPANDED_SIDEBAR_KEY_NAME, false);
    }

    function updateWidth(newWidth) {
      var newContentWidth = document.body.clientWidth - newWidth;

      $content.css('left', newWidth);
      $content.css('width', newContentWidth);
      $sidepanel.css('width', newWidth);
    }

    function limitSidepanelSize(xPos) {
      return Math.min(Math.max(INIT_SIDEPANEL_WIDTH, xPos), document.body.clientWidth/2);
    }
  }
});
