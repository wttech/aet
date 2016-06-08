/*
 * Cognifide AET :: Maven Plugin
 *
 * Copyright (C) 2013 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.aet.maven.plugins;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.messages.ReportMessage;
import com.jcabi.log.Logger;

import java.io.IOException;

public class ReportMessageProcessor implements MessageProcessor {

	private final ReportMessage data;

	private final String buildDirectory;

	private final RunnerTerminator runnerTerminator;

	private final ReportWriter reportWriter;

	public ReportMessageProcessor(ReportMessage data, String buildDirectory,
			RunnerTerminator runnerTerminator, ReportWriter reportWriter) {
		this.data = data;
		this.buildDirectory = buildDirectory;
		this.runnerTerminator = runnerTerminator;
		this.reportWriter = reportWriter;
	}

	@Override
	public void process() throws AETException {
		Logger.info(this, String.format("Received report message: %s", data));
		if (data.getStatus() == ReportMessage.Status.OK) {
			processSuccess();
		} else if (data.getStatus() == ReportMessage.Status.FAILED) {
			processError();
		}
		runnerTerminator.setChanged();
	}

	private void processError() throws AETException {
		for (String error : data.getErrors()) {
			Logger.error(this, error);
		}
		throw new AETException("Failed");
	}

	private void processSuccess() throws AETException {
		try {
			reportWriter.write(buildDirectory, data.getFullReportUrl(), data.getSaveAsFileName());
		} catch (IOException e) {
			throw new AETException("Failed to save report", e);
		}
	}
}
