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
package com.cognifide.aet.job.common.comparators.layout;

import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.ComparatorResult;
import com.cognifide.aet.vs.ResultStatus;

public class LayoutComparatorResult extends ComparatorResult {

	private static final long serialVersionUID = 4452407176247090611L;

	private final String url;

	private String pattern;

	private String result;

	private String currentCreateDate;

	private String patternCreateDate;

	private String decodedPatternUrl;

	private String patternUrl;

	private String patternTitle;

	private String decodedUrl;

	private String title;

	private int widthDifference;

	private int pixelDifferenceCount;

	private int heightDifference;

	private String patternImageUrl;

	private String maskImageUrl;

	private String currentImageUrl;

	public LayoutComparatorResult(String url, ResultStatus status, ComparatorProperties properties) {
		super(status, properties, true);
		this.url = url;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getCurrentCreateDate() {
		return currentCreateDate;
	}

	public void setCurrentCreateDate(String currentCreateDate) {
		this.currentCreateDate = currentCreateDate;
	}

	public String getPatternCreateDate() {
		return patternCreateDate;
	}

	public void setPatternCreateDate(String patternCreateDate) {
		this.patternCreateDate = patternCreateDate;
	}

	public String getDecodedPatternUrl() {
		return decodedPatternUrl;
	}

	public void setDecodedPatternUrl(String decodedPatternUrl) {
		this.decodedPatternUrl = decodedPatternUrl;
	}

	public String getPatternUrl() {
		return patternUrl;
	}

	public void setPatternUrl(String patternUrl) {
		this.patternUrl = patternUrl;
	}

	public String getPatternTitle() {
		return patternTitle;
	}

	public void setPatternTitle(String patternTitle) {
		this.patternTitle = patternTitle;
	}

	public String getDecodedUrl() {
		return decodedUrl;
	}

	public void setDecodedUrl(String decodedUrl) {
		this.decodedUrl = decodedUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getWidthDifference() {
		return widthDifference;
	}

	public void setWidthDifference(int widthDifference) {
		this.widthDifference = widthDifference;
	}

	public int getPixelDifferenceCount() {
		return pixelDifferenceCount;
	}

	public void setPixelDifferenceCount(int pixelDifferenceCount) {
		this.pixelDifferenceCount = pixelDifferenceCount;
	}

	public int getHeightDifference() {
		return heightDifference;
	}

	public void setHeightDifference(int heightDifference) {
		this.heightDifference = heightDifference;
	}

	public String getPatternImageUrl() {
		return patternImageUrl;
	}

	public void setPatternImageUrl(String patternImageUrl) {
		this.patternImageUrl = patternImageUrl;
	}

	public String getMaskImageUrl() {
		return maskImageUrl;
	}

	public void setMaskImageUrl(String maskImageUrl) {
		this.maskImageUrl = maskImageUrl;
	}

	public String getCurrentImageUrl() {
		return currentImageUrl;
	}

	public void setCurrentImageUrl(String currentImageUrl) {
		this.currentImageUrl = currentImageUrl;
	}

	public String getUrl() {
		return url;
	}
}
