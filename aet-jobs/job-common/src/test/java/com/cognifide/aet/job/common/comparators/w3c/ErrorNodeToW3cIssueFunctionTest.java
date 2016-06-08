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

import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

public class ErrorNodeToW3cIssueFunctionTest {

	private ErrorNodeToW3cIssueFunction tested;

	@Before
	public void setUp() throws Exception {
		tested = new ErrorNodeToW3cIssueFunction();
	}
	
	@Test
	public void testApply() throws Exception {
		URL url = getClass().getClassLoader().getResource("mock/W3cComparator/node.html");
		
		String nodeString = IOUtils.toString(url,"UTF-8");
		Element node = Jsoup.parseBodyFragment(nodeString).getElementsByTag("li").first();

 		W3cIssue issue = tested.apply(node);
		
		assertThat(issue.getIssueType(),Matchers.is(W3cIssueType.ERR));
		assertThat(issue.getLine(),Matchers.is(384));
		assertThat(issue.getColumn(),Matchers.is(1));
		assertThat(issue.getMessage(),Matchers.is("Saw &lt; when expecting an attribute name. Probable cause: Missing &gt; immediately before."));
		assertThat(issue.getCode1(),Matchers.is(""));
		assertThat(issue.getCode2(),Matchers.is("/body&gt;"));
		assertThat(issue.getErrorPosition(),Matchers.is("<"));
	}

}
