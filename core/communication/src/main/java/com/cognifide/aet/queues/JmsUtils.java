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

import java.io.Serializable;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General JMS manipulation utilities
 *
 * @author lukasz.wieczorek
 */
public final class JmsUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(JmsUtils.class);

  private JmsUtils() {
    //empty util constructor
  }

  /**
   * Closes a {@link MessageProducer} ignoring nulls and exceptions
   *
   * @param messageProducer - a {@link MessageProducer} to close.
   */
  public static void closeQuietly(MessageProducer messageProducer) {
    if (messageProducer == null) {
      return;
    }
    try {
      messageProducer.close();
    } catch (JMSException e) {
      LOGGER.error("Error closing MessageProducer", e);
    }
  }

  /**
   * Closes a {@link MessageConsumer} ignoring nulls and exceptions
   *
   * @param messageConsumer - a {@link MessageConsumer} to close.
   */
  public static void closeQuietly(MessageConsumer messageConsumer) {
    if (messageConsumer == null) {
      return;
    }
    try {
      messageConsumer.close();
    } catch (JMSException e) {
      LOGGER.error("Error closing MessageConsumer", e);
    }
  }

  /**
   * Closes a {@link Session} ignoring nulls and exceptions
   *
   * @param session - a {@link Session} to close.
   */
  public static void closeQuietly(Session session) {
    if (session == null) {
      return;
    }
    try {
      session.close();
    } catch (JMSException e) {
      LOGGER.error("Error closing Session", e);
    }
  }

  /**
   * Closes a {@link Connection} ignoring nulls and exceptions
   *
   * @param connection - a {@link Connection} to close.
   */
  public static void closeQuietly(Connection connection) {
    if (connection == null) {
      return;
    }
    try {
      connection.close();
    } catch (JMSException e) {
      LOGGER.error("Error closing Connection", e);
    }
  }

  public static <T> T getFromMessage(Message message, Class<T> tClass) throws JMSException {
    T result;
    if (message instanceof ObjectMessage) {
      final Serializable object = ((ObjectMessage) message).getObject();
      if (tClass.isInstance(object)) {
        result = tClass.cast(object);
      } else {
        LOGGER.error("Invalid message object type: {}", object);
        result = null;
      }
    } else {
      LOGGER.error("Invalid message type: {}", message);
      result = null;
    }
    return result;
  }

  public static String getJMSCorrelationID(Message message) {
    String result;
    try {
      result = message.getJMSCorrelationID();
    } catch (JMSException e) {
      LOGGER.error(e.getMessage(), e);
      result = null;
    }
    return result;
  }

  public static String getJMSMessageID(Message message) {
    String result;
    try {
      result = message.getJMSMessageID();
    } catch (JMSException e) {
      LOGGER.error(e.getMessage(), e);
      result = null;
    }
    return result;
  }

}
