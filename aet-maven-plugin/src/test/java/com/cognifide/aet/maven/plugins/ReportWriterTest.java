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

import com.cognifide.aet.maven.plugins.ReportWriter;
import com.google.common.io.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ReportWriterTest {

	public static final String REPORT_NAME = "report.html";

	private static final String REPORT_URL_PATH = ReportWriter.class.getPackage().getName().replace(".", "/")
			+ "/" + REPORT_NAME;

	private ReportWriter tested;

	private File targetDir;

	@Before
	public void setUp() throws IOException {
		cleanUp();
		targetDir = Files.createTempDir();
		tested = new ReportWriter();
	}

	@After
	public void tearDown() throws IOException {
		cleanUp();
	}

	@Test
	public void writeTest() throws IOException {
		String reportPath = getResourceAbsolutePath(getResource(REPORT_URL_PATH));
		tested.write(targetDir.getAbsolutePath(), reportPath, REPORT_NAME);
		assertThat(getTargetFile().exists(), is(true));
	}

	private String getResourceAbsolutePath(URL resource) {
		return resource.toString();
	}

	private URL getResource(String resourcePath) throws IOException {
		return getClass().getClassLoader().getResource(resourcePath);
	}

	private File getTargetFile() {
		return new File(targetDir, REPORT_NAME);
	}

	private void cleanUp() throws IOException {
		if (targetDir != null) {
			File targetFile = getTargetFile();
			if (targetFile.exists() && !(targetFile.delete())) {
				throw new IOException("Could not delete temp file: " + targetFile.getAbsolutePath());
			}
			if (targetDir.exists() && !(targetDir.delete())) {
				throw new IOException("Could not delete temp directory: " + targetDir.getAbsolutePath());
			}
		}
	}

}
