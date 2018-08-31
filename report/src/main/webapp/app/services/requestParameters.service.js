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
define(['angularAMD'], function (angularAMD) {
  'use strict';
  angularAMD.factory('requestParametersService', RequestParametersService);

  function RequestParametersService() {

    var service,
        parameters;

    service = {
      get: get
    };

    return service;

    function get() {
      if (!parameters) {
        parameters = extract();
      }
      return parameters;
    }

    function extract() {
      var result = {},
          location = window.location.search,
          allParametersList = location.replace('?', '').split('&'),
          allParameters = {};

      _.forEach(allParametersList, function (entry) {
        var pair = entry.split('=');
        allParameters[pair[0]] = pair[1];
      });

      result.company = allParameters.company ? allParameters.company : null;
      result.project = allParameters.project ? allParameters.project : null;
      result.suite = allParameters.suite ? allParameters.suite : null;
      result.version = allParameters.version ? allParameters.version : null;
      result.correlationId =
          allParameters.correlationId ? allParameters.correlationId : null;
      return result;
    }
  }
});
