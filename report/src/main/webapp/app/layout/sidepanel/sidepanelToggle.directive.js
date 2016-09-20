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
define(['angularAMD'], function (angularAMD) {
	'use strict';
	// custom prefix for custom directive 'aet' see: http://bit.ly/29U2YFf
	angularAMD.directive('aetSidepanelToggle', ['$rootScope', 'localStorageService', ToggleSidepanelDirective]);

	function ToggleSidepanelDirective($rootScope, localStorageService) {
		var EXPANDED_SIDEBAR_KEY_NAME = 'aet:expandedSidepanel',
			directive;

		directive = {
			restrict: 'AE',
			scope: {
				'type': '@'
			},
			link: linkFunc
		};

		return directive;

		function linkFunc(scope, element) {
			var storedExpanded = localStorageService.get(EXPANDED_SIDEBAR_KEY_NAME);
			if (storedExpanded === null) {
				expand();
			}
			$rootScope.sidebarExpanded = isExpanded();
			var toggleIcon = element.children('i');
			element.on('click', function () {
				$('body').toggleClass('menu-expanded');
				toggle();

				if (isExpanded()) {
					toggleIcon.removeClass('glyphicon-chevron-right').addClass('glyphicon-chevron-left');
				} else {
					toggleIcon.removeClass('glyphicon-chevron-left').addClass('glyphicon-chevron-right');
				}
			});
		}

		function expand() {
			localStorageService.put(EXPANDED_SIDEBAR_KEY_NAME, true);
		}

		function close() {
			localStorageService.put(EXPANDED_SIDEBAR_KEY_NAME, false);
		}

		function isExpanded() {
			var storedState = localStorageService.get(EXPANDED_SIDEBAR_KEY_NAME);
			return storedState;
		}

		function toggle() {
			if (isExpanded()) {
				close();
			} else {
				expand();
			}
		}
	}
});
