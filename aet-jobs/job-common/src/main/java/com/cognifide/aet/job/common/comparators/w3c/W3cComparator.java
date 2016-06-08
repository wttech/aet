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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import org.apache.commons.io.IOUtils;

import com.cognifide.aet.job.api.collector.HttpRequestBuilder;
import com.cognifide.aet.job.api.collector.ResponseObject;
import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.source.SourceCollectorResult;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.VersionStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class W3cComparator implements ComparatorJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(W3cComparator.class);

	public static final String COMPARATOR_TYPE = "source";

	public static final String COMPARATOR_NAME = "w3c";

	private static final String UTF8_ENCODING = "UTF-8";

	private static final String PARAM_IGNORE_WARNINGS = "ignore-warnings";

	private final Node dataNode;

	private final Node resultNode;

	private final ComparatorProperties comparatorProperties;

	private final HttpRequestBuilder httpRequestBuilder;

	private final W3cResponseParser w3cResponseParser;

	private String validatorUrl;

	private boolean ignoreWarnings = true;

	private static final String PARAM_VALIDATOR = "validator";

	private final List<DataModifierJob> dataModifierJobs;

	public W3cComparator(Node dataNode, Node resultNode, ComparatorProperties comparatorProperties,
						 HttpRequestBuilder httpRequestBuilder, W3cResponseParser w3cResponseParser, String validatorUrl,
						 List<DataModifierJob> dataModifierJobs) {
		this.dataNode = dataNode;
		this.resultNode = resultNode;
		this.comparatorProperties = comparatorProperties;
		this.httpRequestBuilder = httpRequestBuilder;
		this.w3cResponseParser = w3cResponseParser;
		this.validatorUrl = validatorUrl;
		this.dataModifierJobs = dataModifierJobs;
	}

	@Override
	public Boolean compare() throws ProcessingException {
		InputStream dataStream = null;
		try {
			SourceCollectorResult dataResult = dataNode.getResult(SourceCollectorResult.class);
			String url = dataResult.getUrl();
			String data = dataResult.getDataFileName();

			LOGGER.info("Checking w3c for page {} using validator: {}.", url, validatorUrl);

			dataStream = dataNode.getData(data);
			String pageSource = IOUtils.toString(dataStream, UTF8_ENCODING);

			String validationOutputUrl = getValidationOutputUrl(validatorUrl, url);
			ResponseObject response = validateBySource(pageSource);
			W3cComparatorResult result = w3cResponseParser.parseResult(response.getHeaders(),
					new String(response.getContent(), "UTF-8"), comparatorProperties, validationOutputUrl,
					ignoreWarnings);
			for (DataModifierJob<W3cComparatorResult> dataModifierJob : dataModifierJobs) {
				LOGGER.info(
						"Starting {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
				result = dataModifierJob.modifyData(result);
				LOGGER.info(
						"Successfully ended data modifications using {}. TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorName: {} ComparatorModule: {} ComparatorName: {}",
						dataModifierJob.getInfo(), comparatorProperties.getTestName(),
						comparatorProperties.getUrlName(), comparatorProperties.getUrl(),
						comparatorProperties.getCollectorModule(),
						comparatorProperties.getCollectorModuleName(),
						comparatorProperties.getComparatorModule(),
						comparatorProperties.getComparatorModuleName());
			}
			resultNode.saveResult(result);
		} catch (IOException | VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(dataStream);
		}
		return NO_COMPARISON_RESULT;
	}

	public void setParameters(Map<String, String> params) throws ParametersException {
		if (params.containsKey(PARAM_VALIDATOR)) {
			validatorUrl = params.get(PARAM_VALIDATOR);
		}
		if (params.containsKey(PARAM_IGNORE_WARNINGS)) {
			ignoreWarnings = Boolean.valueOf(params.get(PARAM_IGNORE_WARNINGS));
		}
	}

	public ResponseObject validateBySource(String pageSource) throws IOException {
		String source = URLEncoder.encode("fragment", UTF8_ENCODING) + "="
				+ URLEncoder.encode(pageSource, UTF8_ENCODING);
		httpRequestBuilder.setContent(source.getBytes(UTF8_ENCODING)).createRequest(validatorUrl + "/check",
				"POST");
		return httpRequestBuilder.executeRequest();
	}

	private String getValidationOutputUrl(final String validatorUrl, final String url)
			throws UnsupportedEncodingException {
		StringBuilder validationUrlSb = new StringBuilder(validatorUrl);
		validationUrlSb.append("check?uri=").append(url);
		return validationUrlSb.toString();
	}

}
