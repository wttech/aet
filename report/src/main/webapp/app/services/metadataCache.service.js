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
define(['angularAMD', 'localStorageService'], function (angularAMD) {
  'use strict';
  angularAMD.factory('metadataCacheService', MetadataCacheService);

  /**
   * Service responsible for caching changes made ba user before committing them.
   * @param localStorageService - local session storage service
   */
  function MetadataCacheService(localStorageService) {
    var service = {
      put: put,
      getLatest: getLatest,
      getVersion: getVersion,
      clearVersion: clearVersion,
      clearAllVersions: clearAllVersions
    };

    return service;

    function put(suite) {
      localStorageService.put(getFullSuiteKey(suite), suite);
    }

    /**
     * Return latest (max) version of cached suite or null if there is no suite.
     */
    function getLatest(company, project, name) {
      var suiteKeyWithoutVersion = keyWith(company, project, name);
      var suiteKeys = localStorageService.getAllKeysWithPrefix(
          suiteKeyWithoutVersion);
      var maxKeyVersion = -1;

      _.forEach(suiteKeys, function (suiteVersionKey) {
        var currentVersion = getVersionFromKey(suiteVersionKey);
        if (currentVersion > maxKeyVersion) {
          maxKeyVersion = currentVersion;
        }
      });

      return localStorageService.get(
          keyWith(company, project, name, maxKeyVersion));
    }

    /**
     * Returns suite for given version or null with no suite present.
     */
    function getVersion(company, project, name, version) {
      return localStorageService.get(keyWith(company, project, name, version));
    }

    function clearVersion(suite) {
      localStorageService.remove(getFullSuiteKey(suite));
    }

    /**
     * Removes all cache entries for given suite.
     */
    function clearAllVersions(company, project, name) {
      var suiteKeyWithoutVersion = keyWith(company, project, name);
      var suiteKeys = localStorageService.getAllKeysWithPrefix(
          suiteKeyWithoutVersion);
      _.forEach(suiteKeys, function (suiteVersionKey) {
        localStorageService.remove(suiteVersionKey);
      });
    }

    /***************************************
     ***********  Private methods  *********
     ***************************************/

    function getFullSuiteKey(suiteObject) {
      return keyWith(suiteObject.company, suiteObject.project, suiteObject.name,
          suiteObject.version);
    }

    function keyWith() {
      return 'aet:' + Array.from(arguments).join('|');
    }

    function getVersionFromKey(cacheKey) {
      var parts = cacheKey.split('|');
      return parts.length == 4 ? parts[3] : null;
    }
  }
});
