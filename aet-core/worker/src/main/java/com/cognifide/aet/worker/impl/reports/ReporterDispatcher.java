/*
 * Cognifide AET :: Worker
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
package com.cognifide.aet.worker.impl.reports;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.helpers.MessageFormatter;

import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.job.ReporterJobData;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.vs.VersionStorage;


@Component(immediate = true, label = "AET Report Dispatcher")
@Service(ReporterDispatcher.class)
public class ReporterDispatcher {

	@Reference
	private VersionStorage versionStorage;
	
	@Reference(referenceInterface = ReporterJobHandler.class, cardinality=ReferenceCardinality.OPTIONAL_MULTIPLE, bind="bindHandler", unbind="unbindHandler", policy = ReferencePolicy.DYNAMIC)
	private List<ReporterJobHandler> handlers;
	
	public NodeMetadata run(ReporterJobData job) throws AETException {
		
		ReporterHandlerWorkload workload = ReporterHandlerWorkload.Builder.createBuilder().withReporterJobData(job).withVersionStorage(versionStorage).build();
	
		for (ReporterJobHandler handler : handlers) {
			if (handler.accept(workload)) {
				return handler.handle(workload);
			}
		}
		
		throw new AETException(MessageFormatter.format("Couldn't found appropiate reporter job handler: {}", job.getReporterStep().getParameters()).getMessage());
	}
	
	protected void bindHandler(ReporterJobHandler filter){
	    if(handlers == null){
	    	handlers = new ArrayList<ReporterJobHandler>();
	    }
	    handlers.add(filter);
	}
	 
	protected void unbindHandler(ReporterJobHandler filter){
		handlers.remove(filter);
	}
	
}
