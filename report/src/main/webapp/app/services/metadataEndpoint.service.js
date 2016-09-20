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
define(['angularAMD', 'configService', 'requestParametersService'], function (angularAMD) {
	'use strict';
	angularAMD.factory('metadataEndpointService', MetadataEndpointService);

	/**
	 * Service responsible for communication with AET metadata REST API endpoint.
	 */
	function MetadataEndpointService($q, $http, configService, requestParametersService) {
		var service = {
				getMetadata: getMetadata,
				saveMetadata: saveMetadata
			},
			requestParams = requestParametersService.get(),
			configParams = configService.getConfig();

		return service;

		function getMetadata() {
			var deferred = $q.defer(),
				url;

			if (requestParams.correlationId) {
				url = configParams.production + 'metadata?company=' + requestParams.company + '&project=' + requestParams.project + '&correlationId=' + requestParams.correlationId;
			}
			else {
				url = configParams.production + 'metadata?company=' + requestParams.company + '&project=' + requestParams.project + '&suite=' + requestParams.suite;
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
			}).catch(function (exception) {
				handleFailed('Failed to load report data!', exception);
			});
		}

		function saveMetadata(suite) {
			var deferred = $q.defer();
			
			$http({
				method: 'POST',
				url: configParams.production + 'metadata',
				data: suite,
				headers: {
					'Content-Type': 'application/json'
				}
			}).then(function (data) {
				deferred.resolve(data.data);
			}).catch(function (e) {
				deferred.reject(e.data.message);
			});

			return deferred.promise;
		}

		/***************************************
		 ***********  Private methods  *********
		 ***************************************/

		function handleFailed(text, exception) {
			console.error(text, requestParams, exception);
			alert(text);
		}


	}
});
