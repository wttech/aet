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
package com.cognifide.aet.worker.impl;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.cognifide.aet.job.api.collector.CollectorFactory;
import com.cognifide.aet.job.api.comparator.ComparatorFactory;
import com.cognifide.aet.job.api.datafilter.DataFilterFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * JobRegistryImplTest
 *
 * @Author: Maciej Laskowski
 * @Date: 13.04.15
 */
public class JobRegistryImplTest {

  private JobRegistryImpl tested;

  @Before
  public void setUp() throws Exception {
    tested = new JobRegistryImpl();
  }

  @Test
  public void bindCollectorFactory_expectCollectorInJobRegistry() throws Exception {
    tested.bindCollectorFactory(mockCollectorFactory("collectorA"));
    assertNotNull(tested.getCollectorFactory("collectorA"));
    assertTrue(tested.hasJob("collectorA"));
  }

  @Test
  public void unbindCollectorFactory_expectCollectorRemovedFromJobRegistry() throws Exception {
    CollectorFactory collectorFactory = mockCollectorFactory("collectorA");
    tested.bindCollectorFactory(collectorFactory);
    assertNotNull(tested.getCollectorFactory("collectorA"));
    tested.unbindCollectorFactory(collectorFactory);
    assertNull(tested.getCollectorFactory("collectorA"));
    assertFalse(tested.hasJob("collectorA"));
  }

  @Test
  public void bindComparatorFactory_expectComparatorInJobRegistry() throws Exception {
    tested.bindComparatorFactory(mockComparatorFactory("typeA", "comparatorA", 100));
    assertNotNull(tested.getComparatorFactory("comparatorA"));
  }

  @Test
  public void unbindComparatorFactory_expectComparatorRemovedFromJobRegistry() throws Exception {
    ComparatorFactory comparatorFactory = mockComparatorFactory("typeA", "comparatorA", 100);
    tested.bindComparatorFactory(comparatorFactory);
    assertNotNull(tested.getComparatorFactory("comparatorA"));
    tested.unbindComparatorFactory(comparatorFactory);
    assertNull(tested.getComparatorFactory("comparatorA"));
  }

  @Test
  public void bindComparatorFactory_whenComparatorTypeExistsInJobRegistry_expectComparatorWithHighestRankingAsDefault()
      throws Exception {
    ComparatorFactory comparatorFactoryA = mockComparatorFactory("typeA", "comparatorA", 100);
    ComparatorFactory comparatorFactoryB = mockComparatorFactory("typeA", "comparatorB", 150);
    ComparatorFactory comparatorFactoryC = mockComparatorFactory("typeA", "comparatorC", 120);
    tested.bindComparatorFactory(comparatorFactoryA);
    tested.bindComparatorFactory(comparatorFactoryB);
    tested.bindComparatorFactory(comparatorFactoryC);
    assertThat(tested.getComparatorFactoryForType("typeA"), is(comparatorFactoryB));
  }

  @Test
  public void unbindComparatorFactory_whenComparatorTypeExistsInJobRegistry_expectComparatorWithHighestRankingAsDefault()
      throws Exception {
    ComparatorFactory comparatorFactoryA = mockComparatorFactory("typeA", "comparatorA", 100);
    ComparatorFactory comparatorFactoryB = mockComparatorFactory("typeA", "comparatorB", 150);
    ComparatorFactory comparatorFactoryC = mockComparatorFactory("typeA", "comparatorC", 120);
    tested.bindComparatorFactory(comparatorFactoryA);
    tested.bindComparatorFactory(comparatorFactoryB);
    tested.bindComparatorFactory(comparatorFactoryC);
    assertThat(tested.getComparatorFactoryForType("typeA"), is(comparatorFactoryB));
    tested.unbindComparatorFactory(comparatorFactoryB);
    assertThat(tested.getComparatorFactoryForType("typeA"), is(comparatorFactoryC));
  }

  @Test
  public void bindDataModifierFactory_expectModifierInJobRegistry() throws Exception {
    tested.bindDataModifierFactory(mockDataModifierFactory("modifierA"));
    assertNotNull(tested.getDataModifierFactory("modifierA"));
  }

  @Test
  public void unbindDataModifierFactory_expectModifierRemovedFromJobRegistry() throws Exception {
    DataFilterFactory dataFilterFactory = mockDataModifierFactory("modifierA");
    tested.bindDataModifierFactory(dataFilterFactory);
    assertNotNull(tested.getDataModifierFactory("modifierA"));
    tested.unbindDataModifierFactory(dataFilterFactory);
    assertNull(tested.getDataModifierFactory("modifierA"));
  }

  private static ComparatorFactory mockComparatorFactory(String type, String name, int ranking) {
    ComparatorFactory comparatorFactory = Mockito.mock(ComparatorFactory.class);
    when(comparatorFactory.getType()).thenReturn(type);
    when(comparatorFactory.getName()).thenReturn(name);
    when(comparatorFactory.getRanking()).thenReturn(ranking);
    return comparatorFactory;
  }

  private static CollectorFactory mockCollectorFactory(String name) {
    CollectorFactory collectorFactory = Mockito.mock(CollectorFactory.class);
    when(collectorFactory.getName()).thenReturn(name);
    return collectorFactory;
  }

  private static DataFilterFactory mockDataModifierFactory(String name) {
    DataFilterFactory dataFilterFactory = Mockito.mock(DataFilterFactory.class);
    when(dataFilterFactory.getName()).thenReturn(name);
    return dataFilterFactory;
  }
}
