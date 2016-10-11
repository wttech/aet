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
  angularAMD.factory('artifactsService', ArtifactsService);

  /**
   * Service responsible for fetching artifacts.
   */
  function ArtifactsService($q, $http, configService, requestParametersService) {
    var service = {
          getArtifactUrl: getArtifactUrl,
          getArtifact: getArtifact
        },
        requestParams = requestParametersService.get(),
        configParams = configService.getConfig();

    return service;

    function getArtifactUrl(artifactId) {
      return configParams.production + 'artifact?company=' + requestParams.company + '&project=' + requestParams.project + '&id=' + artifactId;
    }

    function getArtifact(artifactId) {
      var deferred = $q.defer(),
          url = getArtifactUrl(artifactId);

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
        handleFailed('Failed to load artifact!', exception);
      });
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
