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
package com.cognifide.aet.queues;

import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.communication.api.queues.JmsEndpointConfig;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Component(immediate = true, metatype = true, description = "AET JMS Connection", label = "AET Default JMS Connection")
public class DefaultJmsConnection implements JmsConnection {

  private static final boolean SESSION_TRANSACTION_DEFAULT_SETTING = false;
  private static final Logger LOG = LoggerFactory.getLogger(DefaultJmsConnection.class);
  @Property(name = "url", label = "brokerUrl", description = "URL of the broker, no trailing '/', see http://activemq.apache.org/uri-protocols.html", value = "failover:tcp://localhost:61616")
  private String brokerURL;
  @Property(name = "username", label = "username", description = "ActiveMQ username", value = "karaf")
  private String username;
  @Property(name = "password", label = "password", description = "ActiveMQ password", value = "karaf")
  private String password;
  @Reference
  private ConfigurationAdmin configurationAdmin;
  private Connection connection;

  @Override
  public Session getJmsSession() throws JMSException {
    return connection.createSession(SESSION_TRANSACTION_DEFAULT_SETTING, Session.AUTO_ACKNOWLEDGE);
  }

  @Override
  public Connection getJmsConnection() {
    return connection;
  }

  @Override
  public JmsEndpointConfig getEndpointConfig() {
    return new JmsEndpointConfig(brokerURL, username, password);
  }

  @Activate
  public void activate(Map properties) throws JMSException {
    updateProperties(properties);
    connect();
  }

  public void connect() throws JMSException {
    LOG.info("Connecting to broker {} on user {}", brokerURL, username);
    final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
    connectionFactory.setUserName(username);
    connectionFactory.setPassword(password);
    connectionFactory.setTrustAllPackages(true);
    connection = connectionFactory.createConnection();
    connection.start();
  }

  @Deactivate
  public void deactivate() {
    disconnect();
  }

  public void disconnect() {
    LOG.info("Disconnecting from broker {} on user {}", brokerURL, username);
    JmsUtils.closeQuietly(connection);
  }

  private void updateProperties(Map<String, ?> stringDictionary) {
    username = (String) stringDictionary.get("username");
    password = (String) stringDictionary.get("password");
    brokerURL = (String) stringDictionary.get("url");
  }

}
