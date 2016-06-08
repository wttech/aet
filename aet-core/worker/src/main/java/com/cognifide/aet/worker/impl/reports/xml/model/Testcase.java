/*
 * Cognifide AET :: Worker
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
package com.cognifide.aet.worker.impl.reports.xml.model;

import java.util.List;

import com.google.common.collect.Lists;

public class Testcase {

	private List<Failure> failure = Lists.newArrayList();

	private String name;

	public Testcase(String name, boolean failed) {
		if (failed){
			failure.add(new Failure());
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Failure> getFailure() {
		return failure;
	}
	
}
