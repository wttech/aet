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
package com.cognifide.aet.vs.mongodb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import com.cognifide.aet.vs.SimpleDBKey;
import com.cognifide.aet.vs.metadata.MetadataDAOMongoDBImpl;
import com.cognifide.aet.vs.mongodb.configuration.MongoDBClientConf;
import com.mongodb.client.MongoDatabase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MongoDBClientTest {

  @Rule
  public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  @Mock
  private MongoDBClientConf config;
  @Mock
  private MongoDatabase mockDB;
  @Mock
  private MongoDBClient mockDBClient;
  @InjectMocks
  private MetadataDAOMongoDBImpl mockMetadataImpl;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getDbName_expectUnderlineSeparatedName() throws Exception {
    final String dbName = MongoDBClient.getDbName("company", "project");
    assertThat(dbName, is("company_project"));
  }

  @Test
  public void setupConfiguration_whenNoMongoUriSettingProvided_expectDefaultValue() {
    final MongoDBClient client = new MongoDBClient();
    when(config.mongoURI()).thenReturn("");
    client.setupConfiguration(config);
    assertThat(client.getMongoUri(), is("mongodb://localhost"));
  }

  @Test
  public void setupConfiguration_whenOnlyOsgiConfigProvided_expectConfiguredValue() {
    final MongoDBClient client = new MongoDBClient();
    when(config.mongoURI()).thenReturn("mongodb://custom.domain.com");
    client.setupConfiguration(config);
    assertThat(client.getMongoUri(), is("mongodb://custom.domain.com"));
  }

  @Test
  public void setupConfiguration_whenOnlyEnvConfigProvided_expectConfiguredValue() {
    environmentVariables.set("MONGODB_URI", "mongodb://env.domain.com");
    final MongoDBClient client = new MongoDBClient();
    when(config.mongoURI()).thenReturn("");
    client.setupConfiguration(config);
    assertThat(client.getMongoUri(), is("mongodb://env.domain.com"));
  }

  @Test
  public void setupConfiguration_whenOsgiAndEnvConfigProvided_expectOsgiConfigValue() {
    environmentVariables.set("MONGODB_URI", "mongodb://env.domain.com");
    final MongoDBClient client = new MongoDBClient();
    when(config.mongoURI()).thenReturn("mongodb://custom.domain.com");
    client.setupConfiguration(config);
    assertThat(client.getMongoUri(), is("mongodb://custom.domain.com"));
  }

  @Test
  public void isDatabase_whenAutoCreateIsTrue_expectDatabase() {
    final String dbName = MongoDBClient.getDbName("company", "project");
    when(mockDBClient.getDatabase(dbName,true)).thenReturn(mockDB);
    SimpleDBKey dbKey = new SimpleDBKey("company", "project");
    assertThat(mockMetadataImpl.isDatabase(dbKey), is(true));
  }

  @Test
  public void isDatabase_whenAutoCreateIsFalse_noDatabase() {
    final String dbName = MongoDBClient.getDbName("company", "project");
    when(mockDBClient.getDatabase(dbName,false)).thenReturn(null);
    SimpleDBKey dbKey = new SimpleDBKey("company", "project");
    assertThat(mockMetadataImpl.isDatabase(dbKey), is(false));
  }
}
