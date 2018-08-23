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
define(['angularAMD', 'endpointConfiguration', 'requestParametersService'],
    function (angularAMD) {
      'use strict';
      angularAMD.factory('metadataEndpointService', MetadataEndpointService);

      /**
       * Service responsible for communication with AET metadata REST API endpoint.
       */
      function MetadataEndpointService($q, $http, endpointConfiguration,
          requestParametersService) {
        var service = {
              getMetadata: getMetadata,
              saveMetadata: saveMetadata
            },
            requestParams = requestParametersService.get(),
            endpoint = endpointConfiguration.getEndpoint();

        return service;

        function getMetadata() {
          var deferred = $q.defer(),
              url;

          if (requestParams.correlationId) {
            url = endpoint.getUrl + 'metadata' +
                '?company=' + requestParams.company +
                '&project=' + requestParams.project +
                '&correlationId=' + requestParams.correlationId;
          } else if (requestParams.version) {
            url = endpoint.getUrl + 'metadata' +
                '?company=' + requestParams.company +
                '&project=' + requestParams.project +
                '&suite=' + requestParams.suite +
                '&version=' + requestParams.version;
          }
          else {
            url = endpoint.getUrl + 'metadata' +
                '?company=' + requestParams.company +
                '&project=' + requestParams.project +
                '&suite=' + requestParams.suite;
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
            url: endpoint.getUrl + 'metadata',
            data: suite,
            headers: {
              /*
               * for cross-origin request (when working on localhost:9000 with Grunt)
               * special content type is required according to jQuery documentation at
               *     http://api.jquery.com/jquery.ajax/
               * see also:
               *     http://stackoverflow.com/a/30554385
               */
              'Content-Type': 'multipart/form-data; charset=UTF-8'
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
