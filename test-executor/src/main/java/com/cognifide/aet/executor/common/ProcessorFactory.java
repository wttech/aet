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
package com.cognifide.aet.executor.common;

import com.cognifide.aet.communication.api.messages.FatalErrorMessage;
import com.cognifide.aet.communication.api.messages.FinishedSuiteProcessingMessage;
import com.cognifide.aet.communication.api.messages.ProcessingErrorMessage;
import com.cognifide.aet.communication.api.messages.ProgressMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

public final class ProcessorFactory {

  private ProcessorFactory() {
    // private
  }

  public static MessageProcessor produce(Message message, RunnerTerminator runnerTerminator)
      throws JMSException {
    MessageProcessor processor = null;
    if (message instanceof ObjectMessage) {
      Object object = ((ObjectMessage) message).getObject();
      if (object instanceof ProcessingErrorMessage) {
        processor = new ProcessingErrorMessageProcessor((ProcessingErrorMessage) object,
            runnerTerminator);
      } else if (object instanceof FinishedSuiteProcessingMessage) {
        processor = new SuiteFinishedProcessor((FinishedSuiteProcessingMessage) object,
            runnerTerminator);
      } else if (object instanceof ProgressMessage) {
        processor = new ProgressMessageProcessor((ProgressMessage) object);
      } else if (object instanceof FatalErrorMessage) {
        processor = new FatalErrorMessageProcessor((FatalErrorMessage) object, runnerTerminator);
      } else {
        processor = new UnexpectedMessageProcessor(object);
      }
    }
    return processor;
  }

}
