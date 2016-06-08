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

import static com.google.common.testing.GuavaAsserts.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.cognifide.aet.vs.rest.exceptions.AetRestEndpointException;

/**
 * AetRestEndpointHelperTest
 * 
 * @Author: Maciej Laskowski
 * @Date: 12.03.15
 */
@RunWith(MockitoJUnitRunner.class)
public class AetRestEndpointHelperTest {

	@Mock
	private InputStream dataStream;

	@Test(expected = AetRestEndpointException.class)
	public void validateCollection_whenCollectionIsNull_expectAetRestEndpointException() throws Exception {
		AetRestEndpointHelper.validate(null);
	}

	@Test(expected = AetRestEndpointException.class)
	public void validateCollection_whenCollectionIsEmpty_expectAetRestEndpointException() throws Exception {
		AetRestEndpointHelper.validate(Collections.emptyList());
	}

	@Test
	public void validateCollection_whenCollectionFilled_expectValidationPassed() throws Exception {
		Collection collection = AetRestEndpointHelper.validate(Collections.singletonList("One String"));
		assertThat(collection.size(), is(1));
		assertTrue(collection.contains("One String"));
	}

	@Test(expected = AetRestEndpointException.class)
	public void inputStreamToResponse_whenDataStreamIsNull_expectAetRestEndpointException() throws Exception {
		AetRestEndpointHelper.inputStreamToResponse(null, null);
	}

	@Test
	public void inputStreamToResponse_whenDataStreamIsPng_expectContentTypeImagePng() throws Exception {
		Response response = AetRestEndpointHelper.inputStreamToResponse(dataStream, "file.png");
		assertThat(response.getHeaderString(AetRestEndpointHelper.CONTENT_TYPE),
				is(AetRestEndpointHelper.MIME_TYPE_IMAGE_PNG));
	}

	@Test
	public void inputStreamToResponse_whenDataStreamIsJson_expectContentTypeTextPlain() throws Exception {
		Response response = AetRestEndpointHelper.inputStreamToResponse(dataStream, "file.json");
		assertThat(response.getHeaderString(AetRestEndpointHelper.CONTENT_TYPE),
				is(MediaType.TEXT_PLAIN));
	}

	@Test(expected = AetRestEndpointException.class)
	public void reportInputStreamToResponse_whenDataStreamIsNull_expectAetRestEndpointException() throws Exception {
		AetRestEndpointHelper.reportInputStreamToResponse(null, null);
	}

	@Test
	public void reportInputStreamToResponse_whenDataStreamIsJson_expectContentTypeTextPlain() throws Exception {
		Response response = AetRestEndpointHelper.reportInputStreamToResponse(dataStream, "file.json");
		assertThat(response.getHeaderString(AetRestEndpointHelper.CONTENT_TYPE),
				is(MediaType.TEXT_PLAIN));
	}

	@Test
	public void reportInputStreamToResponse_whenDataStreamIsXml_expectContentTypeImagePng() throws Exception {
		Response response = AetRestEndpointHelper.reportInputStreamToResponse(dataStream, "file.xml");
		assertThat(response.getHeaderString(AetRestEndpointHelper.CONTENT_TYPE),
				is(MediaType.TEXT_XML));
	}

	@Test
	public void reportInputStreamToResponse_whenDataStreamIsHtml_expectContentTypeTextPlain() throws Exception {
		Response response = AetRestEndpointHelper.reportInputStreamToResponse(dataStream, "file.html");
		assertThat(response.getHeaderString(AetRestEndpointHelper.CONTENT_TYPE),
				is(MediaType.TEXT_HTML));
	}
}
