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
package com.cognifide.aet.job.common.collectors.screen;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.modifiers.resolution.ResolutionModifier;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.Result;
import com.cognifide.aet.vs.VersionStorageException;

public class ScreenCollector implements CollectorJob {

	public static final String NAME = "screen";

	private static final String PARAM_MAXIMIZE = "maximize";

	private static final String PARAM_WIDTH = "width";

	private static final String PARAM_HEIGHT = "height";

	private static final String PARAM_MAKE_PATTERN = "makePattern";

	private static final String DATA_FILENAME = "screenshot.png";

	private final Node dataNode;

	private final Node patternNode;

	private final WebDriver webDriver;

	private final CollectorProperties collectorProperties;

	// create pattern if it does not exist yet
	private boolean makePattern = true;

	private ResolutionModifier resolutionModifier;

	public ScreenCollector(Node dataNode, Node patternNode, WebDriver webDriver,
			CollectorProperties collectorProperties) {
		this.dataNode = dataNode;
		this.patternNode = patternNode;
		this.webDriver = webDriver;
		this.collectorProperties = collectorProperties;
	}

	@Override
	public boolean collect() throws ProcessingException {
		if (resolutionModifier != null) {
			resolutionModifier.collect();
		}

		ScreenCollectorResult result = new ScreenCollectorResult(collectorProperties.getUrl());
		result.setDataFileName(DATA_FILENAME);
		result.setTitle(webDriver.getTitle());
		result.setCreated(Calendar.getInstance().getTime());

		byte[] screenshot = takeScreenshot(webDriver);

		InputStream patternStream = null;
		InputStream dataStream = null;
		try {
			result.setMD5(DigestUtils.md5Hex(screenshot));

			if (makePattern) {
				patternStream = new ByteArrayInputStream(screenshot);
				savePatternIfNotExist(result, patternStream);
			}

			String dataUrl;
			if (comparePatternMD5(result)) {
				dataUrl = getPatternUrl();
			} else {
				dataStream = new ByteArrayInputStream(screenshot);
				dataUrl = dataNode.saveData(DATA_FILENAME, dataStream);
			}

			result.setDataUrl(dataUrl);
			dataNode.saveResult(result);
			return true;
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(patternStream);
			IOUtils.closeQuietly(dataStream);
		}
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		if (params.containsKey(PARAM_MAXIMIZE) || params.containsKey(PARAM_HEIGHT)
				|| params.containsKey(PARAM_WIDTH)) {
			resolutionModifier = new ResolutionModifier(webDriver);
			resolutionModifier.setParameters(params);
		}

		if (params.containsKey(PARAM_MAKE_PATTERN)) {
			String paramValue = params.get(PARAM_MAKE_PATTERN);
			makePattern = Boolean.valueOf(paramValue);
		}
	}

	private boolean comparePatternMD5(ScreenCollectorResult dataResult) throws VersionStorageException {
		ScreenCollectorResult patternResult = patternNode.getResult(ScreenCollectorResult.class);
		boolean result = false;
		if (patternResult != null) {
			result = StringUtils.equalsIgnoreCase(patternResult.getMD5(), dataResult.getMD5());
		}
		return result;
	}

	private String getPatternUrl() throws VersionStorageException {
		ScreenCollectorResult patternResult = patternNode.getResult(ScreenCollectorResult.class);
		return patternResult.getUrl();
	}

	private void savePatternIfNotExist(final Result result, InputStream data) throws VersionStorageException {
		ScreenCollectorResult patternResult = patternNode.getResult(ScreenCollectorResult.class);

		if (patternResult == null) {
			patternNode.saveResult(result);
			patternNode.saveData(DATA_FILENAME, data);
		}
	}

	private byte[] takeScreenshot(WebDriver webDriver) throws ProcessingException {
		try {
			return ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
		} catch (WebDriverException e) {
			throw new ProcessingException("Could not take screenshot, page is not loaded yet!", e);
		}
	}

}
