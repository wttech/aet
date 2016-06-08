/*
 * Cognifide AET :: Maven Plugin
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
package com.cognifide.aet.xmlparser.xml;

import com.cognifide.aet.communication.api.config.ReporterStep;
import com.cognifide.aet.xmlparser.api.ParseException;
import com.cognifide.aet.xmlparser.xml.models.Reports;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import java.util.List;
import java.util.Map;

public class ReportsConverter extends BasicPhaseConverter<Reports> {

	@Override
	public Reports read(InputNode node) throws ParseException {
		List<ReporterStep> reporterSteps = Lists.newArrayList();

		InputNode inputNode;
		try {
			while ((inputNode = node.getNext()) != null) {
				Map<String, String> parameters = getParameters(inputNode);
				ReporterStep reporterStep = new ReporterStep(inputNode.getName(), parameters);
				reporterSteps.add(reporterStep);
			}
			if (reporterSteps.isEmpty()) {
				Map<String, String> parameters = Maps.newHashMap();
				ReporterStep reporterStep = new ReporterStep("html-report", parameters);
				reporterSteps.add(reporterStep);
			}
		} catch (Exception e) {
			throw new ParseException(e.getMessage(), e);
		}
		return new Reports(reporterSteps);
	}

	@Override
	public void write(OutputNode arg0, Reports arg1) {
		// no write capability needed
	}

}
