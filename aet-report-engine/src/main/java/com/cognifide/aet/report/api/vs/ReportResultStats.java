/*
 * Cognifide AET :: Report Engine
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
package com.cognifide.aet.report.api.vs;

import java.io.Serializable;
import java.util.Date;

public class ReportResultStats implements Serializable {

	private static final long serialVersionUID = -7946609128253486664L;

	private int passedCount;

	private int failedCount;

	private int warningCount;

	private int totalCount;

	private Date dateTime;

	public int getPassedCount() {
		return passedCount;
	}

	public void setPassedCount(int passedCount) {
		this.passedCount = passedCount;
	}

	public int getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}

	public int getWarningCount() {
		return warningCount;
	}

	public void setWarningCount(int warningCount) {
		this.warningCount = warningCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public Date getDateTime() {
		return dateTime != null ? new Date(dateTime.getTime()) : null;
	}

	public void setDateTime(Date dateTime) {
		if(dateTime != null) {
			this.dateTime = new Date(dateTime.getTime());
		} else {
			this.dateTime = null;
		}
	}

}
