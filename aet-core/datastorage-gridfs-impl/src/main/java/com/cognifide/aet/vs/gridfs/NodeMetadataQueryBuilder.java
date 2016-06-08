/*
 * Cognifide AET :: Data Storage GridFs Implementation
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
package com.cognifide.aet.vs.gridfs;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.vs.visitors.MetadataArtifactQueryParametersVisitor;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import java.util.Collection;
import java.util.Map;

public class NodeMetadataQueryBuilder {

	private String prefix;

	private Map<String, Object> queryParameters;

	public NodeMetadataQueryBuilder(NodeMetadata nodeMetadata) throws AETException {
		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		nodeMetadata.accept(visitor);
		queryParameters = visitor.getQueryParameters();
	}

	public NodeMetadataQueryBuilder setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public NodeMetadataQueryBuilder removeAllKeys(Collection<String> keys) {
		queryParameters.keySet().removeAll(keys);
		return this;
	}

	public NodeMetadataQueryBuilder retainAllKeys(Collection<String> keys) {
		queryParameters.keySet().retainAll(keys);
		return this;
	}

	public NodeMetadataQueryBuilder add(String key, Object val) {
		queryParameters.put(key, val);
		return this;
	}

	public NodeMetadataQueryBuilder remove(String key) {
		queryParameters.remove(key);
		return this;
	}

	public DBObject get() {
		BasicDBObjectBuilder basicDBObjectBuilder = new BasicDBObjectBuilder();
		for (Map.Entry<String, Object> entry : queryParameters.entrySet()) {
			basicDBObjectBuilder.add(prefix + entry.getKey(), entry.getValue());
		}
		return basicDBObjectBuilder.get();
	}
}
