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
package com.cognifide.aet.communication.api.queues;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Provides Jms Session and Connection
 */
public interface JmsConnection {

  /**
   * @return current JMS Session for producing and consuming messages.
   * @throws JMSException when there is problem with obtaining session.
   */
  Session getJmsSession() throws JMSException;

  /**
   * @return active connection to its JMS provider.
   */
  Connection getJmsConnection();

  /**
   * @return broker connection configuration.
   */
  JmsEndpointConfig getEndpointConfig();

}
