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
define(['angularAMD', 'configService', 'cachingService', 'suiteParamsService'], function (angularAMD) {
	'use strict';
	angularAMD.factory('apiServices', function ($q, $http, $rootScope, cachingService, configService, suiteParamsService) {
		var suiteParams = suiteParamsService.getParams(),
			configParams = configService.getConfig();

		return {
			getRawMetadata: function (company, project, suite, correlationId) {
				var deferred = $q.defer(),
					url;

				if (correlationId) {
					url = configParams.production + 'metadata?company=' + company + '&project=' + project + '&suite=' + suite + '&correlationId=' + correlationId;
				}
				else {
					url = configParams.production + 'metadata?company=' + company + '&project=' + project + '&suite=' + suite;
				}

				return $http({
					method: 'GET',
					url: url,
					headers: {
						'Content-Type': 'text/plain'
					}
				}).then(function (data) {
					deferred.resolve(data.data);
					return deferred.promise;
				}).catch(function (e) {
					alert(e.statusText);
				});
			},
			getMetadata: function (company, project, suite, correlationId) {
				var deferred = $q.defer(),
					url;

				if (correlationId) {
					url = configParams.production + 'metadata?company=' + company + '&project=' + project + '&suite=' + suite + '&correlationId=' + correlationId;
				}
				else {
					url = configParams.production + 'metadata?company=' + company + '&project=' + project + '&suite=' + suite;
				}

				return $http({
					method: 'GET',
					url: url,
					headers: {
						'Content-Type': 'text/plain'
					}
				}).then(function (data) {
					var cachedData = cachingService.getSuiteData(company, project, suite);
					if (cachedData !== null && data.data.version <= cachedData.version) {
						deferred.resolve(cachedData);
					} else {
						deferred.resolve(data.data);
						cachingService.revertChanges(company, project, suite);
					}
					return deferred.promise;
				}).catch(function (e) {
					alert(e.statusText);
				});
			},
			processData: function (data) {
				var tests,
					status;

				if (data.data) {
					tests = data.data.tests;
					if (!data.data.changesCount) {
						data.data.changesCount = 0;
					}
				}
				else {
					tests = data.tests;
					if (!data.changesCount) {
						data.changesCount = 0;
					}
				}
				_.forEach(tests, function (test) {
					var testHasErrors = false,
						testHasWarnings = false,
						testHasRebases = false;
					test.status = 'PASSED';
					_.forEach(test.urls, function (url) {
						var urlHasErrors = false,
							urlHasWarnings = false,
							urlHasRebases = false;
						url.status = 'PASSED';
						_.forEach(url.steps, function (step) {
							if (step.comment) {
								url.hasComments = true;
							}
							if (step.comparators) {
								step.status = status;
								_.forEach(step.comparators, function (comparator) {
									if (!comparator.stepResult) {
										return;
									}
									var status = comparator.stepResult.status;
									if (!status || status === 'PROCESSING_ERROR' || status === 'FAILED') {
										step.status = 'FAILED';
										urlHasErrors = true;
									} else if (status === 'WARNING') {
										urlHasWarnings = true;
									} else if (status === 'REBASED') {
										urlHasRebases = true;
									}
								});
							}
						});
						if (urlHasErrors) {
							url.status = 'FAILED';
							testHasErrors = true;
						} else {
							if(urlHasRebases) {
								url.status = 'REBASED';
								testHasRebases = true;
							} else if (urlHasWarnings) {
								url.status = 'WARNING';
								testHasWarnings = true;
							}
						}
						if (url.comments) {
							url.hasComments = true;
							test.hasComments = true;
						}
					});
					if (testHasErrors) {
						test.status = 'FAILED';
					} else {
						if(testHasRebases) {
							test.status = 'REBASED';
						} else if (testHasWarnings) {
							test.status = 'WARNING';
						}
					}
					if (test.comments) {
						test.hasComments = true;
					}
				});
				data.tests = tests;

				var dataParams = data.data ? data.data : data;

				cachingService.storeSuiteData(suiteParams.company, suiteParams.project, suiteParams.suite, dataParams);

				setTimeout(function () {
					$rootScope.$broadcast('metaLoaded');
					$rootScope.$apply();
				});
			},
			getArtifact: function (company, project, id) {
				var deferred = $q.defer();
				return $http({
					method: 'GET',
					url: configParams.production + 'artifact?company=' + company + '&project=' + project + '&id=' + id
				}).then(function (data) {
					deferred.resolve(data.data);
					return deferred.promise;
				});
			},
			sendData: function () {

				return $http({
					method: 'POST',
					url: configParams.production + 'metadata',
					data: cachingService.getSuiteData(suiteParams.company, suiteParams.project, suiteParams.suite),
					headers: {
						'Content-Type': 'application/json'
					}
				}).then(function () {

					alert('Your suite has been updated!');

					cachingService.revertChanges(suiteParams.company, suiteParams.project, suiteParams.suite);
					window.location.reload();

				}).catch(function (e) {
					alert(e.data.message);
				});
			}
		};
	});
});