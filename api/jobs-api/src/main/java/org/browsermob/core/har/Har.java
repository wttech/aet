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
/*
 * Copyright [2016] [http://bmp.lightbody.net/]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.browsermob.core.har;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.codehaus.jackson.map.ObjectMapper;

public class Har {

  private HarLog log;

  public Har() {
  }

  public Har(HarLog log) {
    this.log = log;
  }

  public HarLog getLog() {
    return log;
  }

  public void setLog(HarLog log) {
    this.log = log;
  }

  public void writeTo(Writer writer) throws IOException {
    ObjectMapper om = new ObjectMapper();
    om.writeValue(writer, this);
  }

  public void writeTo(OutputStream os) throws IOException {
    ObjectMapper om = new ObjectMapper();
    om.writeValue(os, this);
  }

  public void writeTo(File file) throws IOException {
    ObjectMapper om = new ObjectMapper();
    om.writeValue(file, this);
  }
}
