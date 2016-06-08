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
package com.cognifide.aet.vs.rest;

import java.util.Collection;

import javax.jws.WebService;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.vs.rest.exceptions.AetRestEndpointException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Created by tomasz.misiewicz on 2014-10-13.
 */
@WebService
@Api(value = "/")
@Produces(MediaType.APPLICATION_JSON)
public interface AetRestEndpoint {
	/* @formatter:off */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of artifacts for given url", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes")
	Collection<String> getArtifactTypes(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	@ApiOperation(value = "returns list of all Companies existing in storage", response = Collection.class)
	Collection<String> getCompanies() throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of artifacts for specified data node collectorType and collectorName", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data/{collectorType}/{collectorName}/{correlationId}")
	Collection<String> getDataArtifacts(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project Name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite Name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase Name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "correlationId", required = true) @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException;

	@GET
	@Produces("application/octet-stream")
	@ApiOperation(value = "returns specified data artifact as file", response = Response.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data/{collectorType}/{collectorName}/{correlationId}/{artifactName}")
	Response getDataArtifactsData(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project Name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite Name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase Name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "Artifact Name", required = true) @PathParam("artifactName") String artifactName,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns collectorTypes for specified artifactType", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data")
	Collection<String> getDataCollectorTypes(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns collectorNames for specified artifactType", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data/{collectorType}")
	Collection<String> getDataCollectorNames(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of artifacts for specified data node modules", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data/{collectorType}/{collectorName}")
	Collection<String> getDataExecutions(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project Name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite Name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase Name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of test environments in testSuite", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}")
	Collection<String> getEnvironments(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns information regarding single url", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}")
	Collection getUrlName(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns available patterns version for specified collector", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns/{collectorType}/{collectorName}/versions")
	Collection getPatternModuleVersions(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "urlName name (url-escaped)", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "returns specified artifact data as a file", response = Response.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns/{collectorType}/{collectorName}/{artifactName}")
	Response getPatternsArtifactData(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "Artifact name", required = true) @PathParam("artifactName") String artifactName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns artifacts names for specified pattern comparator module", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns/{collectorType}/{collectorName}")
	Collection<String> getPatternsArtifacts(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns collectorTypes for patterns", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns")
	Collection<String> getPatternsCollectorTypes(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns collectorNames for specified artifactType", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns/{collectorType}")
	Collection<String> getPatternsCollectorNames(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Type of collector", required = true) @PathParam("collectorType") String collectorType)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of Projects", response = Collection.class)
	@Path("/{company}")
	Collection<String> getProjects(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "returns specified report artifact as a file", response = Response.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/reports/{reporterModule}/{correlationId}/{artifactName}")
	Response getReportsArtifactsData(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "Reporter module", required = true) @PathParam("reporterModule") String reporterModule,
			@ApiParam(value = "Artifact name", required = true) @PathParam("artifactName") String artifactName,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException;

	@GET
	@ApiOperation(value = "returns list of reports artifacts for given report module name and correlationId", response = Collection.class)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/reports/{reporterModule}/{correlationId}")
	Collection<String> getReportsExecutionArtifacts(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "Reporter module", required = true) @PathParam("reporterModule") String reporterModule,
			@ApiParam(value = "CorrelationId name", required = true) @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException;

	@GET
	@ApiOperation(value = "returns list of reports executions Id's for given report module name", response = Collection.class)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/reports/{reporterModule}")
	Collection<String> getReportsExecutions(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "Reporter module", required = true) @PathParam("reporterModule") String reporterModule)
			throws AetRestEndpointException;

	@GET
	@ApiOperation(value = "returns list of report modules", response = Collection.class)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/reports")
	Collection<String> getReportsModules(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of collectorTypes for results nodes", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results")
	Collection<String> getResultsCollectorsTypes(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of collectorNames for given collectorType", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}")
	Collection<String> getResultsCollectorsNames(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of results artifacts for provided correlationId", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}/{correlationId}")
	Collection<String> getResultsArtifacts(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "Comparator type", required = true) @PathParam("comparatorType") String comparatorType,
			@ApiParam(value = "Comparator name", required = true) @PathParam("comparatorName") String comparatorName,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@ApiOperation(value = "return results artifact as a file", response = Response.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}/{correlationId}/{artifactName}")
	Response getResultsArtifactsData(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page Url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "Comparator type", required = true) @PathParam("comparatorType") String comparatorType,
			@ApiParam(value = "Comparator name", required = true) @PathParam("comparatorName") String comparatorName,
			@ApiParam(value = "Artifact name", required = true) @PathParam("artifactName") String artifactName,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of existing comparators types for given collector module", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}/{collectorName}")
	Collection<String> getResultsComparatorsTypes(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of existing collector types for given collector", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}/{collectorName}/{comparatorType}")
	Collection<String> getResultsComparatorsNames(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "Comparator type", required = true) @PathParam("comparatorType") String comparatorType)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of existing executions Id's for provided comparator", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}")
	Collection<String> getResultsExecutions(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Page url", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "Comparator type", required = true) @PathParam("comparatorType") String comparatorType,
			@ApiParam(value = "Comparator name", required = true) @PathParam("comparatorName") String comparatorName)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns information about testCase (currently only next segment of url path)", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}")
	Collection getTestCase(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of test url's in testcase", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls")
	Collection<String> getTestCaseUrls(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of testcases in testSuite for given environment ", response = Collection.class)
	@Path("/{company}/{project}/{testSuite}/{environment}")
	Collection<String> getTestCases(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment)
			throws AetRestEndpointException;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "returns list of test suites in project", response = Collection.class)
	@Path("/{company}/{project}")
	Collection<String> getTestSuites(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project)
			throws AetRestEndpointException;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Rebase pattern module by provided data module and correlationId", response = OperationStatus.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data/{collectorType}/{collectorName}")
	OperationStatus rebaseModule(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "urlName name (url-escaped)", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "CorrelationId", required = true) @FormParam("rebaseCorrelationId") String rebaseCorrelationId);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Rebase pattern testCase by provided data testCase and correlationId", response = OperationStatus.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}")
	OperationStatus rebaseTestCase(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "CorrelationId", required = true) @FormParam("rebaseCorrelationId") String correlationId);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Rebase pattern environment by provided testsuite and correlationId", response = OperationStatus.class)
	@Path("/{company}/{project}/{testSuite}/{environment}")
	OperationStatus rebaseTestSuite(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "CorrelationId", required = true) @FormParam("rebaseCorrelationId") String correlationId);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Rebase pattern url by provided data url and correlationId", response = OperationStatus.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}")
	OperationStatus rebaseUrl(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "urlName name (url-escaped)", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "CorrelationId", required = true) @FormParam("rebaseCorrelationId") String correlationId);

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Restore specified testCase patterns to provided version", response = OperationStatus.class)
	@Path("/{company}/{project}/{testSuite}/{environment}")
	OperationStatus restoreEnvironment(

			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "Pattern version to restore (int)", required = true) @FormParam("restoreVersion") Long version)
			throws AetRestEndpointException;

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Restore specified pattern  collectorType & collectorName  to provided version", response = OperationStatus.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns/{collectorType}/{collectorName}")
	OperationStatus restoreModule(

			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "urlName name (url-escaped)", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector Type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "Pattern version to restore (int)", required = true) @FormParam("restoreVersion") Long version)
			throws AetRestEndpointException;

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Restore specified testCase patterns to provided version", response = OperationStatus.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}")
	OperationStatus restoreTestCase(

			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "Pattern version to restore (int)", required = true) @FormParam("restoreVersion") Long version)
			throws AetRestEndpointException;

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Restore specified testSuite pattern to provided version", response = OperationStatus.class)
	@Path("/{company}/{project}/{testSuite}")
	OperationStatus restoreTestSuite(

			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Pattern version to restore (int)", required = true) @FormParam("restoreVersion") Long version)
			throws AetRestEndpointException;

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Restore specified url patterns to provided version", response = OperationStatus.class)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}")
	OperationStatus restoreUrl(

			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("testCase") String testCase,
			@ApiParam(value = "urlName name (url-escaped)", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Pattern version to restore (int)", required = true) @FormParam("restoreVersion") Long version)
			throws AetRestEndpointException;
}
/* @formatter:on */