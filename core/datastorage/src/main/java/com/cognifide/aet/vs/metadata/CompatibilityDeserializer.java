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

import static com.cognifide.aet.vs.metadata.DocumentConverter.DESERIALIZERS;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.HashMap;

public abstract class CompatibilityDeserializer<T> implements JsonDeserializer<T> {

  @Override
  public T deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

    JsonObject stepObject = jsonElement.getAsJsonObject();
    JsonElement element = stepObject.get(getPropertyFieldName());

    if (element != null) {
      deserializeIncompatible(stepObject, element);
    }

    return continueMapping(jsonElement, type);
  }

  private T continueMapping(JsonElement jsonElement, Type type) {
    HashMap<Class, JsonDeserializer> deserializers = new HashMap<>(DESERIALIZERS);

    removeCurrentDeserializer(type, deserializers);

    GsonBuilder builder = new GsonBuilder();
    deserializers.forEach(builder::registerTypeAdapter);
    return builder.create().fromJson(jsonElement, type);

  }

  private void removeCurrentDeserializer(Type type,
      HashMap<Class, JsonDeserializer> deserializers) {
    try {
      deserializers.remove(Class.forName(type.getTypeName()));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  protected abstract String getPropertyFieldName();

  protected abstract void deserializeIncompatible(JsonObject object, JsonElement element);
}
