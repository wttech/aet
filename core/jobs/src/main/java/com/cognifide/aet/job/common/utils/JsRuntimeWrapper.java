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
package com.cognifide.aet.job.common.utils;

import com.cognifide.aet.job.api.exceptions.ProcessingException;
import com.google.common.base.Charsets;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import net.sourceforge.htmlunit.corejs.javascript.Callable;
import net.sourceforge.htmlunit.corejs.javascript.Context;
import net.sourceforge.htmlunit.corejs.javascript.ContextFactory;
import net.sourceforge.htmlunit.corejs.javascript.NativeJSON;
import net.sourceforge.htmlunit.corejs.javascript.Scriptable;
import net.sourceforge.htmlunit.corejs.javascript.ScriptableObject;
import net.sourceforge.htmlunit.corejs.javascript.tools.shell.Global;
import net.sourceforge.htmlunit.corejs.javascript.tools.shell.Main;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

public class JsRuntimeWrapper {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JsRuntimeWrapper.class);

  private final BundleContext bundleContext;

  private Global global;

  private Context cx;

  private ByteArrayOutputStream out;

  public JsRuntimeWrapper(BundleContext bundleContext) {
    this.bundleContext = bundleContext;
    init();
  }

  private void init() {
    cx = ContextFactory.getGlobal().enterContext();
    cx.setOptimizationLevel(-1);
    cx.setLanguageVersion(Context.VERSION_1_5);
    global = Main.getGlobal();
    if (!global.isInitialized()) {
      global.init(cx);
    }
    out = new ByteArrayOutputStream();
    PrintStream printStream = null;
    try {
      printStream = new PrintStream(out, false, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      LOGGER.error("Can't encode js runtime.", e);
    }
    Main.setOut(printStream);
    Main.setErr(printStream);
  }

  public void putProperty(String name, Object value) {
    ScriptableObject.putProperty(global, name, value);
  }

  public void putJsonProperty(String name, String json) {
    ScriptableObject.putProperty(global, name, NativeJSON.parse(cx,
        global, json, new Callable() {
          @Override
          public Object call(Context context, Scriptable scope, Scriptable
              holdable, Object[] objects) {
            return objects[1];
          }
        }));
  }

  public void putBundledFilePathAsProperty(String name, String path) {
    URL resourceUrl = getResourceFromBundle(path);
    putProperty(name, resourceUrl != null ? resourceUrl.toString() : "");
  }

  public String executeScript(String fileName) throws ProcessingException {
    String result = null;
    try {
      Main.processSource(cx, fileName);
      result = out.toString(Charsets.UTF_8.name());
    } catch (Exception e) {
      throw new ProcessingException(e.getMessage(), e);
    } finally {
      Context.exit();
    }

    return result;
  }

  public String executeBundledScript(String fileName) throws
      ProcessingException {
    URL resourceUrl = getResourceFromBundle(fileName);
    return executeScript(resourceUrl.toString());
  }

  private URL getResourceFromBundle(String path) {
    return bundleContext.getBundle().getResource(path);
  }
}
