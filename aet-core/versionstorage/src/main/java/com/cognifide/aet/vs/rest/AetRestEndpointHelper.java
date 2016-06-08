/*
 * Cognifide AET :: Version Storage
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
package com.cognifide.aet.vs.rest;

import com.cognifide.aet.vs.rest.exceptions.AetRestEndpointException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Collection;

public final class AetRestEndpointHelper {

	public static final String NOT_FOUND_MSG = "Resource not found";

	public static final String UNABLE_TO_RETRIEVE_DATA_MSG = "Unable to retrieve data";

	public static final String CONTENT_TYPE = "Content-Type";

	public static final String MIME_TYPE_IMAGE_PNG = "image/png";

	public static final String PNG_EXTENSION = ".png";

	public static final String JSON_EXTENSION = ".json";

	public static final String HTML_EXTENSION = ".html";

	public static final String XML_EXTENSION = ".xml";

	private AetRestEndpointHelper() {
	}

	public static Collection validate(Collection collection) throws AetRestEndpointException {
		if (collection == null || collection.isEmpty()) {
			throw new AetRestEndpointException(NOT_FOUND_MSG);
		}
		return collection;
	}

	public static Response inputStreamToResponse(InputStream dataStream, String artifactName)
			throws AetRestEndpointException {
		if (dataStream == null) {
			throw new AetRestEndpointException(NOT_FOUND_MSG);
		}
		Response.ResponseBuilder rb = Response.ok(dataStream);

		if (artifactName.endsWith(PNG_EXTENSION)) {
			rb.header(CONTENT_TYPE, MIME_TYPE_IMAGE_PNG);
		} else if (artifactName.endsWith(JSON_EXTENSION)) {
			rb.header(CONTENT_TYPE, MediaType.TEXT_PLAIN);
		}
		return rb.build();
	}

	public static Response reportInputStreamToResponse(InputStream dataStream, String artifactName)
			throws AetRestEndpointException {
		if (dataStream == null) {
			throw new AetRestEndpointException(NOT_FOUND_MSG);
		}
		Response.ResponseBuilder rb = Response.ok(dataStream);
		if (artifactName.endsWith(JSON_EXTENSION)) {
			rb.header(CONTENT_TYPE, MediaType.TEXT_PLAIN);
		} else if (artifactName.endsWith(XML_EXTENSION)) {
			rb.header(CONTENT_TYPE, MediaType.TEXT_XML);
		} else if (artifactName.endsWith(HTML_EXTENSION)) {
			rb.header(CONTENT_TYPE, MediaType.TEXT_HTML);
		}
		return rb.build();
	}
}
