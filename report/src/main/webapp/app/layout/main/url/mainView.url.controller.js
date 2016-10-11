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
	return ['$rootScope', '$stateParams', 'metadataAccessService', MainViewUrlController];

	function MainViewUrlController($rootScope, $stateParams, metadataAccessService) {
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
			vm.comparators = getUrlComparators($stateParams.test, $stateParams.url);
			vm.urlName = $stateParams.url;
		}

		function getUrlComparators(testName, urlName) {
			var urlSteps = metadataAccessService.getUrlSteps(testName, urlName);
			var comparators = [];
			_.forEach(urlSteps, function (step) {
				_.forEach(step.comparators, function (comparator) {
					comparator.displayName = getComparatorDisplayName(step, comparator);
					comparators.push(comparator);
				});
			});
			return comparators;
		}

		function getComparatorDisplayName(step, comparator) {
			var displayName = comparator.type;
			if (step.parameters && step.parameters.name) {
				displayName += ' ' + step.parameters.name;
			} else if (comparator.parameters && comparator.parameters.comparator) {
				displayName += ' ' + comparator.parameters.comparator;
			}
			return displayName;
		}
	}
});
