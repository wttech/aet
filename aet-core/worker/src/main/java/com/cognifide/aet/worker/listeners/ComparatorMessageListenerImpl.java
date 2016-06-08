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
import com.cognifide.aet.communication.api.config.ComparatorStep;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.job.ComparatorJobData;
import com.cognifide.aet.communication.api.job.ComparatorResultData;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.worker.api.ComparatorDispatcher;

@Service
@Component(immediate = true, label = "AET Comparator Message Listener", policy = ConfigurationPolicy.REQUIRE, metatype = true, configurationFactory = true)
public class ComparatorMessageListenerImpl extends AbstractTaskMessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComparatorMessageListenerImpl.class);

	@Property(name = NAME, label = "Comparator name", description = "Name of comparator. Used in logs only", value = "Comparator")
	private String name;

	@Property(name = CONSUMER_QUEUE_NAME, label = "Consumer queue name", value = "AET.comparatorJobs")
	private String consumerQueueName;

	@Property(name = PRODUCER_QUEUE_NAME, label = "Producer queue name", value = "AET.comparatorResults")
	private String producerQueueName;

	@Reference
	private JmsConnection jmsConnection;

	@Reference
	private ComparatorDispatcher dispatcher;

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
		ComparatorJobData comparatorJobData = JmsUtils.getFromMessage(message, ComparatorJobData.class);
		String jmsCorrelationId = JmsUtils.getJMSCorrelationID(message);

		if (comparatorJobData != null && StringUtils.isNotBlank(jmsCorrelationId)) {
			CollectMetadata collectorNodeMetadata = comparatorJobData.getCollectorNodeMetadata();
			ComparatorStep comparatorStep = comparatorJobData.getComparatorStep();
			LOGGER.info(
					"ComparatorJobData  [{}] message arrived. CorrelationId: {} TestName: {} UrlName: {} Url: {} CollectorModule: {} CollectorModuleName {} ComparatorModule: {} ComparatorModuleName: {}",
					name, jmsCorrelationId, collectorNodeMetadata.getTestName(), collectorNodeMetadata.getUrlName(),
					collectorNodeMetadata.getUrl(), collectorNodeMetadata.getCollectorModule(),
					collectorNodeMetadata.getCollectorModuleName(), comparatorStep.getModule(),
					comparatorStep.getName());
			ComparatorResultData comparatorResultData;
			try {
				NodeMetadata comparatorNodeMetadata = dispatcher.run(comparatorJobData);
				LOGGER.info(
						"Comparison successfully ended. CorrelationId: {} TestName: {} Url: {} CollectorModule: {} CollectorModuleName {} ComparatorModule: {} ComparatorModuleName: {}",
						jmsCorrelationId, collectorNodeMetadata.getTestName(), collectorNodeMetadata.getUrl(),
						collectorNodeMetadata.getCollectorModule(), collectorNodeMetadata.getCollectorModuleName(),
						comparatorStep.getModule(), comparatorStep.getName());
				comparatorResultData = ComparatorResultData.createSuccessResult(comparatorNodeMetadata);
			} catch (AETException e) {
				LOGGER.error("Exception during compare. CorrelationId: {}", jmsCorrelationId, e);
				comparatorResultData = ComparatorResultData.createErrorResult(ProcessingError
						.comparingError(e.getMessage()));
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				comparatorResultData = ComparatorResultData.createErrorResult(ProcessingError
						.comparingError("Unrecognized comparator error"));
			}

			feedbackQueue.sendObjectMessageWithCorrelationID(comparatorResultData, jmsCorrelationId);
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
