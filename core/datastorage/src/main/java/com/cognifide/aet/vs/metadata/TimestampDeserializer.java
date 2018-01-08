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

import com.cognifide.aet.communication.api.metadata.Suite;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


class TimestampDeserializer implements JsonDeserializer<Suite.Timestamp> {

  @Override
  public Suite.Timestamp deserialize(JsonElement json, Type typeOfT,
      JsonDeserializationContext context) {
    Suite.Timestamp result = null;
    if (json != null) {
      final Set<Map.Entry<String, JsonElement>> jsonEntrySet = ((JsonObject) json).entrySet();
      final Iterator<Map.Entry<String, JsonElement>> iterator = jsonEntrySet.iterator();
      if (iterator.hasNext()) {
        final JsonElement value = iterator.next().getValue();
        result = new Suite.Timestamp(value.getAsLong());
      }
    }
    return result;
  }

}
