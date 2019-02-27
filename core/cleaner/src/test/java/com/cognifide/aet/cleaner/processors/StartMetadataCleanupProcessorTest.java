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
package com.cognifide.aet.cleaner.processors;

import static org.junit.Assert.assertEquals;

import com.cognifide.aet.vs.mongodb.MongoDBClient;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import java.net.InetSocketAddress;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StartMetadataCleanupProcessorTest {

  @Rule
  public final OsgiContext context = new OsgiContext();

  private MongoCollection<Document> collection;
  private MongoClient client;
  private MongoServer server;
  private String mongoURI;

  @Before
  public void setUp() {
    server = new MongoServer(new MemoryBackend());
    InetSocketAddress serverAddress = server.bind();
    mongoURI = String.format("mongodb://localhost:%d", serverAddress.getPort());
    client = new MongoClient(new MongoClientURI(mongoURI));

    collection = client.getDatabase("testdb").getCollection("testcollection");

    context.registerInjectActivateService(new MongoDBClient(), "mongoURI", mongoURI);
    context.registerInjectActivateService(new StartMetadataCleanupProcessor());
  }

  @Test
  public void test() {
    assertEquals(0, collection.countDocuments());

    Document obj = new Document("_id", 1).append("key", "value");
    collection.insertOne(obj);

    assertEquals(1, collection.countDocuments());
    assertEquals(obj, collection.find().first());
  }

  @After
  public void tearDown() {
    client.close();
    server.shutdown();
  }
}