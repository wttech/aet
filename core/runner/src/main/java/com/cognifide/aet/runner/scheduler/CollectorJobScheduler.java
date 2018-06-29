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
package com.cognifide.aet.runner.scheduler;

import com.cognifide.aet.communication.api.queues.JmsConnection;
import com.cognifide.aet.queues.JmsUtils;
import com.cognifide.aet.runner.MessagesManager;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is singleton which manages collector queues. The main task of CollectorJobScheduler is
 * to limit number of messages in collector queues in one time (to prevent messages timeout).
 *
 * @author lukasz.wieczorek
 */
class CollectorJobScheduler implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollectorJobScheduler.class);

  /**
   * Semaphore that checks if number of collection messages in queue is below fixed limit. When
   * limit is reached, no more messages can go to the collection queue until any collection task is
   * finished.
   */
  private final Semaphore availableQueue;

  /**
   * Holds Queues with url packages to collect (from single test) identified by CorrelationID
   * (CorrelationId -> Queue). Each package contains fixed (configurable) amount of urls that should
   * be send to collect queue. After all packages results are returned, entry is removed from this
   * map.
   */
  private final ConcurrentMap<String, Queue<MessageWithDestination>> messagesMap = Maps
      .newConcurrentMap();

  /**
   * BinarySemaphore which is used to passive waiting for work in task run loop. When message queue
   * is added then semaphore is released for safeRun method to acquire it.
   */
  private final BinarySemaphore availableMessages = new BinarySemaphore(false);

  /**
   * Hold map with number of received collection results for the given message ID (not the same as
   * Correlation ID!). When collection result message is received, number of messages in map is
   * decreased. When it reaches 0, this mean that all messages for given correlation id returned and
   * there is space for new message in collection queue (availableQueue semaphore can be released).
   */
  private final Map<String, ReceivedMessagesInfo> receivedMessagesCounter = Maps.newConcurrentMap();

  private final Session session;

  private final MessageProducer producer;

  private final MessagesManager messagesManager;

  private final Integer maxMessagesInCollectorQueue;

  private volatile boolean running = true;

  CollectorJobScheduler(JmsConnection jmsConnection, Integer maxMessagesInCollectorQueue,
      MessagesManager messagesManager) throws JMSException {
    this.maxMessagesInCollectorQueue = maxMessagesInCollectorQueue;
    this.availableQueue = new Semaphore(maxMessagesInCollectorQueue);
    this.session = jmsConnection.getJmsSession();
    this.producer = session.createProducer(null);
    this.messagesManager = messagesManager;
  }

  @Override
  public void run() {
    try {
      safeRun();
    } finally {
      if (running) {
        LOGGER.error("Fatal error while running thread {}! Closing CollectorJobScheduler.", Thread
            .currentThread().getId());
      }
      if (!receivedMessagesCounter.isEmpty()) {
        LOGGER.warn("Quit CollectorJobScheduler while some tasks still in progress: {}.",
            receivedMessagesCounter.keySet());
      }
      JmsUtils.closeQuietly(producer);
      JmsUtils.closeQuietly(session);
    }
  }

  private void safeRun() {
    while (running) {
      waitUntilMessagesInQueueAvailable();
      // Processes messages map entries.
      for (Iterator<Entry<String, Queue<MessageWithDestination>>> iterator = messagesMap
          .entrySet()
          .iterator(); iterator.hasNext(); ) {
        Map.Entry<String, Queue<MessageWithDestination>> entry = iterator.next();
        Queue<MessageWithDestination> messagesQueue = entry.getValue();
        MessageWithDestination messageWithDestination = messagesQueue.poll();
        LOGGER.trace("CollectorJobScheduler loop at thread ID: {}", Thread.currentThread().getId());
        if (messageWithDestination != null) {
          sendMessage(messageWithDestination);
        } else {
          iterator.remove();
        }
      }
      // Checks if new entries were added in the meantime.
      updateIfMoreMessagesLeft();
    }
  }

  void add(Queue<MessageWithDestination> messagesQueue, String correlationID) {
    if (messagesMap.putIfAbsent(correlationID, messagesQueue) == null) {
      availableMessages.release();
      LOGGER.debug(
          "New collection message with correlationID: {} (Semaphore availableMessages released). Currently waiting for {} packages ({} slots available).",
          correlationID, receivedMessagesCounter.size(), getAvailableSlots());
    } else {
      LOGGER.error("Message {} already in messages map!", correlationID);
      throw new IllegalStateException();
    }
  }

  synchronized void messageReceived(String requestJMSMessageID, String correlationID) {
    ReceivedMessagesInfo msg = receivedMessagesCounter.get(requestJMSMessageID);
    if (msg != null) {
      int amount = msg.getMessagesAmount();
      amount--;
      if (amount == 0) {
        receivedMessagesCounter.remove(requestJMSMessageID);
        availableQueue.release();
        LOGGER.debug(
            "All results for package {} received (correlationID: {}) - releasing availableQueue semaphore. Currently {} slots available.",
            requestJMSMessageID, correlationID, getAvailableSlots());
      } else {
        receivedMessagesCounter.put(requestJMSMessageID, new ReceivedMessagesInfo(amount,
            correlationID));
        LOGGER.debug(
            "Result for package {} received (correlationID: {}) - still waiting for {} results in this package.",
            requestJMSMessageID, correlationID, amount);
      }
    }
  }

  void quit() {
    LOGGER.info("Quit CollectorJobScheduler.");
    running = false;
  }

  /**
   * Cleans all structures of CollectorJobScheduler connected with given correlationID. Also clears
   * all messages (with given correlationID) existing at this moment from all queues.
   *
   * @param correlationID - correlationID of task that cleanup should be done for.
   */
  synchronized void cleanup(String correlationID) {
    LOGGER.info("Cleaning up after test suite execution with correlationID: {}", correlationID);
    removeFromQueueMap(correlationID);
    fixMessageCounter(correlationID);
    try {
      messagesManager.remove(correlationID);
    } catch (Exception e) {
      LOGGER.error("Unable to remove JmsMessages with correlationID: {}", correlationID, e);
    }

  }

  private void fixMessageCounter(String correlationID) {
    Set<Map.Entry<String, ReceivedMessagesInfo>> entrySet = receivedMessagesCounter.entrySet();
    LOGGER.debug("Fixing message counter with correlationID: {} start with size: {}", correlationID,
        receivedMessagesCounter.size());
    for (Map.Entry<String, ReceivedMessagesInfo> entry : entrySet) {
      if (entry.getValue().getCorrelationID().equals(correlationID)) {
        receivedMessagesCounter.remove(entry.getKey());
        availableQueue.release();
        LOGGER.debug("Semaphore's availableQueue released for correlationID: {}", correlationID);
      }
    }
    LOGGER.debug("Fixing message counter to: {}", receivedMessagesCounter.size());
  }

  private void removeFromQueueMap(String correlationID) {
    LOGGER.debug("Start removing queues with correlationID: {}. Total number of queues: {}",
        correlationID, messagesMap.size());
    messagesMap.remove(correlationID);
    LOGGER.debug("End removing queues with correlationID: {} Total number of queues: {}",
        correlationID,
        messagesMap.size());
  }

  private void sendMessage(MessageWithDestination messageWithDestination) {
    try {
      availableQueue.acquire();
      Message message = messageWithDestination.getMessage();
      if (messagesMap.containsKey(message.getJMSCorrelationID())) {
        producer.send(messageWithDestination.getDestination(), message);
        receivedMessagesCounter.put(message.getJMSMessageID(),
            messageWithDestination.getMessagesToReceived());
        LOGGER.debug(
            "Sending new package {} (correlationId: {}). Currently waiting for {} collection results packages ({} slots available).",
            message.getJMSMessageID(), message.getJMSCorrelationID(),
            receivedMessagesCounter.size(), getAvailableSlots());
      } else {
        LOGGER.warn(
            "Message for correlationId {} was already canceled - releasing availableQueue semaphore. Currently waiting for {} collection results packages ({} slots available).",
            message.getJMSCorrelationID(), receivedMessagesCounter.size(), getAvailableSlots());
        availableQueue.release();
      }
    } catch (JMSException e) {
      LOGGER.error(e.getMessage(), e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private String getAvailableSlots() {
    return maxMessagesInCollectorQueue - receivedMessagesCounter.size() + " of "
        + maxMessagesInCollectorQueue;
  }

  private void updateIfMoreMessagesLeft() {
    if (!messagesMap.isEmpty()) {
      availableMessages.release();
    }
  }

  private void waitUntilMessagesInQueueAvailable() {
    try {
      availableMessages.acquire();
      LOGGER.debug("New message is available! (availableQueue acquired)");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private static final class BinarySemaphore {

    private final Semaphore semaphore;

    private BinarySemaphore(boolean available) {
      this.semaphore = new Semaphore(available ? 1 : 0);
    }

    private void acquire() throws InterruptedException {
      semaphore.acquire();
    }

    private synchronized void release() {
      if (semaphore.availablePermits() == 0) {
        semaphore.release();
      }
    }
  }
}
