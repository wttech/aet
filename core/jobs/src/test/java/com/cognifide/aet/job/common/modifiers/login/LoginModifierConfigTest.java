/*
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
package com.cognifide.aet.job.common.modifiers.login;

import static com.cognifide.aet.job.common.modifiers.login.LoginModifierConfig.DEFAULT_LOGIN;
import static com.cognifide.aet.job.common.modifiers.login.LoginModifierConfig.DEFAULT_PASSWORD;
import static com.cognifide.aet.job.common.modifiers.login.LoginModifierConfig.LOGIN_PAGE_PARAM;
import static com.cognifide.aet.job.common.modifiers.login.LoginModifierConfig.LOGIN_PARAM;
import static com.cognifide.aet.job.common.modifiers.login.LoginModifierConfig.PASSWORD_PARAM;
import static org.junit.Assert.assertEquals;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;

@RunWith(ZohhakRunner.class)
public class LoginModifierConfigTest {

  @Rule
  public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  @Test
  public void whenNoPasswordProvided_expectDefaultPasswordInTheConfig() throws ParametersException {
    Map<String, String> params = new HashMap<>();
    params.put(LOGIN_PAGE_PARAM, "whatever");

    LoginModifierConfig tested = new LoginModifierConfig(params);

    assertEquals(DEFAULT_PASSWORD, tested.getPassword());
  }

  @TestWith({
      "s3cret",
      "$ecret",
      "${ecret",
      "$ecret}",
      "s3cret}"
  })
  public void whenPasswordPassedAsPlainText_expectPasswordInTheConfig(String password) throws ParametersException {
    Map<String, String> params = new HashMap<>();
    params.put(LOGIN_PAGE_PARAM, "whatever");
    params.put(PASSWORD_PARAM, password);

    LoginModifierConfig tested = new LoginModifierConfig(params);

    assertEquals(password, tested.getPassword());
  }

  @Test
  public void whenPasswordPassedAsEnv_expectPasswordInTheConfig() throws ParametersException {
    Map<String, String> params = new HashMap<>();
    params.put(LOGIN_PAGE_PARAM, "whatever");
    params.put(PASSWORD_PARAM, "${MY_SECRET_PASSWORD}");
    environmentVariables.set("MY_SECRET_PASSWORD", "pass-from-env");

    LoginModifierConfig tested = new LoginModifierConfig(params);

    assertEquals("pass-from-env", tested.getPassword());
  }

  @Test
  public void whenPasswordPassedAsEnvAndEnvNotSet_expectDefaultPasswordInTheConfig() throws ParametersException {
    Map<String, String> params = new HashMap<>();
    params.put(LOGIN_PAGE_PARAM, "whatever");
    params.put(PASSWORD_PARAM, "${MY_SECRET_PASSWORD}");

    LoginModifierConfig tested = new LoginModifierConfig(params);

    assertEquals(DEFAULT_PASSWORD, tested.getPassword());
  }

  //-----

  @Test
  public void whenNoLoginProvided_expectDefaultLoginInTheConfig() throws ParametersException {
    Map<String, String> params = new HashMap<>();
    params.put(LOGIN_PAGE_PARAM, "whatever");

    LoginModifierConfig tested = new LoginModifierConfig(params);

    assertEquals(DEFAULT_LOGIN, tested.getLogin());
  }

  @TestWith({
      "s3cret",
      "$ecret",
      "${ecret",
      "$ecret}",
      "s3cret}"
  })
  public void whenLoginPassedAsPlainText_expectLoginInTheConfig(String login) throws ParametersException {
    Map<String, String> params = new HashMap<>();
    params.put(LOGIN_PAGE_PARAM, "whatever");
    params.put(LOGIN_PARAM, login);

    LoginModifierConfig tested = new LoginModifierConfig(params);

    assertEquals(login, tested.getLogin());
  }

  @Test
  public void whenLoginPassedAsEnv_expectLoginInTheConfig() throws ParametersException {
    Map<String, String> params = new HashMap<>();
    params.put(LOGIN_PAGE_PARAM, "whatever");
    params.put(LOGIN_PARAM, "${MY_SECRET_LOGIN}");
    environmentVariables.set("MY_SECRET_LOGIN", "user-from-env");

    LoginModifierConfig tested = new LoginModifierConfig(params);

    assertEquals("user-from-env", tested.getLogin());
  }

  @Test
  public void whenLoginPassedAsEnvAndEnvNotSet_expectDefaultLoginInTheConfig() throws ParametersException {
    Map<String, String> params = new HashMap<>();
    params.put(LOGIN_PAGE_PARAM, "whatever");
    params.put(DEFAULT_LOGIN, "${MY_SECRET_PASSWORD}");

    LoginModifierConfig tested = new LoginModifierConfig(params);

    assertEquals(DEFAULT_LOGIN, tested.getLogin());
  }

}