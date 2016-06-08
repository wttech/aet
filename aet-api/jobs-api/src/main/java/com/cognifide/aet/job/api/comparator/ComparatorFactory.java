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

import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.Node;

import java.util.List;
import java.util.Map;

/**
 * This factory is used to instantiate Comparator instance with given the name and comparing data of the given type.
 *
 * Implementation of this interface should be a OSGI service so each implementation requires to have @Service
 * and @Component annotation to work and register properly.
 */
public interface ComparatorFactory {

	int DEFAULT_COMPARATOR_RANKING = 100;

	/**
	 * @return the name of the resource type consumed by defined comparator.
	 * This is always name of tag definition for comparator.
	 */
	String getType();

	/**
	 * @return return the name, which the collector factory will be registered on.
	 * It has to be unique for all modules in the compare phase.
	 * It is also the name of a tag definition for comparator used in a suite.
	 */
	String getName();

	/**
	 * @return  the ranking of a comparator. This number determines if created collector
	 * should be the default collector for comparison of the resource type returned by getType method.
	 * Default comparators defined in the system has ranking value set to 100. In order to make developed comparator a default for the resource type, this method should return higher value than 100. Otherwise it should return lower value.
	 */
	int getRanking();

	/**
	 * Creates comparator job. Each call should return new instance of a comparator object unless it is not stateful - it has no parameters and variable fields.
	 * @param dataNode - a Node that contains data to compare.
	 * @param patternNode - a Node that contains pattern (if pattern exists for comparator).
	 * @param resultNode - a Node to which result of comparison should be saved.
	 * @param params - a map that contains all parameters defined in test suite xml file for comparator.
	 * @param comparatorProperties - contains comparator metadata.
	 * @param dataModifierJobs - list of DataModifierJob which should be invoked before comparison.
	 * @return comparator job instance.
	 * @throws ParametersException
	 */
	ComparatorJob createInstance(Node dataNode, Node patternNode, Node resultNode,
			Map<String, String> params, ComparatorProperties comparatorProperties,
			List<DataModifierJob> dataModifierJobs) throws ParametersException;

}
