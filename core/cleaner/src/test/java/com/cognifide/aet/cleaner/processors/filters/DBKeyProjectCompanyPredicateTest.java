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
package com.cognifide.aet.cleaner.processors.filters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

import com.cognifide.aet.vs.DBKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DBKeyProjectCompanyPredicateTest {

  @Mock
  private DBKey dbKey;

  @Test
  public void apply_whenProjectAndCompanyFiltersAreBlank_expectTrue() throws Exception {
    assertTrue(new DBKeyProjectCompanyPredicate(null, null).apply(dbKey));
  }

  @Test
  public void apply_whenProjectMatchDbKeyAndCompanyBlank_expectTrue() throws Exception {
    when(dbKey.getProject()).thenReturn("filterP");
    assertTrue(new DBKeyProjectCompanyPredicate(null, "filterP").apply(dbKey));
  }

  @Test
  public void apply_whenCompanyMatchDbKeyAndProjectBlank_expectTrue() throws Exception {
    when(dbKey.getCompany()).thenReturn("filterC");
    assertTrue(new DBKeyProjectCompanyPredicate("filterC", null).apply(dbKey));
  }

  @Test
  public void apply_whenProjectDoesNotMatchDbKey_expectFalse() throws Exception {
    when(dbKey.getProject()).thenReturn("filterA");
    assertFalse(new DBKeyProjectCompanyPredicate(null, "filterB").apply(dbKey));
  }

  @Test
  public void apply_whenCompanyDoesNotMatchDbKey_expectFalse() throws Exception {
    when(dbKey.getCompany()).thenReturn("filterA");
    assertFalse(new DBKeyProjectCompanyPredicate("filterB", null).apply(dbKey));
  }
}
