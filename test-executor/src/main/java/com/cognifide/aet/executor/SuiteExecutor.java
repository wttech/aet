/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.executor;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.cognifide.aet.communication.api.messages.MessageType;
import com.cognifide.aet.communication.api.messages.TaskMessage;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.executor.common.ConsumerRemover;
import com.cognifide.aet.executor.common.MessageProcessor;
import com.cognifide.aet.executor.common.ProcessorFactory;
import com.cognifide.aet.executor.common.SuiteFactory;
import com.cognifide.aet.executor.model.CollectorStep;
import com.cognifide.aet.executor.model.ComparatorStep;
import com.cognifide.aet.executor.model.TestRun;
import com.cognifide.aet.executor.model.TestSuiteRun;
import com.cognifide.aet.executor.xmlparser.api.ParseException;
import com.cognifide.aet.executor.xmlparser.api.TestSuiteParser;
import com.cognifide.aet.executor.xmlparser.xml.XmlTestSuiteParser;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

@Service(SuiteExecutor.class)
@Component(label = "AET Suite Executor", description = "Executes received test suite", immediate = true,
    metatype = true)
public class SuiteExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteExecutor.class);

  private static final String SCREEN = "screen";

  private static final String RUNNER_IN_QUEUE = "AET.runner-in";

  private static final String HTML_REPORT_URL_FORMAT = "%s/report.html?company=%s&project=%s&correlationId=%s";

  private static final String XUNIT_REPORT_URL_FORMAT = "%s/xunit?company=%s&project=%s&correlationId=%s";

  private static final String MESSAGE_RECEIVE_TIMEOUT_PROPERTY_NAME = "messageReceiveTimeout";

  private static final long DEFAULT_MESSAGE_RECEIVE_TIMEOUT = 300000L;

  private Map<String, MessageConsumer> consumersMap;

  @Property(name = MESSAGE_RECEIVE_TIMEOUT_PROPERTY_NAME, label = "ActiveMQ message receive timeout",
      description = "ActiveMQ message receive timeout", longValue = DEFAULT_MESSAGE_RECEIVE_TIMEOUT)
  private Long messageReceiveTimeout;

  @Reference
  private JmsConnection jmsConnection;

  @Activate
  public void activate(Map properties) {
    consumersMap = new ConcurrentHashMap<>();
    messageReceiveTimeout = PropertiesUtil.toLong(
        properties.get(MESSAGE_RECEIVE_TIMEOUT_PROPERTY_NAME), DEFAULT_MESSAGE_RECEIVE_TIMEOUT);
  }

  public SuiteExecutionResult execute(String suiteString, String domain, String endpointDomain) {
    SuiteExecutionResult result;

    TestSuiteParser xmlFileParser = new XmlTestSuiteParser();
    try {
      TestSuiteRun testSuiteRun = xmlFileParser.parse(suiteString);
      testSuiteRun = overrideDomainIfDefined(testSuiteRun, domain);
      String validationResult = validateTestSuiteRun(testSuiteRun);
      if (validationResult == null) {
        final Suite suite = new SuiteFactory().suiteFromTestSuiteRun(testSuiteRun);
        suite.validate(Sets.newHashSet("version", "runTimestamp"));

        runTestSuite(suite);

        String statusUrl = getStatusUrl(endpointDomain, suite);
        String htmlReportUrl = getReportUrl(HTML_REPORT_URL_FORMAT, "http://aet-vagrant", suite); //TODO extract parameter (domain)
        String xunitReportUrl = getReportUrl(XUNIT_REPORT_URL_FORMAT, endpointDomain, suite);
        result = SuiteExecutionResult.createSuccessResult(statusUrl, htmlReportUrl, xunitReportUrl);
      } else {
        result = SuiteExecutionResult.createErrorResult(validationResult);
      }
    } catch (ParseException | JMSException | ValidatorException e) {
      LOGGER.error("Failed to run test suite", e);
      result = SuiteExecutionResult.createErrorResult(e.getMessage());
    }

    return result;
  }

  public SuiteStatusResult getExecutionStatus(String correlationId) {
    SuiteStatusResult result = null;

    MessageConsumer consumer = consumersMap.get(correlationId);
    if (consumer != null) {
      try {
        Message statusMessage = consumer.receive(messageReceiveTimeout);
        if (statusMessage != null) {
          ConsumerRemover consumerRemover = new ConsumerRemover(consumersMap, correlationId);
          MessageProcessor processor = ProcessorFactory.produce(statusMessage, consumerRemover);
          if (processor != null) {
            result = processor.process();
          } else {
            result = new SuiteStatusResult(ProcessingStatus.PROGRESS);
          }
        }
      } catch (JMSException e) {
        LOGGER.error("Failed to get processing status", e);
      }
    }

    return result;
  }

  private TestSuiteRun overrideDomainIfDefined(TestSuiteRun testSuiteRun, String domain) {
    TestSuiteRun localTestSuiteRun = testSuiteRun;
    if (StringUtils.isNotBlank(domain)) {
      List<TestRun> tests = Lists.newArrayList(localTestSuiteRun.getTestRunMap().values());
      localTestSuiteRun = new TestSuiteRun(localTestSuiteRun, domain, tests);
    }
    return localTestSuiteRun;
  }

  private String validateTestSuiteRun(TestSuiteRun testSuiteRun) {
    for (TestRun testRun : testSuiteRun.getTestRunMap().values()) {
      if (hasScreenCollector(testRun) && !hasScreenComparator(testRun)) {
        return String.format(
            "Test suite does not contain screen comparator for screen collector in '%s' test, please fix it",
            testRun.getName());
      }
    }
    return null;
  }

  private boolean hasScreenCollector(TestRun testRun) {
    for (CollectorStep collectorStep : testRun.getCollectorSteps()) {
      if (SCREEN.equalsIgnoreCase(collectorStep.getModule())) {
        return true;
      }
    }
    return false;
  }

  private boolean hasScreenComparator(TestRun testRun) {
    for (List<ComparatorStep> comparatorSteps : testRun.getComparatorSteps().values()) {
      for (ComparatorStep comparatorStep : comparatorSteps) {
        if (SCREEN.equalsIgnoreCase(comparatorStep.getType())) {
          return true;
        }
      }
    }
    return false;
  }

  private void runTestSuite(Suite suite) throws JMSException {
    //TODO lock

    Session session = jmsConnection.getJmsSession();
    MessageProducer producer = session.createProducer(session.createQueue(RUNNER_IN_QUEUE));
    Destination outRunnerDestination = session.createTemporaryQueue();
    MessageConsumer consumer = session.createConsumer(outRunnerDestination);
    consumersMap.put(suite.getCorrelationId(), consumer);

    TaskMessage taskMessage = new TaskMessage<>(MessageType.RUN, suite);
    ObjectMessage message = session.createObjectMessage(taskMessage);
    message.setJMSReplyTo(outRunnerDestination);
    producer.send(message);
  }

  private String getReportUrl(String format, String domain, Suite suite) {
    return String.format(format, domain, suite.getCompany(), suite.getProject(), suite.getCorrelationId());
  }

  private String getStatusUrl(String domain, Suite suite) {
    return domain + SuiteStatusServlet.SERVLET_PATH + "/" + suite.getCorrelationId();
  }
}
