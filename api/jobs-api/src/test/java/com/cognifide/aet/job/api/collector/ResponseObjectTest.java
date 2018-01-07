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
package com.cognifide.aet.job.api.collector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class ResponseObjectTest {

  private ResponseObject responseObject;

  private byte[] byteArray;

  @Before
  public void setUp() throws Exception {
    byteArray = "content".getBytes();
    responseObject = new ResponseObject(new HashMap<String, List<String>>(), byteArray);
  }

  @Test
  public void getContent_NullUsedInConstructor_Null() throws Exception {
    responseObject = new ResponseObject(new HashMap<String, List<String>>(), null);
    assertTrue(responseObject.getContent() == null);
  }

  @Test
  public void getContent_ByteArrayUsedInConstructor_ArraysAreDifferentObjects() throws Exception {
    assertFalse(responseObject.getContent() == byteArray);
    byte[] responseArray = responseObject.getContent();
    byteArray = "other".getBytes();
    assertFalse(Arrays.equals(responseArray, byteArray));
  }

  @Test
  public void constructor_ByteArrayUsedInConstructorWasChanged_ContentOfArraysAreDifferent()
      throws Exception {
    byteArray = "other".getBytes();
    byte[] responseArray = responseObject.getContent();
    assertFalse(Arrays.equals(responseArray, byteArray));
  }

  @Test
  public void getContent_ByteArrayUsedInConstructor_ContentOfArraysAreEqual() throws Exception {
    assertTrue(Arrays.equals(responseObject.getContent(), byteArray));
  }
}
