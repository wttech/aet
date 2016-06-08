/*
 * Cognifide AET :: Communication API
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
package com.cognifide.aet.communication.api.job;

import java.io.Serializable;
import java.util.List;

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.node.CollectMetadata;

/**
 * Model which stores collection phase results.
 */
public final class CollectorResultData implements Serializable {

	private static final long serialVersionUID = 2290282611303131656L;

	private final JobStatus status;

	private final ProcessingError processingError;

	private final List<CollectMetadata> collectorNodeMetadatas;

	private final String requestMessageId;

	private final String testName;

	private CollectorResultData(JobStatus status, ProcessingError processingError,
			List<CollectMetadata> collectorNodeMetadatas, String requestMessageId, String testName) {
		this.status = status;
		this.processingError = processingError;
		this.collectorNodeMetadatas = collectorNodeMetadatas;
		this.requestMessageId = requestMessageId;
		this.testName = testName;
	}

	/**
	 * @param processingError - error that caused collector failure.
	 * @param requestJMSMessageID - id of jms message that invoked collection execution.
	 * @param testName - name of the test that collection is part of.
	 * @return new instance of CollectorResultData with {@link JobStatus.ERROR error} status.
	 */
	public static CollectorResultData createErrorResult(ProcessingError processingError,
			String requestJMSMessageID, String testName) {
		return new CollectorResultData(JobStatus.ERROR, processingError, null, requestJMSMessageID, testName);
	}

	/**
	 * @param collectorNodeMetadatas - list of NodeKeys that points to collection results.
	 * @param requestJMSMessageID - id of jms message that invoked collection execution.
	 * @param testName - name of the test that collection is part of.
	 * @return new instance of CollectorResultData with {@link JobStatus.SUCCESS success} status.
	 */
	public static CollectorResultData createSuccessResult(List<CollectMetadata> collectorNodeMetadatas,
			String requestJMSMessageID, String testName) {
		return new CollectorResultData(JobStatus.SUCCESS, null, collectorNodeMetadatas, requestJMSMessageID,
				testName);
	}

	/**
	 * @return status of collector work.
	 */
	public JobStatus getStatus() {
		return status;
	}

	/**
	 * @return error that caused collector failure.
	 */
	public ProcessingError getProcessingError() {
		return processingError;
	}

	/**
	 * @return list of NodeKeys that points to collection results.
	 */
	public List<CollectMetadata> getCollectorNodeMetadatas() {
		return collectorNodeMetadatas;
	}

	/**
	 * @return id of jms message that invoked collection execution.
	 */
	public String getRequestMessageId() {
		return requestMessageId;
	}

	/**
	 * @return name of the test that collection is part of.
	 */
	public String getTestName() {
		return testName;
	}

}
