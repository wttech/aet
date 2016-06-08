/*
 * Cognifide AET :: Job Common
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
package com.cognifide.aet.job.common.comparators.permormance.clientside.parser;

import com.cognifide.aet.job.common.comparators.permormance.clientside.report.ClientSidePerformanceReport;
import com.cognifide.aet.vs.ResultStatus;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

public class ClientSidePerformanceParser {

	public ClientSidePerformanceComparatorResultData parse(String json) {

		Type type = new TypeToken<ClientSidePerformanceReport>() {
		}.getType();
		ClientSidePerformanceReport report = new Gson().fromJson(json, type);

		ResultStatus result = getResultStatus(report.getPrettyOverallScore());
		return new ClientSidePerformanceComparatorResultData(result, report);
	}

	private ResultStatus getResultStatus(String overallScore) {
		ResultStatus result;
		switch (StringUtils.defaultString(overallScore)) {
			case "A":
				result = ResultStatus.SUCCESS;
				break;
			case "F":
				result = ResultStatus.FAILED;
				break;
			default:
				result = ResultStatus.WARNING;
		}
		return result;
	}
}
