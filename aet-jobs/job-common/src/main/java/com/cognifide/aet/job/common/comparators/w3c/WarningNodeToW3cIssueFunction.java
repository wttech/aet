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

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import com.google.common.base.Function;

public final class WarningNodeToW3cIssueFunction implements Function<Node, W3cIssue> {
	@Override
	public W3cIssue apply(Node child) {
		if (!(child instanceof Element)) {
			return null;
		}
		Element element = (Element) child;
		W3cIssueType issueType = W3cIssueType
				.valueOf(StringUtils.removeStart(element.attr("class"), "msg_").toUpperCase());
		String message = element.getElementsByAttributeValue("class", "msg").html();
		String additionalInfo = element.child(1).html();
		return new W3cIssue(0, 0, message, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
				additionalInfo, issueType);

	}
}
