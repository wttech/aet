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
package com.cognifide.aet.job.common.comparators.w3chtml5.parser;

import com.cognifide.aet.job.common.comparators.w3c.W3cComparatorResult;
import com.cognifide.aet.job.common.comparators.w3c.W3cIssue;
import com.cognifide.aet.job.common.comparators.w3c.W3cIssueType;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.ResultStatus;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class W3cHtml5ValidationResultParser {

	public W3cComparatorResult parse(String json, ComparatorProperties properties) {

		Gson gson = new GsonBuilder()
				.registerTypeAdapter(W3cIssue.class, new W3cHtml5IssueDeserializer()).create();

		JsonArray messages = new JsonParser().parse(json).getAsJsonObject().getAsJsonArray("messages");
		Type list = new TypeToken<List<W3cIssue>>() {
		}.getType();
		List<W3cIssue> issues = gson.fromJson(messages, list);

		IssuesUtils utils = new IssuesUtils(issues).invoke();
		int errorCount = utils.getErrorCount();
		int warningCount = utils.getWarningCount();
		ResultStatus resultStatus = utils.getResultStatus();

		return new W3cComparatorResult(resultStatus, properties, errorCount,
				warningCount, null, issues, new ArrayList<W3cIssue>());
	}

	private static class IssuesUtils {

		private List<W3cIssue> issues;

		private int errorCount;

		private int warningCount;

		private ResultStatus resultStatus;

		private IssuesUtils(List<W3cIssue> issues) {
			this.issues = issues;
		}

		private int getErrorCount() {
			return errorCount;
		}

		private int getWarningCount() {
			return warningCount;
		}

		private ResultStatus getResultStatus() {
			return resultStatus;
		}

		private IssuesUtils invoke() {
			errorCount = Iterables.size(Iterables.filter(issues, new IssueTypePredicate(W3cIssueType.ERR)));
			warningCount = Iterables.size(Iterables.filter(issues, new IssueTypePredicate(W3cIssueType.WARN)));

			if (errorCount > 0) {
				resultStatus = ResultStatus.FAILED;
			} else if (warningCount > 0) {
				resultStatus = ResultStatus.WARNING;
			} else {
				resultStatus = ResultStatus.SUCCESS;
			}

			Collections.sort(issues, new Comparator<W3cIssue>() {
				@Override
				public int compare(W3cIssue i1, W3cIssue i2) {
					return i1.getIssueType().compareTo(i2.getIssueType());
				}
			});
			return this;
		}
	}

	private static class IssueTypePredicate implements Predicate<W3cIssue> {

		private final W3cIssueType issueType;

		private IssueTypePredicate(W3cIssueType issueType) {
			this.issueType = issueType;
		}

		@Override
		public boolean apply(W3cIssue issue) {
			return issueType == issue.getIssueType();
		}
	}
}
