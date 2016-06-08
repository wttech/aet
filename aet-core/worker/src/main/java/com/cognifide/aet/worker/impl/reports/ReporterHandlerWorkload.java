/*
 * Cognifide AET :: Worker
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
package com.cognifide.aet.worker.impl.reports;

import java.util.List;

import org.apache.commons.lang3.Validate;

import com.cognifide.aet.communication.api.config.ReporterStep;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.job.ReporterJobData;
import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.cognifide.aet.communication.api.node.ReportMetadata;
import com.cognifide.aet.job.api.report.ReportsProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.VersionStorage;
import com.google.common.collect.Lists;

public class ReporterHandlerWorkload {

	private final ReportMetadata reporterNodeMetadata;

	private final ReportsProperties reportsProperties;

	private final List<Node> resultNodes;

	private final ReporterJobData job;

	private ReporterHandlerWorkload(Builder builder) {
		reporterNodeMetadata = builder.reporterNodeMetadata;
		reportsProperties = builder.reportsProperties;
		resultNodes = builder.resultNodes;
		job = builder.job;
	}

	public ReportMetadata getReporterNodeMetadata() {
		return reporterNodeMetadata;
	}

	public ReportsProperties getReportsProperties() {
		return reportsProperties;
	}

	public List<Node> getResultNodes() {
		return resultNodes;
	}

	public ReporterJobData getJob() {
		return job;
	}

	public static class Builder {
		private ReporterJobData job;

		private VersionStorage versionStorage;

		private ReportMetadata reporterNodeMetadata;

		private ReportsProperties reportsProperties;

		private List<Node> resultNodes = Lists.newArrayList();

		public static Builder createBuilder() {
			return new Builder();
		}

		public Builder withReporterJobData(ReporterJobData job) {
			this.job = job;
			return this;
		}

		public Builder withVersionStorage(VersionStorage versionStorage) {
			this.versionStorage = versionStorage;
			return this;
		}

		public ReporterHandlerWorkload build() throws AETException {
			Validate.notNull(versionStorage, "Versions storage not set");
			Validate.notNull(job, "Reporter job not set");

			ReporterStep reporterStep = job.getReporterStep();
			String reportName = reporterStep.getModule();

			List<NodeMetadata> resultNodeMetadataList = job.getResultNodeMetadatas();
			if (resultNodeMetadataList.isEmpty()) {
				throw new AETException(
						String.format(
								"Report %s can't be created - no results received from comparison phase.",
								reportName));
			}

			resultNodes = Lists.newArrayList();
			for (NodeMetadata nodeMetadata : resultNodeMetadataList) {
				Node resultNode = versionStorage.getResultNode(nodeMetadata);
				resultNodes.add(resultNode);
			}

			CompareMetadata nodeMetadata = (CompareMetadata) resultNodeMetadataList.get(0);

			PatternMetadata parentTestSuiteNodeMetadata = new PatternMetadata(nodeMetadata.getCompany(),
					nodeMetadata.getProject(), nodeMetadata.getTestSuiteName(),
					nodeMetadata.getEnvironment(), null, null, null, null, nodeMetadata.getCorrelationId());
			reportsProperties = new ReportsProperties(
					versionStorage.getRebasePatternParameters(parentTestSuiteNodeMetadata));

			reporterNodeMetadata = ReportMetadata.fromUrlNodeMetadata(nodeMetadata, reportName);

			return new ReporterHandlerWorkload(this);
		}
	}
}
