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
package com.cognifide.aet.worker.listeners;

import java.util.Map;

import javax.jms.Message;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.aet.communication.api.ProcessingError;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.job.ReporterJobData;
import com.cognifide.aet.communication.api.job.ReporterResultData;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.worker.impl.reports.ReporterDispatcher;

@Service
@Component(immediate = true, label = "AET Reporter Message Listener", policy = ConfigurationPolicy.REQUIRE, metatype = true, configurationFactory = true)
public class ReporterMessageListenerImpl extends AbstractTaskMessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReporterMessageListenerImpl.class);

	@Property(name = NAME, label = "Reporter name", description = "Name of reporter. Used in logs only", value = "Reporter")
	private String name;

	@Property(name = CONSUMER_QUEUE_NAME, label = "Consumer queue name", value = "AET.reporterJobs")
	private String consumerQueueName;

	@Property(name = PRODUCER_QUEUE_NAME, label = "Producer queue name", value = "AET.reporterResults")
	private String producerQueueName;

	@Reference
	private JmsConnection jmsConnection;

	@Reference
	private ReporterDispatcher dispatcher;

	@Activate
	void activate(Map<String, String> properties) {
		super.doActivate(properties);
	}

	@Deactivate
	void deactivate() {
		super.doDeactivate();
	}

	@Override
	public void onMessage(final Message message) {
		ReporterJobData reporterJobData = JmsUtils.getFromMessage(message, ReporterJobData.class);
		String jmsCorrelationId = JmsUtils.getJMSCorrelationID(message);

		if (reporterJobData != null && StringUtils.isNotBlank(jmsCorrelationId)) {
			ReporterResultData reporterResultData;
			try {
				LOGGER.info("ReporterJobData [{}] message arrived. CorrelationId: {} ReportModule: {}", name,
						jmsCorrelationId, reporterJobData.getReporterStep().getModule());
				NodeMetadata reporterNodeMetadata = dispatcher.run(reporterJobData);
				reporterResultData = ReporterResultData.createSuccessResult(reporterNodeMetadata);
				LOGGER.info("Report successful generated. CorrelationId: {} ReportModule: {}",
						jmsCorrelationId, reporterJobData.getReporterStep().getModule());
			} catch (AETException e) {
				LOGGER.error("Report generation failed. CorrelationId: {}", jmsCorrelationId, e);
				reporterResultData = ReporterResultData.createErrorResult(ProcessingError.reportingError(e
						.getMessage()));
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				reporterResultData = ReporterResultData.createErrorResult(ProcessingError
						.reportingError(String.format("Unrecognized reporter error: %s", e.getMessage())));
			}

			feedbackQueue.sendObjectMessageWithCorrelationID(reporterResultData, jmsCorrelationId);
		}
	}

	@Override
	protected void setName(String name) {
		this.name = name;
	}

	@Override
	protected void setConsumerQueueName(String consumerQueueName) {
		this.consumerQueueName = consumerQueueName;
	}

	@Override
	protected void setProducerQueueName(String producerQueueName) {
		this.producerQueueName = producerQueueName;
	}

	@Override
	protected JmsConnection getJmsConnection() {
		return jmsConnection;
	}

}
