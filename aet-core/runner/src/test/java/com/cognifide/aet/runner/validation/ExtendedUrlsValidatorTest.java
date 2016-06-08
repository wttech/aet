/*
 * Cognifide AET :: Runner
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
package com.cognifide.aet.runner.validation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.cognifide.aet.communication.api.config.ExtendedUrl;
import com.google.common.collect.Lists;

public class ExtendedUrlsValidatorTest {

	private List<ExtendedUrl> extendedUrls;

	@Before
	public void setUp() {
		extendedUrls = Lists.newArrayList();
		extendedUrls.add(new ExtendedUrl("http://www.test1.com", "http://www.test.com", "test"));
		extendedUrls.add(new ExtendedUrl("http://www.test4.com", "test", "test"));
	}

	@Test
	public void validate_UrlNameIsProvided_ErrorListIsEmpty() throws Exception {
		List<String> errors = ExtendedUrlsValidator.validate(extendedUrls);
		assertEquals(errors.size(), 0);
	}

	@Test
	public void validate_UrlNameIsNullTwice_ErrorListHasTwoErrors() {
		extendedUrls.add(new ExtendedUrl("http://www.test2.com", null, "test"));
		extendedUrls.add(new ExtendedUrl("http://www.test3.com", null, "test"));
		List<String> errors = ExtendedUrlsValidator.validate(extendedUrls);
		assertEquals(errors.size(), 2);
	}
}
