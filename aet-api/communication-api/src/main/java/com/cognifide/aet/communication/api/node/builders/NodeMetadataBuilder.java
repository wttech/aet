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
package com.cognifide.aet.communication.api.node.builders;

import com.cognifide.aet.communication.api.node.NodeMetadata;

/**
 * NodeMetadataBuilder - builder for basic NodeMetadata class.
 * 
 * @Author: Maciej Laskowski
 * @Date: 13.02.15
 */
public class NodeMetadataBuilder extends AMetadataBuilder<NodeMetadata, NodeMetadataBuilder> {

	private NodeMetadataBuilder() {
	}

	public static NodeMetadataBuilder getNodeMetadata() {
		return new NodeMetadataBuilder();
	}

	@Override
	public NodeMetadata build() {
		NodeMetadata nodeMetadata = new NodeMetadata(company, project, testSuiteName, environment,
				artifactType);
		nodeMetadata.setDomain(domain);
		nodeMetadata.setVersion(version);
		return nodeMetadata;
	}

	@Override
	protected NodeMetadataBuilder self() {
		return this;
	}

}
