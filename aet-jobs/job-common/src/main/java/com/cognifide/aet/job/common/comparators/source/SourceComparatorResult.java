/*
 * Cognifide AET :: Job Common
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
package com.cognifide.aet.job.common.comparators.source;

import java.util.List;

import com.cognifide.aet.job.common.comparators.source.diff.ResultDelta;
import com.cognifide.aet.vs.ComparatorProperties;
import com.cognifide.aet.vs.ComparatorResult;
import com.cognifide.aet.vs.ResultStatus;

public class SourceComparatorResult extends ComparatorResult {

	private static final long serialVersionUID = -3843587296533012274L;

	private List<ResultDelta> deltas;

	private String sourceCompareType;

	public SourceComparatorResult(ResultStatus status, ComparatorProperties properties) {
		super(status, properties, true);
	}

	public List<ResultDelta> getDeltas() {
		return deltas;
	}

	public void setDeltas(List<ResultDelta> deltas) {
		this.deltas = deltas;
	}

	public String getSourceCompareType() {
		return sourceCompareType;
	}

	public void setSourceCompareType(String sourceCompareType) {
		this.sourceCompareType = sourceCompareType;
	}
}
