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
package com.cognifide.aet.common;

import com.google.common.collect.Sets;
import com.google.gson.Gson;

import com.cognifide.aet.communication.api.CommunicationSettings;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.messages.MessageType;
import com.cognifide.aet.communication.api.messages.TaskMessage;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.queues.JmsEndpointConfig;
import com.cognifide.aet.model.TestSuiteRun;
import com.jcabi.log.Logger;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

public class TestSuiteRunner {

  private static final String DATE_FORMAT = "HH:mm:ss.SSS";

  private static final ThreadLocal<DateFormat> DATE_FORMATTER = new ThreadLocal<DateFormat>() {
    @Override
    protected DateFormat initialValue() {
      return new SimpleDateFormat(DATE_FORMAT);
    }
  };

  private static final String TIMEOUT_EXCEPTION_MESSAGE = "Timeout: no update received from the system since %d "
          + "seconds. Suite execution will be aborted.";

  private static final String HTML_REPORT_URL_FORMAT = "%s/report.html?company=%s&project=%s&correlationId=%s";

  private static final String XUNIT_REPORT_URL_FORMAT = "%s/xunit?company=%s&project=%s&correlationId=%s";

  private final Connection connection;

  private final Session session;

  private final MessageProducer producer;

  private final Destination outRunnerDestination;

  private final String buildDirectory;

  private final long timeout;

  private final RedirectWriter redirectWriter;

  private final String endpointDomain;

  private final boolean xUnit;

  private MessageConsumer consumer;

  private String reportDomain;

  public TestSuiteRunner(String endpointDomain, String inQueueName,
                         String buildDirectory, long timeout, boolean xUnit) throws JMSException, IOException {
    this.redirectWriter = new RedirectWriter(buildDirectory);
    this.buildDirectory = buildDirectory;
    this.timeout = timeout;
    this.endpointDomain = endpointDomain;
    this.xUnit = xUnit;
    CommunicationSettings communicationSettings = getSettings();
    reportDomain = communicationSettings.getReportDomain();
    JmsEndpointConfig jmsEndpointConfig = communicationSettings.getJmsEndpointConfig();
    ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(jmsEndpointConfig.getUser(),
            jmsEndpointConfig.getPass(), jmsEndpointConfig.getUri());
    activeMQConnectionFactory.setTrustAllPackages(true);
    connection = activeMQConnectionFactory.createConnection();
    session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    producer = session.createProducer(session.createQueue(inQueueName));
    outRunnerDestination = session.createTemporaryQueue();
    connection.start();
  }

  private CommunicationSettings getSettings() throws IOException {
    try {
      URL configUrl = new URL(endpointDomain + "/configs/communicationSettings");
      String communicationSettings = IOUtils.toString(configUrl.openStream());
      return new Gson().fromJson(communicationSettings, CommunicationSettings.class);
    } catch (IOException e) {
      throw new IOException("Unable to get Configs form AET Configs service", e);
    }
  }

  public void runTestSuite(final TestSuiteRun testSuiteRun) throws AETException {
    RunnerTerminator runnerTerminator = new RunnerTerminator();
    try {
      final Suite suite = new SuiteFactory().suiteFromTestSuiteRun(testSuiteRun);
      suite.validate(Sets.newHashSet("version", "runTimestamp"));
      new LockClient(suite, endpointDomain, runnerTerminator).tryToSetLock();
      TaskMessage taskMessage = new TaskMessage<>(MessageType.RUN, suite);
      ObjectMessage message = session.createObjectMessage(taskMessage);
      message.setJMSReplyTo(outRunnerDestination);
      consumer = session.createConsumer(outRunnerDestination);
      producer.send(message);

      String now = DATE_FORMATTER.get().format(new Date());
      Logger.info(this, String.format("CorrelationID: %s", testSuiteRun.getCorrelationId()));
      Logger.info(this,
              "********************************************************************************");
      Logger.info(this,
              "********************** Job Setup finished at " + now + ".**********************");
      Logger.info(this,
              "*** Suite is now processed by the system, progress will be available below. ****");
      Logger.info(this,
              "********************************************************************************");
      while (runnerTerminator.isActive()) {
        Message received = consumer.receive(timeout);
        if (received != null) {
          process(runnerTerminator, getReportUrl(HTML_REPORT_URL_FORMAT, suite), received);
        } else {
          throw new AETException(String.format(TIMEOUT_EXCEPTION_MESSAGE,
                  TimeUnit.MILLISECONDS.toSeconds(timeout)));
        }
      }
      if (xUnit) {
        downloadXUnitTest(suite);
      }
    } catch (JMSException e) {
      throw new AETException("JMS error", e);
    } catch (ValidatorException e) {
      throw new AETException("Invalid Suite : " + e.getAllViolationMessages(), e);
    } finally {
      Logger.info(this, "Suite processing finished.");
      close();
    }
  }

  private void downloadXUnitTest(Suite suite) {
    String endpointUrl = getEndpointUrl(XUNIT_REPORT_URL_FORMAT, suite);
    try {
      new ReportWriter().write(buildDirectory, endpointUrl, "xunit-report.xml");
    } catch (IOException e) {
      Logger.error(this, "Failed to obtain xUnit report from: %s", endpointUrl, e);
    }
  }

  private String getReportUrl(String reportUrlFormat, Suite suite) {
    return String.format(reportUrlFormat, reportDomain, suite.getCompany(), suite.getProject(), suite.getCorrelationId());
  }

  private String getEndpointUrl(String reportUrlFormat, Suite suite) {
    return String.format(reportUrlFormat, endpointDomain, suite.getCompany(), suite.getProject(), suite.getCorrelationId());
  }

  private void process(RunnerTerminator runnerTerminator, String reportUrl, Message received) throws JMSException, AETException {
    MessageProcessor processor = ProcessorFactory.produce(received, reportUrl, redirectWriter, runnerTerminator);
    if (processor != null) {
      processor.process();
    }
  }

  private void close() throws AETException {
    try {
      if (consumer != null) {
        consumer.close();
      }
      producer.close();
      session.close();
      connection.close();
    } catch (JMSException e) {
      throw new AETException("JMS Failed to close connections", e);
    }
  }

}
