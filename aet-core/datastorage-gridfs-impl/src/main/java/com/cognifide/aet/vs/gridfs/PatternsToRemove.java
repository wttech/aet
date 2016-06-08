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

import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tomasz.misiewicz on 2014-09-24.
 */
public class PatternsToRemove {

	private DBObject metadata;

	private Set<Integer> allVersions = Sets.newHashSet();

	public DBObject getMetadata() {
		return metadata;
	}

	public void setMetadata(DBObject metadata) {
		this.metadata = new BasicDBObject();
		Iterator<Map.Entry<String, Object>> it = metadata.toMap().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			if (entry.getValue() != null) {
				this.metadata.put("metadata." + entry.getKey().toString(), entry.getValue());
			}
		}
	}

	public void addVersion(Integer version) {
		allVersions.add(version);
	}

	private List<Integer> getVersionsToDelete(Integer keepVersions) {
		Integer keepVersionsTmp = keepVersions;
		List<Integer> result;
		List allVersionsList = Lists.newArrayList(allVersions);
		Collections.sort(allVersionsList,Collections.reverseOrder());
		Integer size = allVersionsList.size();
		if (keepVersionsTmp < size) {
			result = allVersionsList.subList(keepVersionsTmp, size);
		} else {
			result = new ArrayList<Integer>();
		}
		return result;
	}

	public DBObject getDeleteQuery(Integer keepVersions) {
		DBObject result = null;
		Iterator<Integer> iterator = getVersionsToDelete(keepVersions).iterator();
		BasicDBList query = new BasicDBList();
		while (iterator.hasNext()) {
			query.add(new BasicDBObject(GridFsHelper.inMetadata(PatternMetadata.VERSION_ATTRIBUTE_NAME),
					iterator.next()));
		}
		if (!query.isEmpty()) {
			result = getMetadata();
			result.removeField("metadata.version");
			result.put("$or", query);
		}
		return result;
	}
}
