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
package com.cognifide.aet.job.common.comparators.w3c;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.ResultStatus;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class W3cResponseParser {

	private static final W3cIssueComparator W3C_ISSUE_COMPARATOR = new W3cIssueComparator();

	private static final WarningNodeToW3cIssueFunction WARNING_TO_W3C_FUNCTION = new WarningNodeToW3cIssueFunction();

	private static final ErrorNodeToW3cIssueFunction ERROR_TO_W3C_FUNCTION = new ErrorNodeToW3cIssueFunction();

	private static final String STATUS_HEADER = "X-W3C-Validator-Status";

	private static final String WARNINGS_HEADER = "X-W3C-Validator-Warnings";

	private static final String ERRORS_HEADER = "X-W3C-Validator-Errors";

	public List<W3cIssue> parseHtml(String html) {
		Document doc = Jsoup.parse(html);
		List<W3cIssue> list = Lists.newArrayList();
		list.addAll(parseIssues(doc, "error_loop", ERROR_TO_W3C_FUNCTION));
		list.addAll(parseIssues(doc, "warnings", WARNING_TO_W3C_FUNCTION));
		Collections.sort(list, W3C_ISSUE_COMPARATOR);
		return list;
	}

	private Collection<W3cIssue> parseIssues(Document doc, String id, Function<Node, W3cIssue> function) {
		Element element = doc.getElementById(id);
		Collection<W3cIssue> list = Lists.newArrayList();
		if (element != null) {
			List<Node> nodes = element.childNodes();
			list = Collections2.transform(nodes, function);
			list = Collections2.filter(list, Predicates.notNull());
		}
		return list;
	}

	public W3cComparatorResult parseResult(final Map<String, List<String>> headers, String html,
										   ComparatorProperties properties, String validationOutputUrl, boolean ignoreWarnings)
			throws ProcessingException {
		int errorsCount = -1;
		int warningsCount = -1;
		ResultStatus status = ResultStatus.FAILED;
		if (checkHeaders(headers)) {
			errorsCount = Integer.parseInt(headers.get(ERRORS_HEADER).get(0));
			warningsCount = Integer.parseInt(headers.get(WARNINGS_HEADER).get(0));
			status = ResultStatus.SUCCESS;
			if (errorsCount > 0) {
				status = ResultStatus.FAILED;
			} else if (!ignoreWarnings && warningsCount > 0) {
				status = ResultStatus.WARNING;
			}
		}
		return new W3cComparatorResult(status, properties, errorsCount, warningsCount,
				validationOutputUrl, parseHtml(html), new ArrayList<W3cIssue>());
	}

	private boolean checkHeaders(final Map<String, List<String>> headers) {
		Set<String> headersNames = Sets.newHashSet(ERRORS_HEADER, WARNINGS_HEADER);
		Set<String> abortedValues = Sets.newHashSet("Aborted", "Abort");
		List<String> statusHeaderValue = headers.get(STATUS_HEADER);
		return statusHeaderValue != null && !abortedValues.contains(statusHeaderValue.get(0))
				&& headers.keySet().containsAll(headersNames);
	}

}
