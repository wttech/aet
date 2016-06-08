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
package com.cognifide.aet.vs.rebase;

import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.communication.api.RebaseOperationStatus;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.vs.VersionStorage;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service(Rebaser.class)
@Component(immediate = true, label = "AET Rebase Service", description = "AET Rebase Service", metatype = true)
public class Rebaser {

	private static final Logger LOGGER = LoggerFactory.getLogger(Rebaser.class);

	private static final String N_REBASE_THREADS = "nRebaseThreads";

	private static final int DEFAULT_N_REBASE_THREADS = 5;

	private static final int DEFAULT_TIMEOUT = 2000;

	private static final int DEFAULT_CACHE_TIMEOUT = 15000;

	private static final String TIMEOUT = "timeout";

	private static final String CACHE_TIMEOUT = "cacheTimeout";

	private static final String IN_PROGRESS = "Rebase in progress";

	@Property(name = N_REBASE_THREADS, label = "nRebaseThreads",
			description = "The number of threads available for rebase", intValue = DEFAULT_N_REBASE_THREADS)
	private Integer nThreads;

	@Property(name = TIMEOUT, label = "timeout",
			description = "Defines how long (ms) rebase would wait for operation status before returning \""
					+ IN_PROGRESS + "\"", intValue = DEFAULT_TIMEOUT)
	private Integer timeout;

	@Property(name = CACHE_TIMEOUT, label = "cacheTimeout",
			description = "Defines how long (ms) operation status will be available after last access",
			intValue = DEFAULT_CACHE_TIMEOUT)
	private Integer cacheTimeout;

	@Reference
	private VersionStorage storage;

	private ExecutorService executor;

	private LoadingCache<NodeMetadata, Future<RebaseOperationStatus>> tasks;

	public OperationStatus rebase(UrlNodeMetadata nodeMetadata) {
		RebaseOperationStatus operationStatus;
		Future<RebaseOperationStatus> task = tasks.getUnchecked(nodeMetadata);
		try {
			operationStatus = task.get(timeout, TimeUnit.MILLISECONDS);
			tasks.invalidate(nodeMetadata);
		} catch (TimeoutException | InterruptedException e) {
			LOGGER.warn("Rebase for {} still in progress", nodeMetadata);
			operationStatus = new RebaseOperationStatus(false, IN_PROGRESS, false);
		} catch (ExecutionException e) {
			LOGGER.error("Exception occurs during rebase", e);
			operationStatus = new RebaseOperationStatus(false, e.getCause().getMessage(), true);
		}
		return operationStatus;
	}

	@Activate
	public void activate(Map<String, ?> properties) {
		nThreads = PropertiesUtil.toInteger(properties.get(N_REBASE_THREADS), DEFAULT_N_REBASE_THREADS);
		timeout = PropertiesUtil.toInteger(properties.get(TIMEOUT), DEFAULT_TIMEOUT);
		cacheTimeout = PropertiesUtil.toInteger(properties.get(CACHE_TIMEOUT), DEFAULT_CACHE_TIMEOUT);

		executor = Executors.newFixedThreadPool(nThreads);
		tasks = CacheBuilder.newBuilder().expireAfterAccess(cacheTimeout, TimeUnit.MILLISECONDS).build(
				new RebaseJobLoader(executor, storage));
		LOGGER.info(
				"AET Rebase Service activated with parameters: [nThreads: {} ; timeout: {} ms ; cacheTimeout: {} .]",
				nThreads, timeout, cacheTimeout);
	}

	@Deactivate
	public void deactivate() {
		executor.shutdown();
		tasks.invalidateAll();
		tasks = null;
		LOGGER.info("AET Rebase Service deactivated.");
	}

}
