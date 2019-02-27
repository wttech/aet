package com.cognifide.aet.cleaner.processors;

import static org.junit.Assert.assertEquals;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import java.net.InetSocketAddress;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StartMetadataCleanupProcessorTest {

  private MongoCollection<Document> collection;
  private MongoClient client;
  private MongoServer server;

  @Before
  public void setUp() throws Exception {
    server = new MongoServer(new MemoryBackend());

    // bind on a random local port
    InetSocketAddress serverAddress = server.bind();
    client = new MongoClient(new ServerAddress(serverAddress));
    collection = client.getDatabase("testdb").getCollection("testcollection");
  }

  @Test
  public void test() {
    assertEquals(0, collection.countDocuments());
    collection.insertOne(new Document());
    assertEquals(1, collection.countDocuments());
  }

  @After
  public void tearDown() {
    client.close();
    server.shutdown();
  }
}