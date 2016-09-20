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
/**
 * @deprecated use RequestParametersService instead
 */
define(['angularAMD'], function (angularAMD) {
	'use strict';

	angularAMD.factory('suiteParamsService', function () {
		
		var location = window.location.search,
			params = location.replace('?', '').split('&'),
			paramsObj = {};

		return {

			setParams: function() {

				/*
				 Splitting window.location.search and converting it to object, resulting in paramsObj
				 */
				for (var i = 0; i < params.length; i++) {
					paramsObj[i] = params[i].split('=');
				}

				_.forEach(paramsObj, function (val) {
					paramsObj[val[0]] = val[1];

				});
				_.filter(paramsObj, function (item, val) {
					if ($.type(item) == 'array') {
						delete paramsObj[val];
					}
				});

				paramsObj.suite = _.isUndefined(paramsObj.suite) ? paramsObj.correlationId : paramsObj.suite;
			},

			getParams: function() {
				return paramsObj;
			}
		};
	});

});
