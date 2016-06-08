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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;

import com.cognifide.aet.communication.api.node.ArtifactType;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.vs.Node;
import com.cognifide.aet.vs.Result;
import com.cognifide.aet.vs.VersionStorageException;
import com.google.common.base.Charsets;
import com.google.gson.Gson;

/**
 * DefaultNodeImpl - default Node implementation.
 */
public class DefaultNodeImpl implements Node {

	private static final Gson GSON = new Gson();

	private static final String RESULT_JSON = "result.json";

	private final ArtifactType artifactType;

	private final GridFsStorage storage;

	private final NodeMetadata nodeMetadata;

	public DefaultNodeImpl(ArtifactType artifactType, GridFsStorage storage, NodeMetadata nodeMetadata) {
		this.artifactType = artifactType;
		this.storage = storage;
		this.nodeMetadata = nodeMetadata;
	}

	@Override
	public String saveResult(Result result) throws VersionStorageException {
		InputStream stream = null;
		try {
			stream = IOUtils.toInputStream(GSON.toJson(result), Charsets.UTF_8);
			return save(nodeMetadata, RESULT_JSON, stream);
		} catch (Exception e) {
			throw new VersionStorageException(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	@Override
	public <T> T getResult(Class<T> clazz) throws VersionStorageException {
		InputStream stream = get(nodeMetadata, RESULT_JSON);
		if (stream != null) {
			Reader reader = null;
			try {
				reader = new InputStreamReader(stream, Charsets.UTF_8);
				return GSON.fromJson(reader, clazz);
			} catch (Exception e) {
				throw new VersionStorageException(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
		return null;
	}

	@Override
	public String saveData(String name, InputStream data) {
		String result = null;
		try {
			result = save(nodeMetadata, name, data);
		} finally {
			IOUtils.closeQuietly(data);
		}
		return result;
	}

	@Override
	public InputStream getData(String name) throws VersionStorageException {
		return get(nodeMetadata, name);
	}

	@Override
	public Boolean removeData(String name) throws VersionStorageException {
		return storage.removeNodeArtifact(nodeMetadata, artifactType, name);
	}

	@Override
	public String getDataMD5(String name) throws VersionStorageException {
		return storage.getDataMD5(nodeMetadata, name, artifactType);

	}

	private InputStream get(NodeMetadata nodeMetadata, String fileName) {
		return storage.getStream(nodeMetadata, fileName, artifactType);
	}

	private String save(NodeMetadata nodeMetadata, String fileName, InputStream stream) {
		return storage.saveNode(artifactType, nodeMetadata, fileName, stream);
	}

}
