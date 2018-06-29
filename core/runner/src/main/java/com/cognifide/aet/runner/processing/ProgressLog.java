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
package com.cognifide.aet.runner.processing;

public class ProgressLog {

  private final String name;

  private final int receivedMessagesSuccess;

  private final int receivedMessagesFailed;

  private final int toReceiveMessages;

  public ProgressLog(String name, int receivedMessagesSuccess, int receivedMessagesFailed,
      int toReceiveMessages) {
    this.name = name;
    this.receivedMessagesSuccess = receivedMessagesSuccess;
    this.receivedMessagesFailed = receivedMessagesFailed;
    this.toReceiveMessages = toReceiveMessages;
  }

  @Override
  public String toString() {
    String result;
    if (receivedMessagesFailed > 0) {
      result = String
          .format("%s: [success:% 4d, failed:% 4d, total:% 4d]", name, receivedMessagesSuccess,
              receivedMessagesFailed, toReceiveMessages);
    } else {
      result = String.format("%s: [success:% 4d, total:% 4d]", name, receivedMessagesSuccess,
          toReceiveMessages);
    }
    return result;
  }
}
