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
import com.cognifide.aet.queues.configuration.DefaultJmsConnectionConf;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
@Designate(ocd = DefaultJmsConnectionConf.class)
public class DefaultJmsConnection implements JmsConnection {

  private static final boolean SESSION_TRANSACTION_DEFAULT_SETTING = false;

  private DefaultJmsConnectionConf config;

  @Reference
  private ConfigurationAdmin configurationAdmin;

  private static final Logger LOG = LoggerFactory.getLogger(DefaultJmsConnection.class);

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
    return new JmsEndpointConfig(config.url(), config.username(), config.password());
  }

  @Activate
  public void activate(DefaultJmsConnectionConf config) throws JMSException {
    this.config = config;
    connect();
  }

  public void connect() throws JMSException {
    LOG.info("Connecting to broker {} on user {}", config.url(), config.username());
    final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(config.url());
    connectionFactory.setUserName(config.username());
    connectionFactory.setPassword(config.password());
    connectionFactory.setTrustAllPackages(true);
    connection = connectionFactory.createConnection();
    connection.start();
  }

  @Deactivate
  public void deactivate() {
    disconnect();
  }

  public void disconnect() {
    LOG.info("Disconnecting from broker {} on user {}", config.url(), config.username());
    JmsUtils.closeQuietly(connection);
  }

}
