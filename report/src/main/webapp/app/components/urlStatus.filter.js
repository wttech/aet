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
  angularAMD.filter('aetUrlStatusFilter', UrlStatusFilter);

  /**
   * Filters collection of urls.
   * Return only those urls that have status matching applied status filter
   * (or all when no filter applied).
   */
  function UrlStatusFilter() {
    return filter;

    function filter(urls, statuses) {
      var filteredUrls = urls;
      if (statuses && statuses.length > 0) {
        filteredUrls = _.filter(urls, function (url) {
          return statuses.indexOf(url.getStatus()) > -1;
        });
      }
      return filteredUrls;
    }
  }
});
