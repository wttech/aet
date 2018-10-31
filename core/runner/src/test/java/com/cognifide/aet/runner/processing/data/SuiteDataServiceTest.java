package com.cognifide.aet.runner.processing.data;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cognifide.aet.communication.api.metadata.Suite;
import com.cognifide.aet.communication.api.metadata.SuiteBuilder;
import com.cognifide.aet.communication.api.metadata.ValidatorException;
import com.cognifide.aet.vs.MetadataDAO;
import com.cognifide.aet.vs.StorageException;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SuiteDataServiceTest {

  private static final String CHECKSUM_01 = "checksum01";

  private static final String COMPANY = "company";

  private static final String PROJECT = "project";

  private static final String SUITE_NAME = "suite-name";

  private static final String patternCorrelationId = "company-project-suite-name-123456789";

  private static final String ID_LAST_VERSION_SUITE = "idLastVersionSuite";

  private final MetadataDAO metadataDAO = mock(MetadataDAO.class);

  private Suite currentRunWithChecksum;

  private Suite currentRunWithoutCorrelationId;

  private Suite lastVersionSuite;

  private Suite suiteHashcode;


  private SuiteDataService service;

  @Before
  public void setUp() {

    currentRunWithChecksum = new SuiteBuilder()
        .setCorrelationId(patternCorrelationId)
        .setCompany(COMPANY).setProject(PROJECT)
        .setName(SUITE_NAME)
        .setPatternCorrelationId("id2")
        .setProjectHashCode(CHECKSUM_01)
        .createSuite();

    currentRunWithoutCorrelationId = new SuiteBuilder()
        .setCorrelationId(patternCorrelationId)
        .setCompany(COMPANY).setProject(PROJECT)
        .setName(SUITE_NAME)
        .setPatternCorrelationId(null)
        .createSuite();

    lastVersionSuite = new SuiteBuilder().setCorrelationId(patternCorrelationId)
        .setCompany(COMPANY).setProject(PROJECT)
        .setName(SUITE_NAME)
        .setPatternCorrelationId(ID_LAST_VERSION_SUITE)
        .setProjectHashCode(CHECKSUM_01)
        .createSuite();
    lastVersionSuite.setVersion(generateRandomPositiveLong());

    suiteHashcode = new SuiteBuilder().setCorrelationId(patternCorrelationId)
        .setCompany(COMPANY).setProject(PROJECT)
        .setName(SUITE_NAME)
        .setPatternCorrelationId("id")
        .setProjectHashCode(CHECKSUM_01)
        .createSuite();
    service = new SuiteDataService(metadataDAO);//todo OSGI mock
  }


  @Test
  public void shouldReturnPatternBaseOnChecksum_TestWithChecksum() throws StorageException {
    when(metadataDAO.getSuiteByChecksum(anyObject(), anyString())).thenReturn(suiteHashcode);
    Assert.assertEquals(suiteHashcode.getCheckSumProject(), service.enrichWithPatterns(currentRunWithChecksum).getCheckSumProject());
  }

  @Test
  public void shouldReturnLastSuiteWithCurrentChecksum_TestWithChecksum() throws StorageException, ValidatorException {
    when(metadataDAO.getLatestRun(anyObject(), anyString())).thenReturn(lastVersionSuite);
    when(metadataDAO.getSuiteByChecksum(anyObject(), anyString())).thenReturn(null);
    when(metadataDAO.updateSuite(anyObject())).thenReturn(null);

    currentRunWithoutCorrelationId.setCheckSumProject(CHECKSUM_01);

    lastVersionSuite.setCheckSumProject(suiteHashcode.getCheckSumProject());

    Assert.assertEquals(currentRunWithoutCorrelationId.getCheckSumProject(), service.enrichWithPatterns(currentRunWithoutCorrelationId).getCheckSumProject());
    Assert.assertEquals(lastVersionSuite.getCorrelationId(), service.enrichWithPatterns(currentRunWithoutCorrelationId).getCorrelationId());
    Assert.assertEquals(Long.valueOf(lastVersionSuite.getVersion() + 1L), service.enrichWithPatterns(currentRunWithoutCorrelationId).getVersion());
  }

  private long generateRandomPositiveLong() {
    return ThreadLocalRandom.current().nextLong(1000);
  }

}