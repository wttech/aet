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
package com.cognifide.aet.job.common.comparators.analytics;

import com.cognifide.aet.job.api.comparator.ComparatorJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.cognifide.aet.job.common.collectors.analytics.AnalyticsCollectorResult;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ResultStatus;
import com.cognifide.aet.vs.VersionStorageException;
import com.google.common.collect.Lists;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarNameValuePair;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author magdalena.biala
 * 
 */
public class AnalyticsComparator implements ComparatorJob {

	private static final String PARAM_MARK_SET = "mark-set";

	private static final String SEPARATOR_ITEM = ";";

	public static final String COMPARATOR_TYPE = "analytics";

	public static final String COMPARATOR_NAME = "analytics";

	protected final Node resultNode;

	protected final Node dataNode;

	private final ComparatorProperties comparatorProperties;

	private final List<String> markedSet = Lists.newArrayList();

	public AnalyticsComparator(Node dataNode, Node resultNode, ComparatorProperties comparatorProperties) {
		this.dataNode = dataNode;
		this.resultNode = resultNode;
		this.comparatorProperties = comparatorProperties;
	}

	@Override
	public Boolean compare() throws ProcessingException {
		try {
			AnalyticsCollectorResult dataResult = dataNode.getResult(AnalyticsCollectorResult.class);
			Collection<HarEntry> collectedSet = dataResult.getEntries();
			List<AnalyticsRequest> requests = Lists.newArrayList();
			boolean status = !collectedSet.isEmpty();

			for (HarEntry harEntry : collectedSet) {
				status = processHarEntry(requests, status, harEntry);
			}

			AnalyticsComparatorResult result = new AnalyticsComparatorResult(
					ResultStatus.fromBoolean(status), comparatorProperties, requests);

			resultNode.saveResult(result);
		} catch (VersionStorageException e) {
			throw new ProcessingException(e.getMessage(), e);
		}
		return NO_COMPARISON_RESULT;
	}

	private boolean processHarEntry(List<AnalyticsRequest> requests, boolean status, HarEntry harEntry) {
		boolean resultStatus = status;
		Iterator<HarNameValuePair> it = harEntry.getRequest().getQueryString().iterator();
		List<AnalyticsQuery> queries = Lists.newArrayList();
		while (it.hasNext()) {
			HarNameValuePair harNameValuePair = it.next();
			String key = harNameValuePair.getName();
			String value = harNameValuePair.getValue();
			boolean isMarked = markedSet.contains(key);
			if (isMarked && StringUtils.isEmpty(value)) {
				resultStatus = false;
			}
			queries.add(new AnalyticsQuery(key, value, isMarked));
		}
		requests.add(new AnalyticsRequest(Calendar.getInstance().getTime(), queries));
		return resultStatus;
	}

	@Override
	public void setParameters(Map<String, String> params) throws ParametersException {
		if (params.containsKey(PARAM_MARK_SET)) {
			String markedSetString = params.get(PARAM_MARK_SET);
			String[] items = StringUtils.split(markedSetString, SEPARATOR_ITEM);
			this.markedSet.addAll(Arrays.asList(items));
		}
	}

}
