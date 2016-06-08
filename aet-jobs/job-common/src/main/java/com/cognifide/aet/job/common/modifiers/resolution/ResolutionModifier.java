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
package com.cognifide.aet.job.common.modifiers.resolution;

import com.cognifide.aet.job.api.ParametersValidator;
import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ResolutionModifier implements CollectorJob {

	public static final String NAME = "resolution";

	private static final Logger LOG = LoggerFactory.getLogger(ResolutionModifier.class);

	private static final String PARAM_MAXIMIZE = "maximize";

	private static final String WIDTH_PARAM = "width";

	private static final String HEIGHT_PARAM = "height";

	private static final int MAX_SIZE = 100000;

	private final WebDriver webDriver;

	private int width;

	private int height;

	private boolean maximize;

	public ResolutionModifier(WebDriver webDriver) {
		this.webDriver = webDriver;
	}

	@Override
	public boolean collect() throws ProcessingException {
		setResolution(this.webDriver);
		return false;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		String paramValue = params.get(PARAM_MAXIMIZE);
		maximize = Boolean.valueOf(paramValue);

		if (params.containsKey(WIDTH_PARAM) && params.containsKey(HEIGHT_PARAM)) {
			width = NumberUtils.toInt(params.get(WIDTH_PARAM));
			ParametersValidator.checkRange(width, 1, MAX_SIZE, "Width should be greater than 0");

			height = NumberUtils.toInt(params.get(HEIGHT_PARAM));
			ParametersValidator.checkRange(height, 1, MAX_SIZE, "Height should be greater than 0");

			ParametersValidator.checkParameter(!maximize,
					"You cannot maximize the window and specify the dimension");
		} else if (params.containsKey(WIDTH_PARAM) || params.containsKey(HEIGHT_PARAM)) {
			throw new ParametersException("You have to specify both width and height");
		}
	}

	private void setResolution(WebDriver webDriver) {
		Window window = webDriver.manage().window();
		if (maximize) {
			window.maximize();
			LOG.info("Setting resolution to  {}x{}  ", window.getSize().getWidth(), window.getSize().getHeight());
		} else {
			LOG.info("Setting resolution to  {}x{}  ", width, height);
			window.setSize(new Dimension(width, height));
		}
	}

}
