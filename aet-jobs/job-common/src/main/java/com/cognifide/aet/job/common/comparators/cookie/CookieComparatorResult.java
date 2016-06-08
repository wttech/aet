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

import java.util.HashSet;
import java.util.Set;

import org.openqa.selenium.Cookie;

import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.ComparatorResult;
import com.cognifide.aet.vs.ResultStatus;

/**
 * @author lukasz.wieczorek
 */
public class CookieComparatorResult extends ComparatorResult {

	private static final long serialVersionUID = -2046910588665582784L;

	private final CompareAction compareAction;

	private final HashSet<Cookie> cookies;

	public CookieComparatorResult(ResultStatus status, ComparatorProperties properties,
			CompareAction compareAction, Set<Cookie> cookies) {
		super(status, properties, compareAction == CompareAction.COMPARE);
		this.compareAction = compareAction;
		this.cookies = new HashSet<>(cookies);
	}

	public CompareAction getCompareAction() {
		return compareAction;
	}

	public Set<Cookie> getCookies() {
		return cookies;
	}

}
