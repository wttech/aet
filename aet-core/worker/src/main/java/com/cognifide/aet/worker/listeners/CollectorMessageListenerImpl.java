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

import java.util.List;
import java.util.Map;

import javax.jms.Message;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
import com.cognifide.aet.communication.api.job.CollectorJobData;
import com.cognifide.aet.communication.api.job.CollectorResultData;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.job.api.collector.WebCommunicationWrapper;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.worker.api.CollectorDispatcher;
import com.cognifide.aet.worker.drivers.WebDriverProvider;
import com.cognifide.aet.worker.exceptions.WorkerException;

@Service
@Component(immediate = true, label = "AET Collector Message Listener", policy = ConfigurationPolicy.REQUIRE, metatype = true, configurationFactory = true)
public class CollectorMessageListenerImpl extends AbstractTaskMessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectorMessageListenerImpl.class);

	private static final String WEB_DRIVER_NAME = "webDriverName";

	private static final String PORT_NAME = "portName";

	private static final int DEFAULT_PORT = 4444;

	@Property(name = NAME, label = "Collector name", description = "Name of collector. Used in logs only", value = "Collector")
	private String name;

	@Property(name = CONSUMER_QUEUE_NAME, label = "Consumer queue name", value = "AET.win7-ff16")
	private String consumerQueueName;

	@Property(name = PRODUCER_QUEUE_NAME, label = "Producer queue name", value = "AET.collectorResults")
	private String producerQueueName;

	@Property(name = WEB_DRIVER_NAME, label = "Web Driver name", value = "ff")
	private String webDriverName;

	@Property(name = PORT_NAME, label = "Embedded Proxy Server Port", value = "4501")
	private int port;

	@Reference
	private JmsConnection jmsConnection;

	@Reference
	private CollectorDispatcher dispatcher;

	@Reference
	private WebDriverProvider webDriverProvider;

	@Activate
	void activate(Map<String, String> properties) {
		super.doActivate(properties);
		webDriverName = properties.get(WEB_DRIVER_NAME);
		port = NumberUtils.toInt(properties.get(PORT_NAME), DEFAULT_PORT);
	}

	@Deactivate
	void deactivate() {
		super.doDeactivate();
	}

	@Override
	public void onMessage(final Message message) {
		CollectorJobData collectorJobData = JmsUtils.getFromMessage(message, CollectorJobData.class);
		String correlationId = JmsUtils.getJMSCorrelationID(message);
		String requestMessageId = JmsUtils.getJMSMessageID(message);
		if (collectorJobData != null && StringUtils.isNotBlank(correlationId) && requestMessageId != null) {
			LOGGER.info(
					"CollectorJobData [{}] message arrived with {} urls. CorrelationId: {} RequestMessageId: {}",
					name, collectorJobData.getUrlNodeMetadataList().size(), correlationId, requestMessageId);
			WebCommunicationWrapper webCommunicationWrapper = null;
			int collected = 0;
			try {
				if (isProxyUsed(collectorJobData.getUseProxy())) {
					webCommunicationWrapper = this.webDriverProvider.createWebDriverWithProxy(webDriverName,
							collectorJobData.getUseProxy(), port);
				} else {
					webCommunicationWrapper = this.webDriverProvider.createWebDriver(webDriverName);
				}
				collected = runUrls(collectorJobData, requestMessageId, webCommunicationWrapper,
						correlationId);

			} catch (WorkerException e) {
				for (CollectMetadata nodeMetadata : collectorJobData.getUrlNodeMetadataList()) {
					String errorMessage = String.format(
							"Couldn't collect following url %s because of error: %s", nodeMetadata.getUrl(),
							e.getMessage());
					LOGGER.error(errorMessage, e);
					CollectorResultData collectorResultData = CollectorResultData.createErrorResult(
							ProcessingError.collectingError(errorMessage), requestMessageId,
							collectorJobData.getTestName());
					feedbackQueue.sendObjectMessageWithCorrelationID(collectorResultData, correlationId);
				}
			} finally {
				quitWebDriver(webCommunicationWrapper);
			}
			LOGGER.info("Successfully collected from {}/{} urls.", collected, collectorJobData
					.getUrlNodeMetadataList().size());
		}

	}

	private int runUrls(CollectorJobData collectorJobData, String requestMessageId,
			WebCommunicationWrapper webCommunicationWrapper, String correlationId) {
		int result = 0;
		for (CollectMetadata nodeMetadata : collectorJobData.getUrlNodeMetadataList()) {
			try {
				CollectorResultData collectorResultData;
				List<CollectMetadata> collectedNodesList = dispatcher.run(nodeMetadata, collectorJobData,
						webCommunicationWrapper);
				collectorResultData = CollectorResultData.createSuccessResult(collectedNodesList,
						requestMessageId, collectorJobData.getTestName());

				result++;
				feedbackQueue.sendObjectMessageWithCorrelationID(collectorResultData, correlationId);
			} catch (AETException e) {
				LOGGER.error("Exception during collect. CorrelationId: {}", correlationId, e);
				CollectorResultData collectorResultData = CollectorResultData.createErrorResult(
						ProcessingError.collectingError(e.getMessage()), requestMessageId,
						collectorJobData.getTestName());
				feedbackQueue.sendObjectMessageWithCorrelationID(collectorResultData, correlationId);
			} catch (Exception e) {
				LOGGER.error("Unrecognized collector error", e);
				CollectorResultData collectorResultData = CollectorResultData.createErrorResult(
						ProcessingError.collectingError(e.getMessage()), requestMessageId,
						collectorJobData.getTestName());
				feedbackQueue.sendObjectMessageWithCorrelationID(collectorResultData, correlationId);
			}
		}

		return result;
	}

	private boolean isProxyUsed(String useProxy) {
		return StringUtils.isNotBlank(useProxy) && !("false").equals(useProxy);
	}

	private void quitWebDriver(WebCommunicationWrapper webCommunicationWrapper) {
		if (webCommunicationWrapper != null) {
			webCommunicationWrapper.getWebDriver().quit();
			if (webCommunicationWrapper.isUseProxy()) {
				try {
					webCommunicationWrapper.getProxyServer().stop();
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
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
