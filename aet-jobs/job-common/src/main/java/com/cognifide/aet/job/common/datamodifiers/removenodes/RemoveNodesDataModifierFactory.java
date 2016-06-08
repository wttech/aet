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
package com.cognifide.aet.job.common.datamodifiers.removenodes;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import com.cognifide.aet.job.api.datamodifier.DataModifierFactory;
import com.cognifide.aet.job.api.datamodifier.DataModifierJob;
import com.cognifide.aet.job.api.exceptions.ParametersException;

@Component
@Service
public class RemoveNodesDataModifierFactory implements DataModifierFactory {

	@Override
	public String getName() {
		return RemoveNodesDataModifier.NAME;
	}

	@Override
	public DataModifierJob<String> createInstance(Map<String, String> params) throws ParametersException {
		RemoveNodesDataModifier modifier = new RemoveNodesDataModifier();
		modifier.setParameters(params);
		return modifier;
	}

}
