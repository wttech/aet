/*
 * Cognifide AET :: Job Common
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
package com.cognifide.aet.job.common.collectors.screen;

import java.util.Date;

import com.cognifide.aet.vs.CollectorResult;

public class ScreenCollectorResult extends CollectorResult {

	private static final long serialVersionUID = 531750466960867337L;

	private String title;

	private Date created;

	private String dataUrl;

	private String data;

	private String md5;

	public ScreenCollectorResult(String url) {
		super(url);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public Date getCreated() {
		return new Date(created.getTime());
	}

	public void setCreated(Date created) {
		this.created = new Date(created.getTime());
	}

	public String getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	public String getDataFileName() {
		return data;
	}

	public void setDataFileName(String dataFileName) {
		this.data = dataFileName;
	}

	public String getMD5() {
		return md5;
	}

	public void setMD5(String md5) {
		this.md5 = md5;
	}

}
