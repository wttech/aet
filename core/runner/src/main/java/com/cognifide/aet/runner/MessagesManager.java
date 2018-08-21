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
package com.cognifide.aet.runner;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.runner.configuration.MessagesManagerConf;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = MessagesManager.class, immediate = true)
@Designate(ocd = MessagesManagerConf.class)
public class MessagesManager {

  private static final String REMOVE_OPERATION_NAME = "removeMatchingMessages";

  private static final String JMS_CORRELATION_ID = "JMSCorrelationID";

  private static final String STRING_SIGNATURE = "java.lang.String";

  private static final Logger LOGGER = LoggerFactory.getLogger(MessagesManager.class);

  private static final String BROKER_OBJECT_NAME = "org.apache.activemq:type=Broker,brokerName=localhost";

  private static final String QUEUES_ATTRIBUTE = "Queues";

  private static final String AET_QUEUE_DOMAIN = "AET.";

  static final String DESTINATION_NAME_PROPERTY = "destinationName";

  private MessagesManagerConf config;

  @Activate
  public void activate(MessagesManagerConf config) {
    this.config = config;
  }

  /**
   * Removes all messages with given correlationID. AETException is thrown when failed to remove
   * messages.
   *
   * @param correlationID - correlationId of messages that will be removed.
   */
  public void remove(String correlationID) throws AETException {
    Object[] removeSelector = {JMS_CORRELATION_ID + "='" + correlationID + "'"};
    String[] signature = {STRING_SIGNATURE};

    try (JMXConnector jmxc = getJmxConnection(config.jmxUrl())) {
      MBeanServerConnection connection = jmxc.getMBeanServerConnection();
      for (ObjectName queue : getAetQueuesObjects(connection)) {
        String queueName = queue.getKeyProperty(DESTINATION_NAME_PROPERTY);
        int deletedMessagesNumber = (Integer) connection.invoke(queue, REMOVE_OPERATION_NAME,
            removeSelector, signature);
        LOGGER.debug("Deleted: {} jmsMessages from: {} queue", deletedMessagesNumber, queueName);
      }
    } catch (Exception e) {
      throw new AETException(String.format("Error while removing messages with correlationID: %s",
          correlationID), e);
    }
  }

  public static String createFullQueueName(String name) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("Queue name can't be null or empty string!");
    }
    return AET_QUEUE_DOMAIN + name;
  }

  protected Set<ObjectName> getAetQueuesObjects(MBeanServerConnection connection)
      throws AETException {
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

  private Set<ObjectName> filter(ObjectName[] queuesObjects) {
    Set<ObjectName> queues = new HashSet<>();
    for (ObjectName queueObject : queuesObjects) {
      if (queueObject.getKeyProperty(DESTINATION_NAME_PROPERTY).startsWith(AET_QUEUE_DOMAIN)) {
        queues.add(queueObject);
      }
    }
    return queues;
  }

  protected JMXConnector getJmxConnection(String jmxUrl) throws IOException {
    JMXServiceURL url = new JMXServiceURL(jmxUrl);
    return JMXConnectorFactory.connect(url);
  }
}
