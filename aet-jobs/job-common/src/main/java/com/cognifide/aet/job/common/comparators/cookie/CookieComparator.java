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
package com.cognifide.aet.job.common.comparators.cookie;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.Cookie;

import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.cookie.CookieCollectorResult;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ResultStatus;
import com.cognifide.aet.vs.VersionStorageException;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

/**
 * @author lukasz.wieczorek
 * 
 */
public class CookieComparator implements ComparatorJob {

	public static final String COMPARATOR_TYPE = "cookie";

	public static final String COMPARATOR_NAME = "cookie";

	private static final Function<Cookie, String> COOKIE_STRING_FUNCTION = new Function<Cookie, String>() {

		@Override
		public String apply(Cookie input) {
			String cookieName = null;
			if (input != null) {
				cookieName = input.getName();
			}
			return cookieName;
		}
	};

	private static final String ACTION_PARAMETER = "action";

	private static final String NAME_PARAMETER = "cookie-name";

	private static final String VALUE_PARAMETER = "cookie-value";

	private static final String SHOW_MATCHED = "showMatched";

	private final Node resultNode;

	private final Node dataNode;

	private final Node patternNode;

	private final ComparatorProperties comparatorProperties;

	private CompareAction compareAction;

	private String name;

	private String value;

	private boolean showMatched = true;

	public CookieComparator(Node dataNode, Node resultNode, Node patternNode,
			ComparatorProperties comparatorProperties) {
		this.dataNode = dataNode;
		this.resultNode = resultNode;
		this.patternNode = patternNode;
		this.comparatorProperties = comparatorProperties;
	}

	@Override
	public Boolean compare() throws ProcessingException {
		Boolean compareResult = null;
		try {
			CookieCollectorResult dataResult = dataNode.getResult(CookieCollectorResult.class);

			switch (compareAction) {
				case COMPARE:
					CookieCollectorResult patternResult = patternNode.getResult(CookieCollectorResult.class);
					compareResult = compareCookies(dataResult, patternResult);
					break;
				case TEST:
					testCookie(dataResult, name, value);
					break;
				default:
					listCookies(dataResult);
			}
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		}
		return compareResult;
	}

	private boolean compareCookies(CookieCollectorResult dataResult, CookieCollectorResult patternResult)
			throws VersionStorageException {
		Set<String> dataCookieNames = Sets.newHashSet(Collections2.transform(dataResult.getCookies(),
				COOKIE_STRING_FUNCTION));
		Set<String> patternCookieNames = Sets.newHashSet(Collections2.transform(patternResult.getCookies(),
				COOKIE_STRING_FUNCTION));

		Set<String> additionalCookies = Sets.difference(dataCookieNames, patternCookieNames);
		Set<String> notFoundCookies = Sets.difference(patternCookieNames, dataCookieNames);
		Set<String> foundCookies = Collections.emptySet();
		if (showMatched) {
			foundCookies = Sets.intersection(patternCookieNames, dataCookieNames);
		}
		boolean compareResult = additionalCookies.isEmpty() && notFoundCookies.isEmpty();
		ResultStatus resultStatus = ResultStatus.fromBoolean(compareResult);
		CookieCompareComparatorResult result = new CookieCompareComparatorResult(resultStatus,
				comparatorProperties, compareAction, dataResult.getCookies(), notFoundCookies,
				additionalCookies, foundCookies);

		resultNode.saveResult(result);
		return compareResult;
	}

	private void testCookie(CookieCollectorResult dataResult, String cookieName, String cookieValue)
			throws VersionStorageException {
		boolean testResult = false;
		for (Cookie cookie : dataResult.getCookies()) {
			if (cookie.getName().equals(cookieName)
					&& (cookieValue == null || cookieValue.equals(cookie.getValue()))) {
				testResult = true;
				break;
			}
		}
		CookieTestComparatorResult result = new CookieTestComparatorResult(
				ResultStatus.fromBoolean(testResult), comparatorProperties, compareAction,
				dataResult.getCookies(), cookieName, cookieValue);

		resultNode.saveResult(result);
	}

	private void listCookies(CookieCollectorResult dataResult) throws VersionStorageException {
		CookieComparatorResult result = new CookieComparatorResult(ResultStatus.SUCCESS,
				comparatorProperties, compareAction, dataResult.getCookies());

		resultNode.saveResult(result);
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		compareAction = CompareAction.fromString(params.get(ACTION_PARAMETER));
		name = params.get(NAME_PARAMETER);
		value = params.get(VALUE_PARAMETER);
		if (CompareAction.TEST.equals(compareAction) && name == null) {
			String message = String.format("Missing %s", NAME_PARAMETER);
			throw new ParametersException(message);
		}
		if (params.containsKey(SHOW_MATCHED)) {
			this.showMatched = Boolean.valueOf(params.get(SHOW_MATCHED));
		}
	}

}
