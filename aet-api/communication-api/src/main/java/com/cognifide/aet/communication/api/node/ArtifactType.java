/*
 * Cognifide AET :: Communication API
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
package com.cognifide.aet.communication.api.node;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Types of AET tests steps results.
 */
public enum ArtifactType {
	// @formatter:off
	PATTERNS("patterns"),
	REPORTS("reports"),
	DATA("data"),
	RESULTS("results"),
	EMPTY("");
	// @formatter:on

	private String directory;

	private ArtifactType(String directory) {
		this.directory = directory;
	}

	public String getDirectory() {
		return directory;
	}

	public static Set<ArtifactType> valueOf(String[] types){
		Set<ArtifactType> artifactTypes = Sets.newHashSet();

		for (String type : types) {
			artifactTypes.add(ArtifactType.valueOf(type.trim().toUpperCase()));
		}

		return artifactTypes;
	}

}