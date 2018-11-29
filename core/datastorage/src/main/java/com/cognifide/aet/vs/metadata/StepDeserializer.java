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

import com.cognifide.aet.communication.api.metadata.Step;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class StepDeserializer extends CompatibilityDeserializer<Step> {

  private static final String PATTERN_FIELD = "pattern";

  private static final String PATTERNS_COLLECTION_FIELD = "patterns";

  @Override
  protected void deserializeIncompatible(JsonObject stepObject, JsonElement patternElement) {
    String pattern = patternElement.getAsString();

    JsonObject patternObject = new JsonObject();
    patternObject.addProperty(PATTERN_FIELD, pattern);

    JsonArray patternsArray = new JsonArray();
    patternsArray.add(patternObject);
    stepObject.add(PATTERNS_COLLECTION_FIELD, patternsArray);

    stepObject.remove(PATTERN_FIELD);
  }

  @Override
  protected String getPropertyFieldName() {
    return PATTERN_FIELD;
  }
}
