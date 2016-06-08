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

import java.util.Map;

import com.cognifide.aet.communication.api.NoNullValueMap;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.cognifide.aet.communication.api.node.MetadataVisitor;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.cognifide.aet.communication.api.node.ReportMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;

/**
 * MetadataArtifactPathVisitor implements MetadataVisitor, builds map of metadata bean
 * 
 * @Author: Maciej Laskowski
 * @Date: 16.02.15
 */
public class MetadataArtifactQueryParametersVisitor implements MetadataVisitor {

	Map<String, Object> queryParamsMap = new NoNullValueMap<>();

	/**
	 * @return path Map with key -> value, where key is property name and value is property value. Only
	 * properties that are not null will be returned in this map.
	 */
	public Map<String, Object> getQueryParameters() {
		return queryParamsMap;
	}

	@Override
	public void visit(NodeMetadata metadata) throws AETException {
		visitNodeMetadata(metadata);
	}

	@Override
	public void visit(UrlNodeMetadata metadata) throws AETException {
		visitUrlNodeMetadata(metadata);
	}

	@Override
	public void visit(CollectMetadata metadata) throws AETException {
		visitCollectMetadata(metadata);
	}

	@Override
	public void visit(CompareMetadata metadata) throws AETException {
		visitCollectMetadata(metadata);
		queryParamsMap.put("comparatorModule", metadata.getComparatorModule());
		queryParamsMap.put("comparatorModuleName", metadata.getComparatorModuleName());
	}

	@Override
	public void visit(PatternMetadata metadata) throws AETException {
		visitCollectMetadata(metadata);
	}

	@Override
	public void visit(ReportMetadata metadata) throws AETException {
		visitNodeMetadata(metadata);
		queryParamsMap.put("reporterModule", metadata.getReporterModule());
		queryParamsMap.put("correlationId", metadata.getCorrelationId());
	}

	private void visitNodeMetadata(NodeMetadata metadata) {
		queryParamsMap.put("domain", metadata.getDomain());
		queryParamsMap.put("version", metadata.getVersion());
		queryParamsMap.put("company", metadata.getCompany());
		queryParamsMap.put("project", metadata.getProject());
		queryParamsMap.put("testSuiteName", metadata.getTestSuiteName());
		queryParamsMap.put("environment", metadata.getEnvironment());
		queryParamsMap.put("artifactType", metadata.getArtifactType());
	}

	private void visitUrlNodeMetadata(UrlNodeMetadata metadata) {
		visitNodeMetadata(metadata);
		queryParamsMap.put("correlationId", metadata.getCorrelationId());
		queryParamsMap.put("testName", metadata.getTestName());
		queryParamsMap.put("collectorModuleName", metadata.getCollectorModuleName());
		queryParamsMap.put("collectorModule", metadata.getCollectorModule());
		queryParamsMap.put("url", metadata.getUrl());
		queryParamsMap.put("urlName", metadata.getUrlName());
	}

	private void visitCollectMetadata(CollectMetadata metadata) {
		visitUrlNodeMetadata(metadata);
		queryParamsMap.put("description", metadata.getDescription());
	}
}
