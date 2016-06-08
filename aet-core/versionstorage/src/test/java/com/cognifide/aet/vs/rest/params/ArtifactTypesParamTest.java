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
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ArtifactTypesParamTest {

	@Test
	public void testGetArtifactTypesEmpty() {
		ArtifactTypesParam tested = new ArtifactTypesParam("");

		Assert.assertThat(tested.getArtifactTypes(), is(nullValue()));
	}

	@Test
	public void testGetArtifactTypesSingle() {
		ArtifactTypesParam tested = new ArtifactTypesParam("data");

		Assert.assertThat(tested.getArtifactTypes(), hasSize(1));
		Assert.assertThat(tested.getArtifactTypes(), contains(ArtifactType.DATA));
	}

	@Test
	public void testGetArtifactTypesTwo() {
		ArtifactTypesParam tested = new ArtifactTypesParam("data,results");

		Assert.assertThat(tested.getArtifactTypes(), hasSize(2));
		Assert.assertThat(tested.getArtifactTypes(),
				containsInAnyOrder(ArtifactType.DATA, ArtifactType.RESULTS));
	}

	@Test
	public void testGetArtifactTypesMultiple() {
		ArtifactTypesParam tested = new ArtifactTypesParam("data,results,reports,patterns");

		Assert.assertThat(tested.getArtifactTypes(), hasSize(4));
		Assert.assertThat(
				tested.getArtifactTypes(),
				containsInAnyOrder(ArtifactType.DATA, ArtifactType.RESULTS, ArtifactType.PATTERNS,
						ArtifactType.REPORTS));
	}

	@Test
	public void testGetArtifactTypesRepeatingValues() {
		ArtifactTypesParam tested = new ArtifactTypesParam("results,data,results");

		Assert.assertThat(tested.getArtifactTypes(), hasSize(2));
		Assert.assertThat(tested.getArtifactTypes(),
				containsInAnyOrder(ArtifactType.DATA, ArtifactType.RESULTS));
	}

	@Test(expected = WebApplicationException.class)
	public void testGetArtifactTypesInvalidEntry() {
		new ArtifactTypesParam("abcd");
	}

}
