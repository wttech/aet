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
package com.cognifide.aet.vs.rest.params;

import com.cognifide.aet.communication.api.node.ArtifactType;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Set;

public class ArtifactTypesParam {

	private static final char SEPARATOR = ',';

	private Set<ArtifactType> artifactTypes;

	public ArtifactTypesParam(String stringTypes) {
		if (StringUtils.isEmpty(stringTypes)) {
			return;
		}
		try {
			artifactTypes = ArtifactType.valueOf(StringUtils.split(stringTypes, SEPARATOR));
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response
					.status(Response.Status.BAD_REQUEST)
					.entity("Bad artifactType has been provided. Make sure to use correct separator '"
							+ SEPARATOR + "' and provided correct values. Provided value: " + stringTypes
							+ " Exception: " + e.getMessage()).build());
		}
	}

	public Set<ArtifactType> getArtifactTypes() {
		return artifactTypes;
	}
}
