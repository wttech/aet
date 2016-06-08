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

import java.net.URISyntaxException;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.node.ReportMetadata;
import com.cognifide.aet.job.api.report.ReportsProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ReportResult;
import com.cognifide.aet.vs.VersionStorage;
import com.cognifide.aet.vs.VersionStorageException;

@Component
@Service(ReporterJobHandler.class)
public abstract class BasicReporterJobHandler implements ReporterJobHandler {
	
	@Reference
	protected VersionStorage versionStorage;

	@Reference
	protected ReportingServiceURLProvider reportUrlProvider;

	@Override
	public boolean accept(ReporterHandlerWorkload workload) {
		String reportName = workload.getJob().getReporterStep().getModule();

		return getReportName().equals(reportName);
	}
	
	public ReportMetadata handle(ReporterHandlerWorkload workload) throws AETException {

		String reportName = workload.getJob().getReporterStep().getModule();

		List<Node> resultNodes = workload.getResultNodes();

		ReportsProperties reportsProperties = workload.getReportsProperties();

		ReportMetadata reporterNodeMetadata = workload.getReporterNodeMetadata();

		Node reportNode = versionStorage.getReportNode(reporterNodeMetadata);

		try {
			String reportUrl = reportUrlProvider.produceServiceUrl(reporterNodeMetadata.getCompany(),
					reporterNodeMetadata.getProject(), reporterNodeMetadata.getTestSuiteName(),
					reporterNodeMetadata.getEnvironment(), reporterNodeMetadata.getCorrelationId(),
					reporterNodeMetadata.getReporterModule(), workload.getJob().getReporterStep());
			
			ReportResult reportData = createResultData(workload, resultNodes, reportsProperties, reportUrl);

			reportNode.saveResult(reportData);
			
		} catch (URISyntaxException e) {
			throw new AETException(String.format(
					"Report %s can't be created - can't generate report generator service url: %s",
					reportName, e.getMessage()), e);
		}

		return reporterNodeMetadata;
	}
	
	protected abstract ReportResult createResultData(ReporterHandlerWorkload workload, List<Node> resultNodes,
			ReportsProperties reportsProperties, String reportUrl) throws VersionStorageException;
	
	protected abstract String getReportName();
	
}
