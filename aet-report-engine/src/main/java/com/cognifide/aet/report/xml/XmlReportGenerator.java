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
package com.cognifide.aet.report.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;

import com.cognifide.aet.report.api.ReportGenerateException;
import com.cognifide.aet.report.api.ReportGenerator;
import com.cognifide.aet.report.xml.models.Testcase;
import com.cognifide.aet.report.xml.models.Testsuite;
import com.cognifide.aet.report.xml.models.Testsuites;

public class XmlReportGenerator implements ReportGenerator {
	public static final String NAME = "xunit-report";

	private Testsuites metadata;

	private int passedCounter;

	private int failedCounter;


	public XmlReportGenerator(Testsuites metadata) {
		this.metadata = metadata;
	}

	@Override
	public InputStream generate() throws ReportGenerateException {
		prepareResults();
		Writer writer = new StringWriter();
		try {
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n");
			writer.write("<!-- Report generated only for Jenkins. -->\n");
			prepareJaxbMarshaller().marshal(metadata, writer);
			return IOUtils.toInputStream(writer.toString(), "UTF-8");
		} catch (IOException | JAXBException e) {
			throw new ReportGenerateException(e.getMessage(), e);
		}

	}

	private Testsuites prepareResults() {
		for (Testsuite testsuite : metadata.getTestsuite()) {
			populateSutesTestCaseCount(testsuite);
			for (Testcase testcase : testsuite.getTestcase()) {
				if (isFailed(testcase)) {
					failedCounter++;
				}else{
					passedCounter++;
				}
			}
			
			metadata.setTests(String.valueOf(passedCounter + failedCounter));
			metadata.setFailures(String.valueOf(failedCounter));
		}
		return null;
	}

	private boolean isFailed(Testcase testcase) {
		return !testcase.getFailure().isEmpty();
	}

	private void populateSutesTestCaseCount(Testsuite testsuite) {
		testsuite.setTests(String.valueOf(testsuite.getTestcase().size()));
	}


	private Marshaller prepareJaxbMarshaller() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Testsuites.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		return jaxbMarshaller;
	}

	@Override
	public ContentType getContentType() {
		return ContentType.APPLICATION_XML;
	}

}
