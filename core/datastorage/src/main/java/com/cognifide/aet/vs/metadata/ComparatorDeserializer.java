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
package com.cognifide.aet.vs.metadata;

import com.cognifide.aet.communication.api.metadata.Comparator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ComparatorDeserializer extends CompatibilityDeserializer<Comparator> {

  private static final String RESULT_FIELD = "stepResult";

  private static final String RESULTS_COLLECTION_FIELD = "stepResults";

  @Override
  protected void deserializeIncompatible(JsonObject comparatorObject,
      JsonElement comparatorResultElement) {
    JsonArray resultsArray = new JsonArray();
    resultsArray.add(comparatorResultElement);
    comparatorObject.add(RESULTS_COLLECTION_FIELD, resultsArray);

    comparatorObject.remove(RESULT_FIELD);
  }

  @Override
  protected String getPropertyFieldName() {
    return RESULT_FIELD;
  }
}
