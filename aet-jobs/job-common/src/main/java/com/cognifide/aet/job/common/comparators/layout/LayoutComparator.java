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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.comparators.layout.utils.ImageComparison;
import com.cognifide.aet.job.common.comparators.layout.utils.ImageComparisonResult;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ResultStatus;
import com.cognifide.aet.vs.VersionStorageException;

public class LayoutComparator implements ComparatorJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(LayoutComparator.class);

	public static final String COMPARATOR_TYPE = "screen";

	public static final String COMPARATOR_NAME = "layout";

	private static final String FN_RESULT = "result.png";

	private static final String DATA_ARTIFACT_NAME = "screenshot.png";

	private static final String FN_PATTERN = "patternSnapshot.png";

	private static final String MD5 = "md5";

	private final Node dataNode;

	private final Node patternNode;

	private final Node resultNode;

	private final ComparatorProperties comparatorProperties;

	public LayoutComparator(Node dataNode, Node patternNode, Node resultNode,
			ComparatorProperties comparatorProperties) {
		this.dataNode = dataNode;
		this.patternNode = patternNode;
		this.resultNode = resultNode;
		this.comparatorProperties = comparatorProperties;
	}

	@Override
	public Boolean compare() throws ProcessingException {
		InputStream data = null;
		InputStream pattern = null;
		Boolean compareResult = null;
		try {
			LayoutJson layoutJson = dataNode.getResult(LayoutJson.class);
			String dataMD5 = layoutJson.getMD5();
			Map<String, String> patternMetadata = patternNode.getResult(Map.class);
			String patternMD5 = patternMetadata.get(MD5);

			ImageComparisonResult comparisonResult;
			if (StringUtils.equalsIgnoreCase(dataMD5, patternMD5)) {
				comparisonResult = new ImageComparisonResult();
				compareResult = true;
			} else {
				data = dataNode.getData(layoutJson.getData());
				pattern = patternNode.getData(patternMetadata.get("data"));
				BufferedImage patternImg = ImageIO.read(pattern);
				BufferedImage dataImg = ImageIO.read(data);
				comparisonResult = ImageComparison.compare(patternImg, dataImg);
				compareResult = comparisonResult.isMatch();
			}

			saveArtifacts(compareResult, comparisonResult, pattern, patternMetadata, layoutJson);
		} catch (Exception e) {
			throw new ProcessingException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(data);
			IOUtils.closeQuietly(pattern);
		}
		return compareResult;
	}

	private void saveArtifacts(Boolean noDifferencesFound, ImageComparisonResult comparisonResult,
			InputStream pattern, Map<String, String> patternMetadata, LayoutJson layoutJson)
					throws ProcessingException {
		ByteArrayOutputStream baos = null;
		LayoutComparatorResult result;
		String patternImageUrl;
		String maskImageUrl = null;
		InputStream mask = null;
		try {
			if (noDifferencesFound) {
				patternImageUrl = getPatternUrl();
			} else {
				baos = new ByteArrayOutputStream();
				ImageIO.write(comparisonResult.getResultImage(), "png", baos);
				mask = new ByteArrayInputStream(baos.toByteArray());
				maskImageUrl = resultNode.saveData(FN_RESULT, mask);
				// read it once more
				IOUtils.closeQuietly(pattern);
				final InputStream data = patternNode.getData(patternMetadata.get("data"));
				patternImageUrl = resultNode.saveData(FN_PATTERN, data);
			}
			result = getResultMap(comparisonResult, layoutJson, patternMetadata, patternImageUrl,
					maskImageUrl);
			resultNode.saveResult(result);
		} catch (Exception e) {
			throw new ProcessingException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(mask);
			IOUtils.closeQuietly(baos);
		}
	}

	/**
	 * THIS IS DIRTY HACK AND IT SHOULDN'T BE NEEDED AFTER 'CORE REFACTOR' it saves empty results pattern just
	 * to retrieve rest url then it operates on string to get pattern url
	 *
	 * @return string url to pattern artifact
	 * @throws ProcessingException
	 */
	private String getPatternUrl() throws ProcessingException {
		String resultPart = "/results/";
		String patternPart = "/patterns/";
		String result;
		try {
			result = resultNode.saveData(FN_PATTERN, new InputStream() {
				int i = 0;

				@Override
				public int read() throws IOException {
					if (i > 0) {
						return -1;
					}
					i++;
					return 0;
				}
			});
			resultNode.removeData(FN_PATTERN);
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		}
		result = result.replaceAll(resultPart, patternPart);
		int searchStartPoint = result.indexOf(patternPart) + patternPart.length();
		int cutEnd = result.indexOf("/", result.indexOf("/", searchStartPoint) + 1);
		result = result.substring(0, cutEnd) + "/" + DATA_ARTIFACT_NAME;
		return result;
	}

	private LayoutComparatorResult getResultMap(ImageComparisonResult comparisonResult, LayoutJson layoutJson,
			Map<String, String> patternMetadata, String patternImageUrl, String maskImageUrl) {

		ResultStatus status = ResultStatus.fromBoolean(comparisonResult.isMatch());
		LayoutComparatorResult result = new LayoutComparatorResult(layoutJson.getUrl(), status,
				comparatorProperties);

		result.setHeightDifference(comparisonResult.getHeightDifference());
		result.setPixelDifferenceCount(comparisonResult.getPixelDifferenceCount());
		result.setWidthDifference(comparisonResult.getWidthDifference());
		result.setTitle(layoutJson.getTitle());
		result.setDecodedUrl(getDecoded(layoutJson.getUrl()));
		result.setPatternTitle(patternMetadata.get("title"));
		result.setPatternUrl(patternMetadata.get("url"));
		result.setDecodedPatternUrl(getDecoded(patternMetadata.get("url")));
		result.setCurrentCreateDate(layoutJson.getCreated());
		result.setResult(FN_RESULT);
		result.setPattern(FN_PATTERN);
		result.setPatternImageUrl(patternImageUrl);
		result.setMaskImageUrl(maskImageUrl);
		result.setCurrentImageUrl(layoutJson.getDataUrl());

		if (patternMetadata.containsKey("created")) {
			result.setPatternCreateDate(patternMetadata.get("created"));
		}
		return result;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		// no parameters needed
	}

	private String getDecoded(String url) {
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.warn("Failed to decode url: {} Cause {} ", url, e);
			return StringUtils.EMPTY;
		}
	}

}
