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
define(['apiServices', 'cachingService', 'configService', 'tabStateService', 'suiteParamsService', 'userSettingsService'], function () {
	'use strict';

	return ['$scope',
		'$http',
		'$stateParams',
		'$rootScope',
		'apiServices',
		'cachingService',
		'configService',
		'tabStateService',
		'$timeout',
		'suiteParamsService',
		'$filter',
		'patternsService',
		'userSettingsService',
		'metadataAccessService',
		'viewModeService',
		'$uibModal',
		'notesService',
		function ($scope,
				  $http,
				  $stateParams,
				  $rootScope,
				  apiServices,
				  cachingService,
				  configService,
				  tabStateService,
				  $timeout,
				  suiteParamsService,
				  $filter,
				  patternsService,
				  userSettingsService,
				  metadataAccessService,
				  viewModeService,
				  $uibModal,
				  notesService) {

			var suiteParams = suiteParamsService.getParams(),
				configParams = configService.getConfig(),
				rebaseableCount = 0,
				compareIndex = 0,
				resultId = null,
				data = null;

			$scope.testName = $stateParams.url;
			$scope.viewTest = true;
			$scope.reportComment = false;
			$scope.reportCommentText = '';

			var updateUrlView = function() {
				var comparator = getCurrentContext().comparator;

				$scope.comparatorPatternMayBeAccepted = comparator.stepResult.status === 'FAILED' && comparator.stepResult.rebaseable;
				$scope.comparatorPatternMayBeReverted = comparator.hasNotSavedChanges;
				$scope.comparatorCurrentStatus = comparator.stepResult.status;
			};

			$rootScope.$on('metadata:changed', updateUrlView);

			var getReport = function (reports, reportName) {
				var results = reports.filter(function (report) {
					return report.name === reportName;
				});
				return results.length > 0 ? results[0] : undefined;
			};

			var getUrlWithDomainAndName = function (report, testResultName) {
				var result = null;
				if (!report) {
					return result;
				}

				var foundTestResults = report.urls.filter(function (testResult) {
					return testResult.name === testResultName || testResult.url === testResultName;
				});
				if (foundTestResults.length > 0) {
					result = {
						url: foundTestResults[0].domain + foundTestResults[0].url,
						name: foundTestResults[0].name
					}
				}

				return result;
			};

			var processTestData = function () {
				$timeout(function () {
					var suite = cachingService.getSuiteData(suiteParams.company, suiteParams.project, suiteParams.suite);
					if (suite === null || $scope.report) {
						console.warn('no data to display');
						return false;
					}
					$scope.report = getReport(suite.tests, $stateParams.test);
					var urlWithDomainAndName = getUrlWithDomainAndName($scope.report, $stateParams.url);
					$scope.testUrl = urlWithDomainAndName.url;
					$scope.testName = urlWithDomainAndName.name;
					groupTests();
					tabStateService.bindTabState(tabStateService);
				});
			};

			$('[data-toggle="popover"]').popover({
				placement: 'bottom'
			});

			$scope.getTab = function (e) {
				e.preventDefault();
			};

			$rootScope.$on('reassignData', function () {
				data = cachingService.getSuiteData(suiteParams.company, suiteParams.project, suiteParams.suite);
			});

			$rootScope.$on('metaLoaded', processTestData);
			$scope.$on('$viewContentLoaded', processTestData);

			$scope.getTestCase = function (e, type, testCaseItem, layoutVersion, index) {
				e.preventDefault();
				$(e.target).tab('show');
				tabStateService.bindTabState(tabStateService);
				$('.test-tabs li').not($(e.target).parent()).removeClass('active');

				compareIndex = index !== undefined && index !== '' && testCaseItem.comparators[index] ? index : 0;

				$scope.comparatorIndex = compareIndex;

				$scope.currentTab = layoutVersion !== undefined && layoutVersion !== '' ? layoutVersion : type;

				$scope.currentComparator = getCurrentContext().comparator;
				$scope.oldCommentModel = testCaseItem;

				if (testCaseItem.comment) {
					$scope.reportComment = true;
					$rootScope.testCaseCommentText = testCaseItem.comment;
				} else {
					$scope.reportComment = false;
					$rootScope.testCaseCommentText = '';
				}

				$('[data-toggle="popover"]').popover({
					placement: 'bottom'
				});

				$scope.comparatorsError = [];
				$scope.processingErrorText = [];

				$scope.processingError = false;

				updateUrlView();

				if (testCaseItem.comparators[compareIndex].stepResult && testCaseItem.comparators[compareIndex].stepResult.errors) {
					$scope.processingError = true;
					for (var errorComparator = 0; errorComparator < testCaseItem.comparators[compareIndex].stepResult.errors.length; errorComparator++) {
						$scope.comparatorsError.push(testCaseItem.comparators[compareIndex].stepResult.errors[errorComparator]);
					}
					$rootScope.hideTestLoader = true;
					return false;
				}

				if (testCaseItem.status == 'PROCESSING_ERROR' && testCaseItem.stepResult.errors) {
					$scope.processingError = true;
					for (var errorStep = 0; errorStep < testCaseItem.stepResult.errors.length; errorStep++) {
						$scope.processingErrorText.push(testCaseItem.stepResult.errors[errorStep]);
					}
					$rootScope.hideTestLoader = true;
					return false;
				}

				var currentComparatorHasAcceptedPattern = getCurrentContext().comparator.stepResult.status === 'REBASED';
				if (type === 'layout') {

					if (testCaseItem.comparators[0].stepResult && testCaseItem.comparators[compareIndex].stepResult.data) {
						$scope.patternCollect = testCaseItem.comparators[compareIndex].stepResult.data.patternTimestamp;
						$scope.newCollect = testCaseItem.comparators[compareIndex].stepResult.data.collectTimestamp;
					}
					$scope.mask = '';
					if (testCaseItem.status === 'PASSED' || currentComparatorHasAcceptedPattern) {
						$scope.pattern = '';
						$scope.new = '';
						$scope.design = configParams.production + 'artifact?company=' + suiteParams.company + '&project=' + suiteParams.project + '&id=' + testCaseItem.stepResult.artifactId;
					} else {
						$scope.pattern = configParams.production + 'artifact?company=' + suiteParams.company + '&project=' + suiteParams.project + '&id=' + testCaseItem.pattern;

						if (testCaseItem.stepResult.artifactId !== undefined) {
							$scope.new = configParams.production + 'artifact?company=' + suiteParams.company + '&project=' + suiteParams.project + '&id=' + testCaseItem.stepResult.artifactId;
						}
						if (testCaseItem.comparators[compareIndex].stepResult.artifactId !== undefined) {
							$scope.mask = configParams.production + 'artifact?company=' + suiteParams.company + '&project=' + suiteParams.project + '&id=' + testCaseItem.comparators[compareIndex].stepResult.artifactId;
						}

						$scope.design = '';
					}

				} else {
					if (testCaseItem.type === 'client-side-performance') {
						resultId = testCaseItem.stepResult.artifactId;
					} else if (
						testCaseItem.type === 'accessibility'
						&& (testCaseItem.status === 'FAILED'
								|| testCaseItem.status === 'REBASED'
								||	(testCaseItem.status === 'PASSED'
										|| testCaseItem.status === 'WARNING'
										&& testCaseItem.comparators[0].stepResult.data !== undefined
										&& testCaseItem.comparators[0].stepResult.data.warningCount > 0
									)
							)
						&& testCaseItem.comparators[0].stepResult.artifactId) {

						resultId = testCaseItem.comparators[0].stepResult.artifactId;
					} else if (testCaseItem.comparators[compareIndex].stepResult.artifactId) {
						resultId = testCaseItem.comparators[compareIndex].stepResult.artifactId;
					} else {
						resultId = testCaseItem.stepResult.artifactId;
					}

					if (type === 'source' && (testCaseItem.comparators[compareIndex].stepResult.status === 'PASSED' || currentComparatorHasAcceptedPattern)) {
						$scope.source = '';
					} else {
						apiServices.getArtifact(suiteParams.company, suiteParams.project, resultId).then(function (data) {

							switch (type) {
								case 'accessibility':
									$scope.accessibility = data;
									break;
								case 'js-errors':
									if (_.isEmpty(data) && testCaseItem.comparators[compareIndex].filters) {
										$scope.jsFilters = [];
										$scope.jsFilters.push(testCaseItem.comparators[compareIndex].filters[0].parameters);
									}
									$scope.js = data;
									break;
								case 'w3c':
									if ($.type(data) === 'string') {
										$scope.w3c = '';
									} else {
										$scope.w3c = data;
									}
									break;
								case 'source':
									if ($.type(data) === 'string') {
										$scope.source = '';
									} else {
										$scope.source = data;
									}
									break;
								case 'client-side-performance':
									$scope.performance = data;
									break;
								case 'cookie':
									$scope.cookies = data;
									break;
								case 'status-codes':
									$scope.statuscodes = data;
									break;
							}
							$scope.comparatorData = testCaseItem.comparators[compareIndex];
						}).catch(function (e) {
							console.log(e);
						});
					}
				}

				$timeout(function () {
					$rootScope.hideTestLoader = true;
					$scope.$apply();
					$('.test-url').not('.is-active').closest('.url-name').removeClass('is-active');
					$('.test-url.is-active').closest('.url-name').addClass('is-active');
					$('.test-tabs').mCustomScrollbar({
						axis: 'x',
						theme: 'dark-thin',
						advanced: {
							autoExpandHorizontalScroll: true,
							updateOnContentResize: true
						},
						callbacks: {
							onCreate: function () {
								if (!$('.mCS_no_scrollbar_x').length) {
									$('.tabs, .tab-content-toolbar').addClass('slider-init');
								}
							}
						}
					}).mCustomScrollbar('scrollTo', $('.test-tabs').find('.active'));
				});
			};

			var groupTests = function () {
				data = cachingService.getSuiteData(suiteParams.company, suiteParams.project, suiteParams.suite);
				var selectedTests = data.tests,
					urlResults,
					currentIndex,
					urlList,
					nest,
					grouppedTests,
					steps;

				if (selectedTests === null) {
					return false;
				}
				$scope.test = selectedTests.filter(function (el) {
					return el.name === $stateParams.test;
				})[0];

				var urlsAfterFiltering = $filter('aetUrlStatusFilter')($scope.test.urls, $rootScope.activeFilters.status);
				urlResults = urlsAfterFiltering;

				urlResults = urlResults.filter(function (el) {
					return el.name === $stateParams.url;
				});

				if (_.isEmpty(urlResults)) {
					return false;
				}

				steps = urlResults[0].steps;

				currentIndex = _.indexOf(urlsAfterFiltering, urlResults[0]);
				urlList = urlsAfterFiltering;

				$rootScope.previousURL = _.isUndefined(urlList[currentIndex - 1]) ? null : urlList[currentIndex - 1].url;
				$rootScope.nextURL = _.isUndefined(urlList[currentIndex + 1]) ? null : urlList[currentIndex + 1].url;

				tabStateService.bindTabState(tabStateService);

				$scope.rebaseableUrl = false;

				steps = steps.filter(function (el) {
					return el.comparators !== undefined;
				});

				urlResults[0].status === 'REBASED' ? $scope.rebasedUrl = true : $scope.rebasedUrl = false;

				_.forEach(steps, function (v) {
					if (v.status !== 'PASSED' && v.status !== 'REBASED') {
						for (var k = 0; k < v.comparators.length; k++) {
							if (k === v.comparators.length) {
								break;
							}
							if (v.comparators[k].stepResult && v.comparators[k].stepResult.rebaseable && v.comparators[k].stepResult.status !== 'REBASED') {
								rebaseableCount++;
							}
						}
					}
				});

				$scope.rebaseableUrl = (rebaseableCount > 0 || (urlResults[0].status === 'REBASED' && urlResults[0].previousStatus));

				nest = function (seq, keys) {
					if (!keys.length) {
						return seq;
					}
					var first = keys[0],
						rest = keys.slice(1);

					return _.mapValues(_.groupBy(seq, first), function (value) {
						return nest(value, rest);
					});
				};

				grouppedTests = nest(urlResults[0].steps, ['type', 'type']);

				if (urlResults[0].comment) {
					$rootScope.urlComment = true;
					$rootScope.urlCommentText = urlResults[0].comment;
				} else {
					$rootScope.urlComment = false;
					$rootScope.urlCommentText = '';
				}

				grouppedTests = _.omit(grouppedTests, ['click', 'login', 'header', 'modify-cookie', 'wait-for-page-loaded', 'sleep', 'resolution', 'open', 'hide']);

				$scope.testCase = grouppedTests;

				$timeout(function () {
					$scope.$apply();
					tabStateService.forceTabState();
				});
			};

			$scope.addNewComment = function (level) {
				$rootScope.commentType = level;
			};

			$scope.removeComment = function (level) {
				$rootScope.commentType = level;
				$rootScope.comment = '';
				$scope.rebase('e', 'addComment', null, level);
			};

			$rootScope.$on('saveTestComment', function (args, val) {
				$rootScope.comment = val.text;
				$scope.rebase('e', 'addComment', val.text, val.level);
			});

			$rootScope.$on('saveReportComment', function (args, val) {
				$rootScope.comment = val.text;
				$scope.rebase('e', 'addComment', val.text, val.level);
			});

			var rebaseStep = function (data, url, urlIndex, index, step, suiteParams) {
				// new api call
				patternsService.updateCase($stateParams.test, $stateParams.url, step.index, compareIndex);
			};

			var getCurrentContext = function() {
				var test, url, step, comparator;

				test = metadataAccessService.getTest($stateParams.test);
				url = metadataAccessService.getUrl($stateParams.test, $stateParams.url);
				step = url.steps.filter(function (el) {
					return el.name === $scope.currentTab;
				})[0];
				comparator = step.comparators[compareIndex];

				return {
					test: test,
					url: url,
					step: step,
					comparator: comparator
				};
			};


			$scope.displayCommentModal = function () {
				var context = getCurrentContext();

				$uibModal.open({
					animation: true,
					templateUrl: 'app/layout/modal/note/noteModal.view.html',
					controller: 'noteModalController',
					controllerAs: 'noteModal',
					resolve: {
						model: function () {
							return context.comparator;
						},
						viewMode: function () {
							return viewModeService.COMPARATOR;
						},
						notesService: function () {
							return notesService;
						}
					}
				});
			};

			var getCurrentContext = function(){
				var test, url, step, comparator;

				test = metadataAccessService.getTest($stateParams.test);
				url = metadataAccessService.getUrl($stateParams.test, $stateParams.url);
				step = url.steps.filter(function (el) {
					return el.name === $scope.currentTab;
				})[0];
				comparator = step.comparators[compareIndex];

				return {
					test: test,
					url: url,
					step: step,
					comparator: comparator
				};
			}

			var cancelRebaseStep = function (data, url, urlIndex, index, step, suiteParams) {
				patternsService.revertCase($stateParams.test, $stateParams.url, step.index, compareIndex);
			};

			var addComment = function (url, comment, level, step, data, index, urlIndex, suiteParams) {

				if (data.changesCount) {
					data.changesCount--;
					$rootScope.hasChanges--;
				} else {
					data.changesCount++;
					$rootScope.hasChanges++;
				}

				if (level === 'url') {
					url.comment = comment;
					$rootScope.urlCommentText = comment;

					if (!comment) {
						$rootScope.urlComment = false;
					} else {
						$rootScope.urlComment = true;
					}
				} else if (level === 'report') {

					if (!comment) {
						$scope.reportComment = false;
						url.hasComments = false;
					} else {
						$scope.reportComment = true;
						url.hasComments = true;
					}
					step.comment = comment;
					$rootScope.testCaseCommentText = comment;
				}

				data.tests[index].urls[urlIndex] = url;

				storeChanges(data, suiteParams);
			};

			var storeChanges = function (data, suiteParams) {

				cachingService.storeChangedSuiteData(suiteParams.company, suiteParams.project, suiteParams.suite, data);
				$timeout(function () {
					$scope.$apply();
					$rootScope.$apply();
				});
			};

			$scope.toggleMask = function () {
				$rootScope.maskVisible = userSettingsService.toggleScreenshotMask();
			};

			$scope.toggleFullSource = function () {
				$rootScope.fullSourceVisible = userSettingsService.toggleFullSource();
			};

			$scope.rebase = function (e, action, comment, level) {

				var currentData = cachingService.getSuiteData(suiteParams.company, suiteParams.project, suiteParams.suite),
					tests = currentData.tests,
					testName = $scope.test.name,
					index = _.indexOf(tests, _.find(tests, {'name': testName})),
					urls = tests[index].urls,
					url,
					urlIndex,
					steps,
					step;

				url = urls.filter(function (el) {
					return el.name === $stateParams.url;
				})[0];

				urlIndex = _.indexOf(urls, _.find(urls, {'url': $stateParams.url}));


				if (_.isUndefined(url)) {
					return false;
				}

				steps = url.steps;

				if ($scope.currentTab === 'w3c') {
					$scope.currentTab = 'source';
				}

				step = steps.filter(function (el) {
					return el.name === $scope.currentTab;
				})[0];

				switch (action) {
					case 'rebaseStep':
						rebaseStep(data, url, urlIndex, index, step, suiteParams);
						break;
					case 'cancelRebaseStep':
						cancelRebaseStep(data, url, urlIndex, index, step, suiteParams);
						break;
					case 'addComment':
						addComment(url, comment, level, step, data, index, urlIndex, suiteParams);
						break;
				}
			};
		}];
});
