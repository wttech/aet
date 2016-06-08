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
package com.cognifide.aet.report.rest.vs.client;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;

public class GetClient {
	private String uri;

	private HttpGet request;

	private GetClient(String uri) {
		this.uri = uri;
		request = new HttpGet(uri);
	}

	public static GetClient create(String endpointLocation, String uri) {
		return new GetClient(String.format("%s%s", endpointLocation, uri));
	}

	public GetClient accept(ContentType contentType) {
		request.addHeader("Content-Type", contentType.toString());
		return this;
	}

	public String get() throws RestCallException {
		HttpClient client = HttpClientBuilder.create().build();
		try {
			HttpResponse response = getHttpResponse(client);

			String responseString = null;
			if (response != null && response.getStatusLine().getStatusCode() > HttpStatus.SC_OK) {
				throw new RestCallException(uri, response.getStatusLine().getStatusCode(), "Failture while peforming GET request ->");
			} else if (response != null) {
				responseString = IOUtils.toString(response.getEntity().getContent());
			}

			return responseString;
		} catch (IllegalStateException | IOException e) {
			throw new IllegalArgumentException("Can't read response: "+e.getMessage(), e);
		} finally {
			request.reset();
		}
	}

	private HttpResponse getHttpResponse(HttpClient client) throws RestCallException {
		try {
			return client.execute(request);
		} catch (IOException e) {
			throw new RestCallException(e.getMessage(), e);
		}
	}

}
