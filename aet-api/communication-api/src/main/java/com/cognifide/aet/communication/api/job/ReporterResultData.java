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
 * Model which stores generated report data.
 */
public final class ReporterResultData implements Serializable {

	private static final long serialVersionUID = -3418707356518932969L;

	private final JobStatus status;

	private final ProcessingError processingError;

	private final NodeMetadata reporterNodeMetadata;

	private ReporterResultData(JobStatus status, ProcessingError processingError, NodeMetadata reporterNodeMetadata) {
		this.status = status;
		this.processingError = processingError;
		this.reporterNodeMetadata = reporterNodeMetadata;
	}

	/**
	 * @param processingError error that caused report generation failure.
	 * @return new instance of CollectorResultData with {@link JobStatus.ERROR error} status.
	 */
	public static ReporterResultData createErrorResult(ProcessingError processingError) {
		return new ReporterResultData(JobStatus.ERROR, processingError, null);
	}

	/**
	 * @param reportNodeMetadata node key which points to generated report.
	 * @return new instance of CollectorResultData with {@link JobStatus.SUCCESS success} status.
	 */
	public static ReporterResultData createSuccessResult(NodeMetadata reportNodeMetadata) {
		return new ReporterResultData(JobStatus.SUCCESS, null, reportNodeMetadata);
	}

	/**
	 * @return status of report generation.
	 */
	public JobStatus getStatus() {
		return status;
	}

	/**
	 * @return error that caused report generation failure.
	 */
	public ProcessingError getProcessingError() {
		return processingError;
	}

	/**
	 * @return node key which points to generated report.
	 */
	public NodeMetadata getReporterNodeMetadata() {
		return reporterNodeMetadata;
	}

}
