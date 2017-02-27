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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.communication.api.execution.ProcessingStatus;
import com.cognifide.aet.communication.api.execution.SuiteExecutionResult;
import com.cognifide.aet.communication.api.execution.SuiteStatusResult;
import com.cognifide.aet.executor.model.CollectorStep;
import com.cognifide.aet.executor.model.ComparatorStep;
import com.cognifide.aet.executor.model.TestRun;
import com.cognifide.aet.executor.model.TestSuiteRun;
import com.cognifide.aet.executor.xmlparser.api.ParseException;
import com.cognifide.aet.executor.xmlparser.api.TestSuiteParser;
import com.cognifide.aet.executor.xmlparser.xml.XmlTestSuiteParser;
import com.cognifide.aet.rest.LockService;
import com.cognifide.aet.rest.helpers.ReportConfigurationManager;

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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * This service is responsible for executing the test suite and maintaining the processing statuses.
 * It creates the {@link SuiteRunner} and {@link SuiteStatusResult} queue for each test suite run
 * and keeps those items in cache.
 */
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

  private static final long DEFAULT_MESSAGE_RECEIVE_TIMEOUT = 20000L;

  @Property(name = MESSAGE_RECEIVE_TIMEOUT_PROPERTY_NAME, label = "ActiveMQ message receive timeout",
      description = "ActiveMQ message receive timeout", longValue = DEFAULT_MESSAGE_RECEIVE_TIMEOUT)
  private Long messageReceiveTimeout;

  private static final String CACHE_EXPIRATION_TIMEOUT_PROPERTY_NAME = "cacheExpirationTimeout";

  private static final long DEFAULT_CACHE_EXPIRATION_TIMEOUT = 30000L;

  @Property(name = CACHE_EXPIRATION_TIMEOUT_PROPERTY_NAME, label = "Suite runner cache expiration timeout",
      description = "Suite runner cache expiration timeout. Should be longer than 'ActiveMQ message receive timeout'.",
      longValue = DEFAULT_CACHE_EXPIRATION_TIMEOUT)
  private Long cacheExpirationTimeout;

  @Reference
  private JmsConnection jmsConnection;

  @Reference
  private LockService lockService;

  @Reference
  private ReportConfigurationManager reportConfigurationManager;

  private Cache<String, SuiteRunner> suiteRunnerCache;

  private Cache<String, Queue<SuiteStatusResult>> suiteStatusCache;

  private RunnerCacheUpdater runnerCacheUpdater;

  private SuiteStatusHandler suiteStatusHandler;

  @Activate
  public void activate(Map properties) {
    messageReceiveTimeout = PropertiesUtil.toLong(
        properties.get(MESSAGE_RECEIVE_TIMEOUT_PROPERTY_NAME), DEFAULT_MESSAGE_RECEIVE_TIMEOUT);
    cacheExpirationTimeout = PropertiesUtil.toLong(
        properties.get(CACHE_EXPIRATION_TIMEOUT_PROPERTY_NAME), DEFAULT_CACHE_EXPIRATION_TIMEOUT);

    suiteRunnerCache = CacheBuilder.newBuilder()
        .expireAfterAccess(cacheExpirationTimeout, TimeUnit.MILLISECONDS)
        .removalListener(new RunnerCacheRemovalListener())
        .build();

    suiteStatusCache = CacheBuilder.newBuilder()
        .expireAfterAccess(cacheExpirationTimeout, TimeUnit.MILLISECONDS)
        .build();

    runnerCacheUpdater = new RunnerCacheUpdater(suiteRunnerCache);
    suiteStatusHandler = new SuiteStatusHandler(suiteStatusCache);
  }

  /**
   * Executes the test suite provided as a parameter.
   *
   * @param suiteString - content of the test suite XML file
   * @param domain - overrides domain defined in the suite file
   * @param endpointDomain - domain under which AET system is available, used to create links to
   *                       xUnit report and status check
   * @return status of the suite execution
   */
  public SuiteExecutionResult execute(String suiteString, String domain, String endpointDomain) {
    SuiteRunner suiteRunner = null;
    SuiteExecutionResult result;

    TestSuiteParser xmlFileParser = new XmlTestSuiteParser();
    try {
      TestSuiteRun testSuiteRun = xmlFileParser.parse(suiteString);
      testSuiteRun = overrideDomainIfDefined(testSuiteRun, domain);

      String validationResult = validateTestSuiteRun(testSuiteRun);
      if (validationResult == null) {
        final Suite suite = new SuiteFactory().suiteFromTestSuiteRun(testSuiteRun);
        suite.validate(Sets.newHashSet("version", "runTimestamp"));

        if (lockTestSuite(suite)) {
          suiteRunner = createSuiteRunner(suite);
          suiteRunner.runSuite();

          String statusUrl = getStatusUrl(endpointDomain, suite);
          String htmlReportUrl = getReportUrl(HTML_REPORT_URL_FORMAT,
              reportConfigurationManager.getReportDomain(), suite);
          String xunitReportUrl = getReportUrl(XUNIT_REPORT_URL_FORMAT, endpointDomain, suite);
          result = SuiteExecutionResult.createSuccessResult(suite.getCorrelationId(), statusUrl,
              htmlReportUrl, xunitReportUrl);
        } else {
          result = SuiteExecutionResult.createErrorResult("Suite is currently locked");
        }
      } else {
        result = SuiteExecutionResult.createErrorResult(validationResult);
      }
    } catch (ParseException | JMSException | ValidatorException e) {
      LOGGER.error("Failed to run test suite", e);
      result = SuiteExecutionResult.createErrorResult(e.getMessage());
      if (suiteRunner != null) {
        suiteRunner.close();
      }
    }

    return result;
  }

  /**
   * Returns the status of test suite processing.
   *
   * @param correlationId
   * @return status of the test suite run identified by provided correlation ID
   */
  public SuiteStatusResult getExecutionStatus(String correlationId) {
    SuiteStatusResult result = null;

    Queue<SuiteStatusResult> statusQueue = suiteStatusCache.getIfPresent(correlationId);
    if (statusQueue != null) {
      result = statusQueue.poll();
      if (result == null) {
        result = new SuiteStatusResult(ProcessingStatus.UNKNOWN);
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

  private boolean lockTestSuite(Suite suite) {
    return lockService.trySetLock(suite.getSuiteIdentifier(), suite.getCorrelationId());
  }

  private SuiteRunner createSuiteRunner(Suite suite) throws JMSException {
    Session session = jmsConnection.getJmsSession();
    SuiteRunner suiteRunner = new SuiteRunner(session, lockService, runnerCacheUpdater,
        suiteStatusHandler, suite, RUNNER_IN_QUEUE, messageReceiveTimeout);
    suiteRunnerCache.put(suite.getCorrelationId(), suiteRunner);
    suiteStatusCache.put(suite.getCorrelationId(), new ConcurrentLinkedQueue<SuiteStatusResult>());
    return suiteRunner;
  }

  private String getReportUrl(String format, String domain, Suite suite) {
    return String.format(format, domain, suite.getCompany(), suite.getProject(), suite.getCorrelationId());
  }

  private String getStatusUrl(String domain, Suite suite) {
    return domain + SuiteStatusServlet.SERVLET_PATH + "/" + suite.getCorrelationId();
  }

  private static class RunnerCacheRemovalListener implements RemovalListener<String, SuiteRunner> {

    @Override
    public void onRemoval(RemovalNotification<String, SuiteRunner> removalNotification) {
      removalNotification.getValue().terminate();
    }
  }
}
