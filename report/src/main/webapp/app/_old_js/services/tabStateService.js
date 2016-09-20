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

	angularAMD.factory('tabStateService', function ($rootScope) {
		return {
			preserveState: function () {
				var $testTabs = $('.test-tabs').find('li.active');
				if (!_.isEmpty($testTabs) && !_.isUndefined($testTabs.children().attr('class'))) {
					$rootScope.preservedTabClass = $testTabs.children().attr('class').split(' ')[0];
				}
			},
			bindTabState: function (directive) {
				var tabsWrapper = $('.nav-tabs-wrapper');
				tabsWrapper.on('click', 'a', function () {
					directive.preserveState();
				});
			},
			forceTabState: function () {
				var $preservedTabLink = $('.nav-tabs li a.' + $rootScope.preservedTabClass);

				if (!_.isUndefined($rootScope.preservedTabClass) && $preservedTabLink.length) {
					$preservedTabLink.click();
				} else {
					$('.nav-tabs li:first-of-type a').click();
				}
			}
		};
	});

});
