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

import com.cognifide.aet.job.common.comparators.w3c.W3cIssue;
import com.cognifide.aet.job.common.comparators.w3c.W3cIssueType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

public class W3cHtml5IssueDeserializer implements JsonDeserializer<W3cIssue> {

	private static final String TYPE = "type";
	private static final String SUBTYPE = "subType";
	private static final String MESSAGE = "message";
	private static final String EXTRACT = "extract";
	private static final String LAST_LINE = "lastLine";
	private static final String LAST_COLUMN = "lastColumn";
	private static final String HILITE_START = "hiliteStart";
	private static final String HILITE_LENGTH = "hiliteLength";

	@Override
	public W3cIssue deserialize(JsonElement json, Type typeOfT,
								JsonDeserializationContext jsonDeserializationContext) {

		JsonObject jo = json.getAsJsonObject();

		String type = getAsString(jo, TYPE);
		String subtype = getAsString(jo, SUBTYPE);
		String message = getAsString(jo, MESSAGE);
		String extract = getAsString(jo, EXTRACT);

		int lastLine = getAsInt(jo, LAST_LINE);
		int lastColumn = getAsInt(jo, LAST_COLUMN);
		int hiliteStart = getAsInt(jo, HILITE_START);
		int hiliteLength = getAsInt(jo, HILITE_LENGTH);

		String beforeHilite = StringUtils.substring(extract, 0, hiliteStart);
		String hilite = StringUtils.substring(extract, hiliteStart, hiliteStart + hiliteLength);
		String afterHilite = StringUtils.substring(extract, hiliteStart + hiliteLength);

		// problem with left and right double quotation mark not being displayed correctly
		message = StringUtils.replaceEach(message, new String[]{"\u00E2\u20AC\u015B", "\u00E2\u20AC" +
				"\u0165"}, new String[]{"", ""});
		// messages often contain HTML entities, escaping them, so they are displayed literally
		message = StringEscapeUtils.escapeHtml4(message);

		return new W3cIssue(lastLine, lastColumn, message, beforeHilite, afterHilite, hilite, null, W3cIssueType.getType(type, subtype));
	}

	private int getAsInt(JsonObject jsonObject, String key) {
		JsonElement element = jsonObject.get(key);
		if (element != null) {
			return element.getAsInt();
		}
		return 0;
	}

	private String getAsString(JsonObject jsonObject, String key) {
		JsonElement element = jsonObject.get(key);
		if (element != null) {
			return element.getAsString();
		}
		return null;
	}
}
