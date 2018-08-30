package com.cognifide.aet.runner.processing;

import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.runner.RunnerConfiguration;
import com.cognifide.aet.runner.processing.data.SuiteDataService;
import com.cognifide.aet.runner.processing.data.SuiteIndexWrapper;
import com.cognifide.aet.vs.StorageException;
import java.util.concurrent.Callable;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jms.Destination;

abstract class ProcessorStrategy implements Callable<String> {

  protected static final Logger LOGGER = LoggerFactory.getLogger(SuiteExecutionTask.class);

  //private final Suite suite;
  protected final Destination jmsReplyTo;
  protected final SuiteDataService suiteDataService;
  protected final RunnerConfiguration runnerConfiguration;
  protected final SuiteExecutionFactory suiteExecutionFactory;

  protected SuiteIndexWrapper indexedSuite;

  protected MessagesSender messagesSender;
  protected SuiteProcessor suiteProcessor;

  public ProcessorStrategy(Destination jmsReplyTo,
      SuiteDataService suiteDataService, RunnerConfiguration runnerConfiguration,
      SuiteExecutionFactory suiteExecutionFactory) {
    this.jmsReplyTo = jmsReplyTo;
    this.suiteDataService = suiteDataService;
    this.runnerConfiguration = runnerConfiguration;
    this.suiteExecutionFactory = suiteExecutionFactory;
  }

  @Override
  public String call() {
    try {
      prepareSuiteWrapper();
      init();
      process();
      save();
    } catch (StorageException | JMSException | ValidatorException e) {
//      LOGGER.error("Error during processing suite {}", suite, e);
//      FinishedSuiteProcessingMessage message = new FinishedSuiteProcessingMessage(Status.FAILED,
//          suite.getCorrelationId());
//      message.addError(e.getMessage());
//      messagesSender.sendMessage(message);
    } finally {
      cleanup();
    }
//    return suite.getCorrelationId();
    return "";
  }

  protected void prepareSuiteWrapper() throws StorageException {
//    LOGGER.debug("Fetching suite patterns {}", suite);
//    indexedSuite = new SuiteIndexWrapper(suiteDataService.enrichWithPatterns(suite));
  }

  protected void init() throws JMSException {
//    LOGGER.debug("Initializing suite processors {}", suite);
//    messagesSender = suiteExecutionFactory.newMessagesSender(jmsReplyTo);
//    suiteProcessor = new SuiteProcessor(suiteExecutionFactory, indexedSuite, runnerConfiguration,
//        messagesSender);
  }

  protected void process() throws JMSException {
    LOGGER.info("Start processing: {}", indexedSuite.get());
    suiteProcessor.startProcessing();
  }


  protected void save() throws ValidatorException, StorageException {
//    LOGGER.debug("Persisting suite {}", suite);
//    suiteDataService.saveSuite(indexedSuite.get());
//    messagesSender.sendMessage(
//        new FinishedSuiteProcessingMessage(FinishedSuiteProcessingMessage.Status.OK,
//            suite.getCorrelationId()));
  }

  protected void cleanup() {
//    LOGGER.debug("Cleaning up suite {}", suite);
//    messagesSender.close();
//    suiteProcessor.cleanup();
  }

}
