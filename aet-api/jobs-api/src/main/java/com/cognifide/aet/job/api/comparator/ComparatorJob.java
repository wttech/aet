/*
 * Cognifide AET :: Jobs API
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
package com.cognifide.aet.job.api.comparator;

import java.util.Map;

import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.job.api.exceptions.ProcessingException;

/**
 * @author michal.chudy
 * @date 13.11.13
 */
public interface ComparatorJob {

	Boolean NO_COMPARISON_RESULT = null;

	/**
	 * Method executed during compare phase.
	 *
	 * @return true should be returned only in case of positive data comparison,
	 * false should be returned in case of negative data comparison (errors detected).
	 * null value should be returned in other cases.
	 * @throws ProcessingException
	 */
	Boolean compare() throws ProcessingException;

	/**
	 * Setup all parameters necessary to perform comparison. Method is invoked before compare method.
	 * @param params - map that contains all parameters defined in test suite xml file.
	 * @throws ParametersException
	 */
	void setParameters(Map<String, String> params) throws ParametersException;

}
