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
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import com.google.common.base.Function;

public final class ErrorNodeToW3cIssueFunction implements Function<Node, W3cIssue> {

	private static final String CLASS_ATTRIBUTE = "class";

	@Override
	public W3cIssue apply(Node child) {
		if (isValidNode(child)) {
			return null;
		}

		W3cIssueBuilder builder = new W3cIssueBuilder();
		Element element = (Element) child;
		extractIssueType(element, builder);
		extractLineColumn(element, builder);
		extractMessage(element, builder);
		extractCode(element, builder);
		extractAdditionalInfo(element, builder);
		return builder.build();
	}

	private boolean isValidNode(Node child) {
		return !(child instanceof Element);
	}

	private void extractAdditionalInfo(Element element, W3cIssueBuilder builder) {
		String additionalInfo = element.getElementsByAttributeValueContaining(CLASS_ATTRIBUTE, "ve").html();
		builder.setAdditionalInfo(additionalInfo);
	}

	private void extractCode(Element element, W3cIssueBuilder builder) {
		Elements code = element.getElementsByTag("code");
		String errorPosition = StringUtils.EMPTY;

		if (!code.isEmpty()) {
			for (Node node : code.get(0).childNodes()) {
				errorPosition = extractErrorCode(builder, errorPosition, node);
			}
		}
		builder.setErrorPosition(errorPosition);
	}

	private String extractErrorCode(W3cIssueBuilder builder, String errorPosition, Node node) {
		String resultErrorPosition = errorPosition;
		if (node instanceof Element) {
			resultErrorPosition = ((Element) node).getElementsByTag("strong").text();
		} else if (node instanceof TextNode) {
			if (StringUtils.isBlank(errorPosition)) {
				builder.setCode1(node.toString());
			} else {
				builder.setCode2(node.toString());
			}
		}
		return resultErrorPosition;
	}

	private void extractMessage(Element element, W3cIssueBuilder builder) {
		String message = element.getElementsByAttributeValue(CLASS_ATTRIBUTE, "msg").html();
		builder.setMessage(message);
	}

	private void extractLineColumn(Element element, W3cIssueBuilder builder) {
		String lineColumn = element.children().get(1).text();
		String[] position = lineColumn.split(",");
		String line = position[0].trim();
		String column = position[1].trim();
		builder.setLine(extractNumber(line));
		builder.setColumn(extractNumber(column));
	}

	private void extractIssueType(Element element, W3cIssueBuilder builder) {
		String issueTypeString = StringUtils.removeStart(element.attr(CLASS_ATTRIBUTE), "msg_").toUpperCase();
		builder.setIssueType(W3cIssueType.valueOf(issueTypeString));
	}

	private String extractNumber(String string) {
		String numberString = StringUtils.substringAfterLast(string, " ");
		if (!StringUtils.isNumeric(numberString)) {
			numberString = null;
		}
		return numberString;
	}
}
