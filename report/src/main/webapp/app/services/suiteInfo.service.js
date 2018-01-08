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
define(['angularAMD', 'metadataService'], function (angularAMD) {
  'use strict';
  angularAMD.factory('suiteInfoService', SuiteInfoService);

  /**
   * Service responsible for serving base suite information.
   */
  function SuiteInfoService(metadataService) {
    var service = {
          getInfo: getInfo
        },
        suiteInfo;

    return service;

    /**
     * return object with following information:
     * - name - name of the suite,
     * - correlationId - id of suite run,
     * - company - name of the company,
     * - project - name of the project,
     * - version - version of suite run,
     * - lastUpdate - timestamp of last suite update
     */
    function getInfo() {
      var suite;
      if (!suiteInfo) {
        suite = metadataService.getSuite();
        suiteInfo = {
          correlationId: suite.correlationId,
          patternCorrelationId: suite.patternCorrelationId,
          company: suite.company,
          project: suite.project,
          name: suite.name,
          version: suite.version,
          lastUpdate: suite.runTimestamp,
          duration: suite.statistics ? suite.statistics.duration
              : 'Not available'
        };
      }
      return suiteInfo;
    }

  }
});
