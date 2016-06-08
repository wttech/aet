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

import com.cognifide.aet.communication.api.JobStatus;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.ProcessingError;

/**
 * Model which stores comparison phase results.
 */
public final class ComparatorResultData implements Serializable {

	private static final long serialVersionUID = 7467521940503927517L;

	private final JobStatus status;

	private final ProcessingError processingError;

	private final NodeMetadata comparatorNodeMetadata;

	private ComparatorResultData(JobStatus status, ProcessingError processingError, NodeMetadata comparatorNodeMetadata) {
		this.status = status;
		this.processingError = processingError;
		this.comparatorNodeMetadata = comparatorNodeMetadata;
	}

	/**
	 * @param processingError - error that caused comparator failure.
	 * @return new instance of ComparatorResultData with {@link JobStatus.ERROR error} status.
	 */
	public static ComparatorResultData createErrorResult(ProcessingError processingError) {
		return new ComparatorResultData(JobStatus.ERROR, processingError, null);
	}

	/**
	 * @param comparatorNodeMetadata - node key that points to comparison results.
	 * @return new instance of ComparatorResultData with {@link JobStatus.SUCCESS success} status.
	 */
	public static ComparatorResultData createSuccessResult(NodeMetadata comparatorNodeMetadata) {
		return new ComparatorResultData(JobStatus.SUCCESS, null, comparatorNodeMetadata);
	}

	/**
	 * @return status of comparison work.
	 */
	public JobStatus getStatus() {
		return status;
	}

	/**
	 * @return error that caused comparator failure.
	 */
	public ProcessingError getProcessingError() {
		return processingError;
	}

	/**
	 * @return node key that points to comparison results.
	 */
	public NodeMetadata getComparatorNodeMetadata() {
		return comparatorNodeMetadata;
	}

}
