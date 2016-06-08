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
package com.cognifide.aet.report.html.freemarker;

import com.cognifide.aet.report.api.ReportResource;
import com.cognifide.aet.report.api.ReportResourceRegistry;
import com.cognifide.aet.report.html.ReportResourceTemplateLoader;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportResourceTemplateLoaderTest {

	private static final String TEMPLATE_NAME = "template-name";

	private static final String TEST_FILE_PATH = "mock/HtmlReporter/freemarker/sample-template.html";

	@InjectMocks
	private ReportResourceTemplateLoader tested;

	@Mock
	private ReportResourceRegistry reportResourceRegistry;

	@Mock
	private ReportResource reportResource;

	@Mock
	private InputStream templateSource;

	private InputStream fetchedTemplateStream;

	private URL url;

	@Before
	public void setUp() throws IOException {
		url = getResource();
		when(reportResource.getUrl()).thenReturn(url);
	}

	@After
	public void tearDown() {
		IOUtils.closeQuietly(fetchedTemplateStream);
	}

	@Test
	public void findTemplateSourceTest() throws IOException {
		when(reportResourceRegistry.getResource(TEMPLATE_NAME)).thenReturn(reportResource);

		fetchedTemplateStream = (InputStream) tested.findTemplateSource(TEMPLATE_NAME);
		List<String> fetchedLines = IOUtils.readLines(fetchedTemplateStream);
		InputStream stream = getResource().openStream();
		List<String> sampleLines = IOUtils.readLines(stream);

		assertThat(fetchedLines, is(sampleLines));
		IOUtils.closeQuietly(stream);
	}

	@Test
	public void findTemplateSourceTest_resourceNotFound() throws IOException {
		when(reportResourceRegistry.getResource(TEMPLATE_NAME)).thenReturn(null);

		Object templateSource = tested.findTemplateSource(TEMPLATE_NAME);

		assertThat(templateSource, is(nullValue()));
	}

	@Test
	public void getReaderTest() throws IOException {
		Reader reader = tested.getReader(templateSource, "UTF-8");

		assertThat(reader, is(instanceOf(InputStreamReader.class)));
	}

	@Test
	public void closeTemplateSourceTest() throws IOException {
		tested.closeTemplateSource(templateSource);

		verify(templateSource, times(1)).close();
	}

	@Test
	public void closeTemplateSourceTest_noTemplateSource() throws IOException {
		tested.closeTemplateSource(null);

		verify(templateSource, never()).close();
	}

	@Test
	public void getLastModifiedTest() {
		assertThat(tested.getLastModified(TEMPLATE_NAME), is(-1l));
	}

	private URL getResource() {
		return getClass().getClassLoader().getResource(TEST_FILE_PATH);
	}

}
