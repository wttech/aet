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
package com.cognifide.aet.cleaner.processors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cognifide.aet.cleaner.context.CleanerContext;
import com.cognifide.aet.cleaner.processors.exchange.ReferencedArtifactsMessageBody;
import com.cognifide.aet.vs.ArtifactsDAO;
import com.cognifide.aet.vs.DBKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RemoveArtifactsProcessorTest {

  private static final Set<String> ONE_TO_SEVEN_ARTIFACTS_ID_SET = new HashSet<>(Arrays
      .asList("1", "2", "3", "4", "5", "6", "7"));

  private static final Set<String> ONE_TO_FIVE_ARTIFACTS_ID_SET = new HashSet<>(Arrays
      .asList("1", "2", "3", "4", "5"));

  private static final Set<String> SIX_TO_SEVEN_ARTIFACTS_ID_SET = new HashSet<>(Arrays
      .asList("6", "7"));

  private Exchange exchange;

  @Mock
  private ArtifactsDAO artifactDAO;

  @Mock
  private CleanerContext cleanerContext;

  @Mock
  private DBKey dbKey;

  private RemoveArtifactsProcessor removeArtifactsProcessor;

  @Before
  public void setUp() {
    when(cleanerContext.isDryRun()).thenReturn(false);

    CamelContext ctx = new DefaultCamelContext();
    exchange = new DefaultExchange(ctx);

    Message in = exchange.getIn();
    in.setBody(new ReferencedArtifactsMessageBody("", dbKey));
    in.setHeader(CleanerContext.KEY_NAME, cleanerContext);
  }

  @Test
  public void checkIfRemoveArtifactsWasCalled_whenDryRunIsFalse_expectTrue() throws Exception {
    when(artifactDAO.getArtifactsIds(any(DBKey.class)))
        .thenReturn(new HashSet<>(ONE_TO_SEVEN_ARTIFACTS_ID_SET));
    setArtifactsIdToKeep(ONE_TO_FIVE_ARTIFACTS_ID_SET);

    removeArtifactsProcessor = new RemoveArtifactsProcessor(artifactDAO);
    removeArtifactsProcessor.process(exchange);

    verify(artifactDAO, times(1)).removeArtifacts(any(DBKey.class), any(Set.class));
    verify(artifactDAO, times(1)).getArtifactsIds(any(DBKey.class));
  }

  @Test
  public void checkIfRemoveArtifactsWasCalled_whenDryRunIsTrue_expectFalse() throws Exception {
    when(cleanerContext.isDryRun()).thenReturn(true);
    when(artifactDAO.getArtifactsIds(any(DBKey.class)))
        .thenReturn(new HashSet<>(ONE_TO_SEVEN_ARTIFACTS_ID_SET));
    setArtifactsIdToKeep(ONE_TO_FIVE_ARTIFACTS_ID_SET);

    removeArtifactsProcessor = new RemoveArtifactsProcessor(artifactDAO);
    removeArtifactsProcessor.process(exchange);

    verify(artifactDAO, times(0)).removeArtifacts(any(DBKey.class), any(Set.class));
    verify(artifactDAO, times(1)).getArtifactsIds(any(DBKey.class));
  }

  @Test
  public void check_substractArtifactsSets_expectSetOfTwoVariables() throws Exception {
    when(artifactDAO.getArtifactsIds(any(DBKey.class)))
        .thenReturn(new HashSet<>(ONE_TO_SEVEN_ARTIFACTS_ID_SET));
    setArtifactsIdToKeep(ONE_TO_FIVE_ARTIFACTS_ID_SET);
    ReferencedArtifactsMessageBody messageBody = (ReferencedArtifactsMessageBody) exchange.getIn()
        .getBody();
    assertEquals(SIX_TO_SEVEN_ARTIFACTS_ID_SET,
        removeArtifactsProcessor.getArtifactsIdsToRemove(artifactDAO, messageBody));
  }

  @Test
  public void check_substractArtifactsSets_expectEmptySet() throws Exception {
    when(artifactDAO.getArtifactsIds(any(DBKey.class)))
        .thenReturn(new HashSet<>(ONE_TO_FIVE_ARTIFACTS_ID_SET));
    setArtifactsIdToKeep(ONE_TO_SEVEN_ARTIFACTS_ID_SET);
    ReferencedArtifactsMessageBody messageBody = (ReferencedArtifactsMessageBody) exchange.getIn()
        .getBody();
    assertTrue(removeArtifactsProcessor.getArtifactsIdsToRemove(artifactDAO, messageBody).isEmpty());
  }

  @Test
  public void check_substractArtifactsSets_expectSetOfFiveVariables() throws Exception {
    when(artifactDAO.getArtifactsIds(any(DBKey.class)))
        .thenReturn(new HashSet<>(ONE_TO_SEVEN_ARTIFACTS_ID_SET));
    setArtifactsIdToKeep(SIX_TO_SEVEN_ARTIFACTS_ID_SET);

    ReferencedArtifactsMessageBody messageBody = (ReferencedArtifactsMessageBody) exchange.getIn()
        .getBody();

    assertEquals(ONE_TO_FIVE_ARTIFACTS_ID_SET,
        removeArtifactsProcessor.getArtifactsIdsToRemove(artifactDAO, messageBody));
  }

  @Test
  public void check_substractArtifactsSetsWhenDbIsEmpty_expectEmptySet() throws Exception {
    when(artifactDAO.getArtifactsIds(any(DBKey.class)))
        .thenReturn(new HashSet<>());
    setArtifactsIdToKeep(SIX_TO_SEVEN_ARTIFACTS_ID_SET);
    ReferencedArtifactsMessageBody messageBody = (ReferencedArtifactsMessageBody) exchange.getIn()
        .getBody();
    assertTrue(removeArtifactsProcessor.getArtifactsIdsToRemove(artifactDAO, messageBody).isEmpty());
  }

  private void setArtifactsIdToKeep(Set<String> artifactsIdToKeep) {
    ReferencedArtifactsMessageBody body = (ReferencedArtifactsMessageBody) exchange.getIn()
        .getBody();
    body.setArtifactsToKeep(artifactsIdToKeep);
  }

}
