package com.cognifide.aet.cleaner.processors;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StartMetadataCleanupProcessorTest {

  private MongodExecutable mongodExecutable;

  @Before
  public void setUp() throws Exception {
    MongodStarter starter = MongodStarter.getDefaultInstance();
    String bindIp = "localhost";
    int port = 12345;
    IMongodConfig mongodConfig = new MongodConfigBuilder()
        .version(Version.Main.PRODUCTION)
        .net(new Net(bindIp, port, Network.localhostIsIPv6()))
        .build();

    mongodExecutable = starter.prepare(mongodConfig);
    mongodExecutable.start();

    MongoClient mongo = new MongoClient(bindIp, port);
    DB db = mongo.getDB("test");
    DBCollection col = db.createCollection("testCol", new BasicDBObject());
    col.save(new BasicDBObject("testDoc", new Date()));

  }

  @Test
  public void test() {

  }

  @After
  public void tearDown() {
    if (mongodExecutable != null) {
      mongodExecutable.stop();
    }
  }
}