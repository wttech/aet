/**
 * Automated Exploratory Tests
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.runner.util;

import com.cognifide.aet.communication.api.exceptions.AETException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * MessagesHelper
 *
 * @Author: Maciej Laskowski
 * @Date: 13.03.15
 */
final class MessagesHelper {

  static final String BROKER_OBJECT_NAME = "org.apache.activemq:type=Broker,brokerName=localhost";

  static final String QUEUES_ATTRIBUTE = "Queues";

  static final String DESTINATION_NAME_PROPERTY = "destinationName";

  static final String AET_QUEUE_DOMAIN = "AET.";

  private MessagesHelper() {
    // helper constructor
  }

  public static MBeanServerConnection setupConnection(String jmxUrl) throws IOException {
    JMXServiceURL url = new JMXServiceURL(jmxUrl);
    JMXConnector jmxc = JMXConnectorFactory.connect(url);
    return jmxc.getMBeanServerConnection();
  }

  public static Set<ObjectName> getAetQueuesObjects(MBeanServerConnection connection) throws AETException {
    ObjectName[] queues;
    try {
      ObjectName broker = new ObjectName(BROKER_OBJECT_NAME);
      connection.getMBeanInfo(broker);
      queues = (ObjectName[]) connection.getAttribute(broker, QUEUES_ATTRIBUTE);
    } catch (Exception e) {
      throw new AETException("Exception while getting AET Queues.", e);
    }

    return filter(queues);
  }

  private static Set<ObjectName> filter(ObjectName[] queuesObjects) {
    Set<ObjectName> queues = new HashSet<>();
    for (ObjectName queueObject : queuesObjects) {
      if (queueObject.getKeyProperty(DESTINATION_NAME_PROPERTY).startsWith(AET_QUEUE_DOMAIN)) {
        queues.add(queueObject);
      }
    }
    return queues;
  }
}
