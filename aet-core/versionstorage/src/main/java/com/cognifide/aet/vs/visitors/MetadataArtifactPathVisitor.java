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
package com.cognifide.aet.vs.visitors;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.cognifide.aet.communication.api.node.MetadataVisitor;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.cognifide.aet.communication.api.node.ReportMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.vs.VersionStorageException;

/**
 * MetadataArtifactPathVisitor implements MetadataVisitor, builds proper REST path to given type of Metadata
 * Node
 * 
 * @Author: Maciej Laskowski
 * @Date: 16.02.15
 */
public class MetadataArtifactPathVisitor implements MetadataVisitor {

	private final String pathSeparator;

	private final StringBuilder pathBuilder = new StringBuilder();

	public MetadataArtifactPathVisitor(String pathSeparator) {
		this.pathSeparator = pathSeparator;
	}

	/**
	 * @return path to given metadata artifact
	 */
	public String getPath() {
		return pathBuilder.toString();
	}

	@Override
	public void visit(NodeMetadata metadata) throws AETException {
		visitBasicNode(metadata);
	}

	@Override
	public void visit(UrlNodeMetadata metadata) throws AETException {
		visitUrlNode(metadata);
	}

	@Override
	public void visit(CollectMetadata metadata) throws AETException {
		visitUrlNode(metadata);
		pathBuilder.append(metadata.getCorrelationId()).append(pathSeparator);
	}

	@Override
	public void visit(CompareMetadata metadata) throws AETException {
		visitUrlNode(metadata);
		pathBuilder.append(metadata.getComparatorModule()).append(pathSeparator)
				.append(metadata.getComparatorModuleName()).append(pathSeparator)
				.append(metadata.getCorrelationId()).append(pathSeparator);
	}

	@Override
	public void visit(PatternMetadata metadata) throws AETException {
		visitUrlNode(metadata);
	}

	@Override
	public void visit(ReportMetadata metadata) throws AETException {
		visitBasicNode(metadata);

		pathBuilder.append(metadata.getArtifactType().toLowerCase()).append(pathSeparator)
				.append(metadata.getReporterModule()).append(pathSeparator)
				.append(metadata.getCorrelationId()).append(pathSeparator);
	}

	private void visitBasicNode(NodeMetadata metadata) {
		pathBuilder.append(metadata.getCompany()).append(pathSeparator).append(metadata.getProject())
				.append(pathSeparator).append(metadata.getTestSuiteName()).append(pathSeparator)
				.append(metadata.getEnvironment()).append(pathSeparator);
	}

	private void visitUrlNode(UrlNodeMetadata metadata) throws VersionStorageException {
		visitBasicNode(metadata);
		URLCodec urlCodec = new URLCodec();
		String encodedUrl;
		try {
			encodedUrl = urlCodec.encode(metadata.getUrlName());
		} catch (EncoderException e) {
			throw new VersionStorageException("Unable to generate path from node", e);
		}
		pathBuilder.append(metadata.getTestName()).append(pathSeparator).append("urls")
				.append(pathSeparator).append(encodedUrl).append(pathSeparator).append("artifactTypes")
				.append(pathSeparator).append(metadata.getArtifactType().toLowerCase())
				.append(pathSeparator).append(metadata.getCollectorModule()).append(pathSeparator)
				.append(metadata.getCollectorModuleName()).append(pathSeparator);
	}

}
