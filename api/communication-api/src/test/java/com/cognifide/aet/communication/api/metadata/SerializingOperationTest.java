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
package com.cognifide.aet.communication.api.metadata;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

public class SerializingOperationTest {

  @Test
  public void getParameters_whenObjectIsSerializedAndDeserialized_expectedNotEmptyMap()
      throws Exception {
    Operation operationInput = new Operation("test");
    operationInput.addParameter("key-test", "value-test");
    Assert.assertFalse(operationInput.getParameters().isEmpty());
    Assert.assertEquals(1, operationInput.getParameters().size());

    Gson gson = new Gson();
    String json = gson.toJson(operationInput);
    Operation operationOutput = gson.fromJson(json, Operation.class);

    Assert.assertFalse(operationOutput.getParameters().isEmpty());
    Assert.assertEquals(1, operationOutput.getParameters().size());
  }

  @Test
  public void getParameters_whenThereIsNoParametersInDeserializedJson_expectedEmptyMap()
      throws Exception {
    Gson gson = new Gson();
    String json = "{\"type\":\"test\"}";
    Operation operationOutput = gson.fromJson(json, Operation.class);
    Assert.assertTrue(operationOutput.getParameters().isEmpty());
  }
}
