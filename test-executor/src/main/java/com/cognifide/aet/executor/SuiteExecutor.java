/**
 * AET
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.executor;

import com.cognifide.aet.communication.api.execution.ProcessingStatus;
import com.cognifide.aet.communication.api.execution.SuiteExecutionResult;
import com.cognifide.aet.communication.api.execution.SuiteStatusResult;
import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.executor.configuration.SuiteExecutorConf;
import com.cognifide.aet.executor.http.HttpSuiteExecutionResultWrapper;
import com.cognifide.aet.executor.model.TestRun;
import com.cognifide.aet.executor.model.TestSuiteRun;
import com.cognifide.aet.executor.xmlparser.api.ParseException;
import com.cognifide.aet.executor.xmlparser.api.TestSuiteParser;
import com.cognifide.aet.executor.xmlparser.xml.XmlTestSuiteParser;
import com.cognifide.aet.rest.LockService;
import com.cognifide.aet.rest.helpers.ReportConfigurationManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service is responsible for executing the test suite and maintaining the processing statuses.
 * It creates the {@link SuiteRunner} and {@link SuiteStatusResult} queue for each test suite run
 * and keeps those items in cache.
 */
@Component(service = SuiteExecutor.class, immediate = true)
@Designate(ocd = SuiteExecutorConf.class)
public class SuiteExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(SuiteExecutor.class);

  private static final String RUNNER_IN_QUEUE = "AET.runner-in";

  private static final String HTML_REPORT_URL_FORMAT = "%s/report.html?company=%s&project=%s&correlationId=%s";

  private static final String XUNIT_REPORT_URL_FORMAT = "%s/xunit?company=%s&project=%s&correlationId=%s";

  private static final String LOCKED_SUITE_MESSAGE = "Suite is currently locked. Please try again later.";

  private static final long CACHE_EXPIRATION_TIMEOUT = 20000L;

  private SuiteExecutorConf config;

  @Reference
  private JmsConnection jmsConnection;

  @Reference
  private LockService lockService;

  @Reference
  private ReportConfigurationManager reportConfigurationManager;

  @Reference
  private SuiteValidator suiteValidator;

  @Reference
  private SuiteFactory suiteFactory;

  private Cache<String, SuiteRunner> suiteRunnerCache;

  private Cache<String, Queue<SuiteStatusResult>> suiteStatusCache;

  private CacheUpdater cacheUpdater;

  private SuiteStatusHandler suiteStatusHandler;

  @Activate
  public void activate(SuiteExecutorConf config) {
    this.config = config;

    suiteRunnerCache = CacheBuilder.newBuilder()
        .expireAfterAccess(CACHE_EXPIRATION_TIMEOUT, TimeUnit.MILLISECONDS)
        .removalListener(new RunnerCacheRemovalListener())
        .build();

    suiteStatusCache = CacheBuilder.newBuilder()
        .expireAfterAccess(CACHE_EXPIRATION_TIMEOUT, TimeUnit.MILLISECONDS)
        .build();

    cacheUpdater = new CacheUpdater(suiteRunnerCache, suiteStatusCache, lockService);
    suiteStatusHandler = new SuiteStatusHandler(suiteStatusCache);
  }

  /**
   * Executes the test suite provided as a parameter.
   *
   * @param suiteString - content of the test suite XML file
   * @param domain - overrides domain defined in the suite file
   * @param patternCorrelationId - optional pattern to set, this is a correlation ID of a suite
   * that will be used as patterns source
   * @param patternSuite - optional pattern to set, this is a name of a suite whose latest version
   * will be used as patterns source. This parameter is ignored if patternCorrelationId is set
   * @param projectCheckSum
   * @return status of the suite execution
   */
  HttpSuiteExecutionResultWrapper execute(String suiteString, String name, String domain,
      String patternCorrelationId, String patternSuite, String projectCheckSum) {
    SuiteRunner suiteRunner = null;
    HttpSuiteExecutionResultWrapper result;

    TestSuiteParser xmlFileParser = new XmlTestSuiteParser();
    try {
      TestSuiteRun testSuiteRun = xmlFileParser.parse(suiteString);
      testSuiteRun = overrideDomainOrNameIfDefined(testSuiteRun, name, domain);
      testSuiteRun.setPatternCorrelationId(patternCorrelationId);
      testSuiteRun.setPatternSuite(patternSuite);

      String validationError = suiteValidator.validateTestSuiteRun(testSuiteRun);
      if (validationError == null) {
        final Suite suite = suiteFactory.suiteFromTestSuiteRun(testSuiteRun);
        suite.setCheckSumProject(projectCheckSum);//todo temporary  for test only!
        suite.validate(Sets.newHashSet("version", "runTimestamp"));

        if (lockTestSuite(suite)) {
          suiteRunner = createSuiteRunner(suite);
          suiteRunner.runSuite();

          String statusUrl = getStatusUrl(suite);
          String htmlReportUrl = getReportUrl(HTML_REPORT_URL_FORMAT,
              reportConfigurationManager.getReportDomain(), suite);
          String xunitReportUrl = getReportUrl(XUNIT_REPORT_URL_FORMAT, StringUtils.EMPTY, suite);
          result = HttpSuiteExecutionResultWrapper.wrap(
              SuiteExecutionResult.createSuccessResult(suite.getCorrelationId(), statusUrl,
                  htmlReportUrl, xunitReportUrl));
        } else {
          result = HttpSuiteExecutionResultWrapper.wrapError(
              SuiteExecutionResult.createErrorResult(LOCKED_SUITE_MESSAGE), HttpStatus.SC_LOCKED);
        }
      } else {
        result = HttpSuiteExecutionResultWrapper
            .wrapError(SuiteExecutionResult.createErrorResult(validationError),
                HttpStatus.SC_BAD_REQUEST);
      }
    } catch (ParseException | ValidatorException e) {
      LOGGER.error("Failed to run test suite", e);
      result = HttpSuiteExecutionResultWrapper
          .wrapError(SuiteExecutionResult.createErrorResult(e.getMessage()),
              HttpStatus.SC_BAD_REQUEST);

    } catch (JMSException e) {
      LOGGER.error("Fatal error", e);
      result = HttpSuiteExecutionResultWrapper
          .wrapError(SuiteExecutionResult.createErrorResult(e.getMessage()),
              HttpStatus.SC_INTERNAL_SERVER_ERROR);
      if (suiteRunner != null) {
        suiteRunner.close();
      }
    }

    return result;
  }

  /**
   * Returns the status of test suite processing.
   *
   * @param correlationId - identifier of a suite run.
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

  private TestSuiteRun overrideDomainOrNameIfDefined(TestSuiteRun testSuiteRun, String name,
      String domain) {
    TestSuiteRun localTestSuiteRun = testSuiteRun;
    if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(domain)) {
      List<TestRun> tests = Lists.newArrayList(localTestSuiteRun.getTestRunMap().values());
      String overriddenName = StringUtils.defaultString(name, localTestSuiteRun.getName());
      String overridenDomain = StringUtils.defaultString(domain, localTestSuiteRun.getDomain());
      localTestSuiteRun = new TestSuiteRun(localTestSuiteRun, overriddenName, overridenDomain,
          tests);
    }
    return localTestSuiteRun;
  }

  private boolean lockTestSuite(Suite suite) {
    String suiteIdentifier = suite.getSuiteIdentifier();
    String correlationId = suite.getCorrelationId();
    LOGGER.debug("locking suite: '{}' with correlation id: '{}'", suiteIdentifier, correlationId);
    return lockService.trySetLock(suiteIdentifier, correlationId);
  }

  private SuiteRunner createSuiteRunner(Suite suite) throws JMSException {
    Session session = jmsConnection.getJmsSession();
    SuiteRunner suiteRunner = new SuiteRunner(session, cacheUpdater,
        suiteStatusHandler, suite, RUNNER_IN_QUEUE, config.messageReceiveTimeout());
    suiteRunnerCache.put(suite.getCorrelationId(), suiteRunner);
    suiteStatusCache.put(suite.getCorrelationId(), new ConcurrentLinkedQueue<SuiteStatusResult>());
    return suiteRunner;
  }

  private String getReportUrl(String format, String domain, Suite suite) {
    return String
        .format(format, domain, suite.getCompany(), suite.getProject(), suite.getCorrelationId());
  }

  private String getStatusUrl(Suite suite) {
    return SuiteStatusServlet.SERVLET_PATH + "/" + suite.getCorrelationId();
  }

  private static class RunnerCacheRemovalListener implements RemovalListener<String, SuiteRunner> {

    @Override
    public void onRemoval(RemovalNotification<String, SuiteRunner> removalNotification) {
      removalNotification.getValue().terminate();
    }
  }
}
