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
package com.cognifide.aet.worker.impl.reports.html;

import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import com.cognifide.aet.job.api.report.ReportsProperties;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.ReportResult;
import com.cognifide.aet.vs.VersionStorageException;
import com.cognifide.aet.worker.impl.reports.BasicReporterJobHandler;
import com.cognifide.aet.worker.impl.reports.ReporterHandlerWorkload;
import com.cognifide.aet.worker.impl.reports.ReporterJobHandler;

@Component
@Service(ReporterJobHandler.class)
public class HtmlReporterJobHandler extends BasicReporterJobHandler {

	protected ReportResult createResultData(ReporterHandlerWorkload workload, List<Node> resultNodes,
			ReportsProperties reportsProperties, String reportUrl) throws VersionStorageException {
		// @formatter:off
		return HtmlReportJob.Builder.create()
				.withTestRunMap(workload.getJob().getExpectedResults())
				.withReportsProperties(reportsProperties).withResultNodes(resultNodes)
				.withServiceUrl(reportUrl).build();
		// @formatter:on
	}

	@Override
	protected String getReportName() {

		return "html-report";
	}

}
