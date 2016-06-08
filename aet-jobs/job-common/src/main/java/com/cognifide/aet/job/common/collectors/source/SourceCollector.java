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
package com.cognifide.aet.job.common.collectors.source;

import com.cognifide.aet.job.api.collector.CollectorJob;
import com.cognifide.aet.job.api.collector.CollectorProperties;
import com.cognifide.aet.job.api.collector.HttpRequestBuilder;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.Result;
import com.cognifide.aet.vs.VersionStorageException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SourceCollector implements CollectorJob {

	public static final String NAME = "source";

	private static final String DATA_FILENAME = "source.html";

	private static final String CHAR_ENCODING = "UTF-8";

	private final Node dataNode;

	private final Node patternNode;

	private final HttpRequestBuilder httpRequestBuilder;

	private final CollectorProperties collectorProperties;

	private final int timeoutValue;

	public SourceCollector(final Node dataNode, final Node patternNode,
			CollectorProperties collectorProperties, HttpRequestBuilder httpRequestBuilder, int timeoutValue) {
		this.dataNode = dataNode;
		this.patternNode = patternNode;
		this.collectorProperties = collectorProperties;
		this.httpRequestBuilder = httpRequestBuilder;
		this.timeoutValue = timeoutValue;
	}

	@Override
	public final boolean collect() throws ProcessingException {
		InputStream dataInputStream = null;
		try {

			byte[] content = getContent();
			final String pageSource = new String(content, CHAR_ENCODING);

			if (StringUtils.isBlank(pageSource)) {
				throw new ProcessingException("Page source is empty!");
			}
			dataInputStream = IOUtils.toInputStream(pageSource, CHAR_ENCODING);

			SourceCollectorResult result = new SourceCollectorResult(collectorProperties.getUrl(),
					DATA_FILENAME);

			dataNode.saveResult(result);
			dataNode.saveData(DATA_FILENAME, dataInputStream);
			savePatternIfNotExist(result, pageSource);
			return true;
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ProcessingException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(dataInputStream);
		}
	}

	private byte[] getContent() throws ProcessingException {
		byte[] content;
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Object> task = new Callable<Object>() {
			public Object call() throws IOException {
				return httpRequestBuilder.executeRequest().getContent();
			}
		};
		Future<Object> future = executor.submit(task);
		try {
			content = (byte[]) future.get(timeoutValue, TimeUnit.MILLISECONDS);
		} catch (TimeoutException | InterruptedException | ExecutionException e) {
			throw new ProcessingException(e.getMessage(), e);
		} finally {
			future.cancel(true);
		}
		return content;
	}

	@Override
	public void setParameters(final Map<String, String> params) throws ParametersException {
		// no parameters needed
	}

	private void savePatternIfNotExist(final Result result, final String pageSource)
			throws VersionStorageException, ProcessingException {
		SourceCollectorResult patternResult = patternNode.getResult(SourceCollectorResult.class);
		if (patternResult == null) {
			InputStream patternInputStream = null;
			try {
				patternInputStream = IOUtils.toInputStream(pageSource, CHAR_ENCODING);
				patternNode.saveResult(result);
				patternNode.saveData(DATA_FILENAME, patternInputStream);
			} catch (IOException e) {
				throw new ProcessingException(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(patternInputStream);
			}
		}

	}
}
