package com.cognifide.aet.cleaner.processors;

import static org.junit.Assert.assertEquals;

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

public class StartMetadataCleanupProcessorTest {

  @Rule
  public final OsgiContext context = new OsgiContext();

  private MongoCollection<Document> collection;
  private MongoClient client;
  private MongoServer server;
  private String mongoURI;

  @Before
  public void setUp() throws Exception {
    server = new MongoServer(new MemoryBackend());
    InetSocketAddress serverAddress = server.bind();
    mongoURI = String.format("mongodb://localhost:%d", serverAddress.getPort());
    client = new MongoClient(new MongoClientURI(mongoURI));

    collection = client.getDatabase("testdb").getCollection("testcollection");
  }

  @Test
  public void test() {
    assertEquals(0, collection.count());

    // creates the database and collection in memory and insert the object
    Document obj = new Document("_id", 1).append("key", "value");
    collection.insertOne(obj);

    assertEquals(1, collection.count());
    assertEquals(obj, collection.find().first());
  }

  @After
  public void tearDown() {
    client.close();
    server.shutdown();
  }
}