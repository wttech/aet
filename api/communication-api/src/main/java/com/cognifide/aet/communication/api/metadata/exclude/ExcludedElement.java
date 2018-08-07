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
package com.cognifide.aet.communication.api.metadata.exclude;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

public class ExcludedElement implements Serializable {
  private static final long serialVersionUID = 692282363549228800L;

  private final Point point;

  private final Dimension dimension;

  public ExcludedElement(Point point, Dimension dimension) {
    this.point = point;
    this.dimension = dimension;
  }

  public Point getPoint() {
    return point;
  }

  public Dimension getDimension() {
    return dimension;
  }
}
