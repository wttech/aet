/**
 * AET
 * <p>
 * Copyright (C) 2013 Cognifide Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cognifide.aet.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorsMap {

  private Map<String, List<Object>> map = new HashMap<>();

  public void mergeMap(String errorType, Object object) {
    map.merge(errorType, new ArrayList<>(Collections.singletonList(object)),
        (old, error) -> {
          old.addAll(error);
          return old;
        });
  }

  public Map<String, List<Object>> getMap() {
    return map;
  }
}
