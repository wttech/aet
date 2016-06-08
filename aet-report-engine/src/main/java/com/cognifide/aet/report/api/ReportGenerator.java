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
package com.cognifide.aet.report.api;

import java.io.InputStream;

import org.apache.http.entity.ContentType;


/**
 * Generates report for report data
 */
public interface ReportGenerator {
	/**
	 * Generate report based on @see ReportResultMetadata model
	 * @return  - @see InputStream ready to read with generated cotnent
	 * @throws ReportGenerateException
	 */
	InputStream generate() throws ReportGenerateException;

	
	/**
	 * @return Content type of the report
	 */
	ContentType getContentType();
	
	
}
