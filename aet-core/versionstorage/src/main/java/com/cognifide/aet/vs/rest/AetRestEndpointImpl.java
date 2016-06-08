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

import java.io.InputStream;
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

import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.cognifide.aet.communication.api.node.PatternMetadata;
import com.cognifide.aet.communication.api.node.ReportMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.communication.api.node.builders.UrlNodeMetadataBuilder;
import com.cognifide.aet.vs.rebase.Rebaser;
import org.apache.commons.io.IOUtils;

import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.builders.NodeMetadataBuilder;
import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.vs.VersionStorage;
import com.cognifide.aet.vs.rest.exceptions.AetRestEndpointException;
import com.google.common.collect.Lists;
import com.google.common.net.UrlEscapers;

/**
 * Created by tomasz.misiewicz on 2014-08-21.
 */

@Path("/")
@WebService
public class AetRestEndpointImpl implements AetRestEndpoint {
	/* @formatter:off */
	private VersionStorage storage;

	private Rebaser rebaser;

	/**
	 * Do not remove this setter, it is used by OSGi
	 * @param storage
	 */
	public void setStorage(VersionStorage storage) {
		this.storage = storage;
	}

	/**
	 * Do not remove this setter, it is used by OSGi
	 * @param rebaser
	 */
	public void setRebaser(Rebaser rebaser) {
		this.rebaser = rebaser;
	}

	@Override
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<String> getCompanies() throws AetRestEndpointException {
		Collection result;
		try {
			result = storage.getCompanies(NodeMetadataBuilder.getNodeMetadata().build());
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}")
	public Collection<String> getProjects(@PathParam("company") String company)
			throws AetRestEndpointException {
		Collection result;
		try {
			result = storage.getProjects(NodeMetadataBuilder.getNodeMetadata().withCompany(company).build());
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}")
	public Collection<String> getTestSuites(@PathParam("company") String company,
											@PathParam("project") String project) throws AetRestEndpointException {
		Collection result;
		try {
			result = storage.getTestSuites(NodeMetadataBuilder.getNodeMetadata().withCompany(company)
					.withProject(project).build());
		} catch (Exception e) {
			throw new AetRestEndpointException("Bad request", e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}")
	public Collection<String> getEnvironments(@PathParam("company") String company,
											  @PathParam("project") String project,
											  @PathParam("testSuite") String testSuite)
			throws AetRestEndpointException {
		return AetRestEndpointHelper.validate(storage.getEnvironments(NodeMetadataBuilder.getNodeMetadata()
				.withCompany(company).withProject(project).withTestSuiteName(testSuite).build()));
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}")
	public Collection<String> getTestCases(@PathParam("company") String company,
										   @PathParam("project") String project,
										   @PathParam("testSuite") String testSuite,
										   @PathParam("environment") String environment) throws AetRestEndpointException {
		return AetRestEndpointHelper.validate(storage.getTestCases(NodeMetadataBuilder.getNodeMetadata()
				.withCompany(company).withProject(project).withTestSuiteName(testSuite)
				.withEnvironment(environment).build()));

	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}")
	public Collection getTestCase(

			@PathParam("company") String company,
			@PathParam("project") String project,
			@PathParam("testSuite") String testSuite,
			@PathParam("environment") String environment,
			@PathParam("testCase") String testCase) throws AetRestEndpointException {
		/*
		 * This is temporary workaround. Concrete implementation should returns more info about urls in
		 * test case. Validate if this node have any subnodes and returns static 'urls' string in collection,
		 * if not, returns 404
		 */
		AetRestEndpointHelper.validate(storage.getTestCaseUrls(UrlNodeMetadataBuilder.getUrlNodeMetadata()
				.withTestName(testCase).withCompany(company).withProject(project).withEnvironment(environment)
				.withTestSuiteName(testSuite).build()));
		return Lists.newArrayList("urls");
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls")
	public Collection<String> getTestCaseUrls(@PathParam("company") String company,
											  @PathParam("project") String project,
											  @PathParam("testSuite") String testSuite,
											  @PathParam("environment") String environment,
											  @PathParam("testCase") String testCase)
			throws AetRestEndpointException {
		return AetRestEndpointHelper.validate(storage.getTestCaseUrls(UrlNodeMetadataBuilder
				.getUrlNodeMetadata().withTestName(testCase).withCompany(company).withProject(project)
				.withEnvironment(environment).withTestSuiteName(testSuite).build()));
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}")
	public Collection getUrlName(@PathParam("company") String company,
								 @PathParam("project") String project,
								 @PathParam("testSuite") String testSuite,
								 @PathParam("environment") String environment,
								 @PathParam("testCase") String testCase,
								 @PathParam("urlName") String urlName)
			throws AetRestEndpointException {
		Collection result = null;
		try {
			Collection urlNames = storage.getTestCaseUrls(UrlNodeMetadataBuilder.getUrlNodeMetadata()
					.withTestName(testCase).withCompany(company).withProject(project).withEnvironment(
							environment)
					.withTestSuiteName(testSuite).build());
			String encodedurlName = UrlEscapers.urlFormParameterEscaper().escape(urlName);
			if (urlNames.contains(encodedurlName)) {
				result = Lists.newArrayList("artifactTypes");
			}
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes")
	public Collection<String> getArtifactTypes(@PathParam("company") String company,
											   @PathParam("project") String project,
											   @PathParam("testSuite") String testSuite,
											   @PathParam("environment") String environment,
											   @PathParam("testCase") String testCase,
											   @PathParam("urlName") String urlName) throws AetRestEndpointException {
		Collection result = null;
		try {
			Collection urlNames = storage.getTestCaseUrls(UrlNodeMetadataBuilder.getUrlNodeMetadata()
					.withTestName(testCase).withCompany(company).withProject(project)
					.withTestSuiteName(testSuite).build());
			String encodedUrlName = UrlEscapers.urlFormParameterEscaper().escape(urlName);
			if (urlNames.contains(encodedUrlName)) {
				result = storage.getTypes();
			}
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns")
	public Collection<String> getPatternsCollectorTypes(@PathParam("company") String company,
														@PathParam("project") String project,
														@PathParam("testSuite") String testSuite,
														@PathParam("environment") String environment,
														@PathParam("testCase") String testCase,
														@PathParam("urlName") String urlName) throws AetRestEndpointException {

		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withUrlName(urlName).build();
		return AetRestEndpointHelper.validate(storage.getPatternsCollectorsTypes(nodeMetadata));
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns/{collectorType}")
	public Collection<String> getPatternsCollectorNames(@PathParam("company") String company,
														@PathParam("project") String project,
														@PathParam("testSuite") String testSuite,
														@PathParam("environment") String environment,
														@PathParam("testCase") String testCase,
														@PathParam("collectorType") String collectorType,
														@PathParam("urlName") String urlName)
			throws AetRestEndpointException {

		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withUrlName(urlName).withCollectorModule(collectorType).build();
		return AetRestEndpointHelper.validate(storage.getPatternsCollectorsNames(nodeMetadata));

	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns/{collectorType}/{collectorName}")
	public Collection<String> getPatternsArtifacts(@PathParam("company") String company,
												   @PathParam("project") String project,
												   @PathParam("testSuite") String testSuite,
												   @PathParam("environment") String environment,
												   @PathParam("testCase") String testCase,
												   @PathParam("urlName") String urlName,
												   @PathParam("collectorType") String collectorType,
												   @PathParam("collectorName") String collectorName) throws AetRestEndpointException {
		PatternMetadata patternMetadata = new PatternMetadata(company, project, testSuite, environment,
				testCase, urlName, collectorType, collectorName, null);
		Collection result = storage.getPatternsArtifacts(patternMetadata);
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns/{collectorType}/{collectorName}/{artifactName}")
	public Response getPatternsArtifactData(@PathParam("company") String company,
											@PathParam("project") String project,
											@PathParam("testSuite") String testSuite,
											@PathParam("environment") String environment,
											@PathParam("testCase") String testCase,
											@PathParam("urlName") String urlName,
											@PathParam("collectorType") String collectorType,
											@PathParam("collectorName") String collectorName,
											@PathParam("artifactName") String artifactName)
			throws AetRestEndpointException {
		InputStream dataStream = null;
		try {
			PatternMetadata patternMetadata = new PatternMetadata(company, project, testSuite, environment,
					testCase, urlName, collectorType, collectorName, null);
			dataStream = storage.getPatternsArtifactData(patternMetadata, artifactName);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.UNABLE_TO_RETRIEVE_DATA_MSG, e);
		} finally {
			IOUtils.closeQuietly(dataStream);
		}
		return AetRestEndpointHelper.inputStreamToResponse(dataStream, artifactName);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data")
	public Collection<String> getDataCollectorTypes(@PathParam("company") String company,
													@PathParam("project") String project,
													@PathParam("testSuite") String testSuite,
													@PathParam("environment") String environment,
													@PathParam("testCase") String testCase,
													@PathParam("urlName") String urlName) throws AetRestEndpointException {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withUrlName(urlName).build();
		return AetRestEndpointHelper.validate(storage.getDataCollectorTypes(nodeMetadata));
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data/{collectorType}")
	public Collection<String> getDataCollectorNames(@PathParam("company") String company,
													@PathParam("project") String project,
													@PathParam("testSuite") String testSuite,
													@PathParam("environment") String environment,
													@PathParam("testCase") String testCase,
													@PathParam("urlName") String urlName,
													@PathParam("collectorType") String collectorType)
			throws AetRestEndpointException {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withUrlName(urlName).withCollectorModule(collectorType).build();
		return AetRestEndpointHelper.validate(storage.getDataCollectorNames(nodeMetadata));
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data/{collectorType}/{collectorName}")
	public Collection<String> getDataExecutions(@PathParam("company") String company,
												@PathParam("project") String project,
												@PathParam("testSuite") String testSuite,
												@PathParam("environment") String environment,
												@PathParam("testCase") String testCase,
												@PathParam("urlName") String urlName,
												@PathParam("collectorType") String collectorType,
												@PathParam("collectorName") String collectorName) throws AetRestEndpointException {
		Collection result;
		try {
			result = storage.getDataExecutions(UrlNodeMetadataBuilder.getUrlNodeMetadata()
					.withCompany(company).withProject(project).withTestSuiteName(testSuite)
					.withEnvironment(environment).withTestName(testCase).withUrlName(urlName)
					.withCollectorModule(collectorType).withCollectorModuleName(collectorName).build());
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data/{collectorType}/{collectorName}/{correlationId}")
	public Collection<String> getDataArtifacts(@PathParam("company") String company,
											   @PathParam("project") String project,
											   @PathParam("testSuite") String testSuite,
											   @PathParam("environment") String environment,
											   @PathParam("testCase") String testCase,
											   @PathParam("urlName") String urlName,
											   @PathParam("collectorType") String collectorType,
											   @PathParam("collectorName") String collectorName,
											   @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException {
		Collection result;
		try {
			CollectMetadata collectMetadata = new CollectMetadata(company, project, testSuite, environment,
					correlationId, testCase, null, urlName, collectorType, collectorName);
			result = storage.getDataArtifacts(collectMetadata);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data/{collectorType}/{collectorName}/{correlationId}/{artifactName}")
	public Response getDataArtifactsData(@PathParam("company") String company,
										 @PathParam("project") String project,
										 @PathParam("testSuite") String testSuite,
										 @PathParam("environment") String environment,
										 @PathParam("testCase") String testCase,
										 @PathParam("urlName") String urlName,
										 @PathParam("collectorType") String collectorType,
										 @PathParam("collectorName") String collectorName,
										 @PathParam("artifactName") String artifactName,
										 @PathParam("correlationId") String correlationId) throws AetRestEndpointException {
		InputStream dataStream = null;
		try {
			CollectMetadata collectMetadata = new CollectMetadata(company, project, testSuite, environment,
					correlationId, testCase, null, urlName, collectorType, collectorName);
			dataStream = storage.getDataArtifactData(collectMetadata, artifactName);
		} catch (Exception e) {
			throw new AetRestEndpointException("Unable to retrieve data", e);
		} finally {
			IOUtils.closeQuietly(dataStream);
		}
		return AetRestEndpointHelper.inputStreamToResponse(dataStream, artifactName);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results")
	public Collection<String> getResultsCollectorsTypes(@PathParam("company") String company,
														@PathParam("project") String project,
														@PathParam("testSuite") String testSuite,
														@PathParam("environment") String environment,
														@PathParam("testCase") String testCase,
														@PathParam("urlName") String urlName) throws AetRestEndpointException {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withUrlName(urlName).build();
		return AetRestEndpointHelper.validate(storage.getResultsCollectorsTypes(nodeMetadata));
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}")
	public Collection<String> getResultsCollectorsNames(@PathParam("company") String company,
														@PathParam("project") String project,
														@PathParam("testSuite") String testSuite,
														@PathParam("environment") String environment,
														@PathParam("testCase") String testCase,
														@PathParam("urlName") String urlName,
														@PathParam("collectorType") String collectorType)
			throws AetRestEndpointException {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withUrlName(urlName).withCollectorModule(collectorType).build();
		Collection result;
		try {
			result = storage.getResultsCollectorsNames(nodeMetadata);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}/{collectorName}")
	public Collection<String> getResultsComparatorsTypes(@PathParam("company") String company,
														 @PathParam("project") String project,
														 @PathParam("testSuite") String testSuite,
														 @PathParam("environment") String environment,
														 @PathParam("testCase") String testCase,
														 @PathParam("urlName") String urlName,
														 @PathParam("collectorType") String collectorType,
														 @PathParam("collectorName") String collectorName) throws AetRestEndpointException {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withUrlName(urlName).withCollectorModule(collectorType)
				.withCollectorModuleName(collectorName).build();
		Collection result;
		try {
			result = storage.getResultsComparatorsTypes(nodeMetadata);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}/{collectorName}/{comparatorType}")
	public Collection<String> getResultsComparatorsNames(@PathParam("company") String company,
														 @PathParam("project") String project,
														 @PathParam("testSuite") String testSuite,
														 @PathParam("environment") String environment,
														 @PathParam("testCase") String testCase,
														 @PathParam("urlName") String urlName,
														 @PathParam("collectorType") String collectorType,
														 @PathParam("collectorName") String collectorName,
														 @PathParam("comparatorType") String comparatorType) throws AetRestEndpointException {
		CompareMetadata nodeMetadata = new CompareMetadata(company, project, testSuite, environment, null,
				testCase, null, urlName, collectorType, collectorName, comparatorType, null);
		nodeMetadata.clearComparatorModuleName();
		Collection result;
		try {
			result = storage.getResultsComparatorsNames(nodeMetadata);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}")
	public Collection<String> getResultsExecutions(@PathParam("company") String company,
												   @PathParam("project") String project,
												   @PathParam("testSuite") String testSuite,
												   @PathParam("environment") String environment,
												   @PathParam("testCase") String testCase,
												   @PathParam("urlName") String urlName,
												   @PathParam("collectorType") String collectorType,
												   @PathParam("collectorName") String collectorName,
												   @PathParam("comparatorType") String comparatorType,
												   @PathParam("comparatorName") String comparatorName) throws AetRestEndpointException {
		CompareMetadata nodeMetadata = new CompareMetadata(company, project, testSuite, environment, null,
				testCase, null, urlName, collectorType, collectorName, comparatorType, comparatorName);
		Collection result;
		try {
			result = storage.getResultsExecutions(nodeMetadata);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}/{correlationId}")
	public Collection<String> getResultsArtifacts(@PathParam("company") String company,
												  @PathParam("project") String project,
												  @PathParam("testSuite") String testSuite,
												  @PathParam("environment") String environment,
												  @PathParam("testCase") String testCase,
												  @PathParam("urlName") String urlName,
												  @PathParam("collectorType") String collectorType,
												  @PathParam("collectorName") String collectorName,
												  @PathParam("comparatorType") String comparatorType,
												  @PathParam("comparatorName") String comparatorName,
												  @PathParam("correlationId") String correlationId) throws AetRestEndpointException {
		CompareMetadata nodeMetadata = new CompareMetadata(company, project, testSuite, environment,
				correlationId, testCase, null, urlName, collectorType, collectorName, comparatorType,
				comparatorName);
		Collection result;
		try {
			result = storage.getResultsArtifacts(nodeMetadata);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/results/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}/{correlationId}/{artifactName}")
	public Response getResultsArtifactsData(@PathParam("company") String company,
											@PathParam("project") String project,
											@PathParam("testSuite") String testSuite,
											@PathParam("environment") String environment,
											@PathParam("testCase") String testCase,
											@PathParam("urlName") String urlName,
											@PathParam("collectorType") String collectorType,
											@PathParam("collectorName") String collectorName,
											@PathParam("comparatorType") String comparatorType,
											@PathParam("comparatorName") String comparatorName,
											@PathParam("artifactName") String artifactName,
											@PathParam("correlationId") String correlationId)
			throws AetRestEndpointException {
		InputStream dataStream = null;

		try {
			CompareMetadata nodeMetadata = new CompareMetadata(company, project, testSuite, environment,
					correlationId, testCase, null, urlName, collectorType, collectorName, comparatorType,
					comparatorName);
			dataStream = storage.getResultsArtifactData(nodeMetadata, artifactName);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.UNABLE_TO_RETRIEVE_DATA_MSG, e);
		} finally {
			IOUtils.closeQuietly(dataStream);
		}

		return AetRestEndpointHelper.inputStreamToResponse(dataStream, artifactName);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/reports")
	public Collection<String> getReportsModules(@PathParam("company") String company,
												@PathParam("project") String project,
												@PathParam("testSuite") String testSuite,
												@PathParam("environment") String environment) throws AetRestEndpointException {
		Collection result;
		try {
			NodeMetadata nodeMetadata = NodeMetadataBuilder.getNodeMetadata().withCompany(company)
					.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment).build();
			result = storage.getReportsModules(nodeMetadata);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/reports/{reporterModule}")
	public Collection<String> getReportsExecutions(@PathParam("company") String company,
												   @PathParam("project") String project,
												   @PathParam("testSuite") String testSuite,
												   @PathParam("environment") String environment,
												   @PathParam("reporterModule") String reporterModule)
			throws AetRestEndpointException {
		Collection result;
		try {
			ReportMetadata nodeMetadata = new ReportMetadata(company, project, testSuite, environment,
					null, reporterModule);
			result = storage.getReportsExecutions(nodeMetadata);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/reports/{reporterModule}/{correlationId}")
	public Collection<String> getReportsExecutionArtifacts(@PathParam("company") String company,
														   @PathParam("project") String project,
														   @PathParam("testSuite") String testSuite,
														   @PathParam("environment") String environment,
														   @PathParam("reporterModule") String reporterModule,
														   @PathParam("correlationId") String correlationId) throws AetRestEndpointException {
		Collection result;
		try {
			ReportMetadata nodeMetadata = new ReportMetadata(company, project, testSuite, environment,
					correlationId, reporterModule);
			result = storage.getReportsExecutionArtifacts(nodeMetadata);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.NOT_FOUND_MSG, e);
		}
		return AetRestEndpointHelper.validate(result);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/{company}/{project}/{testSuite}/{environment}/reports/{reporterModule}/{correlationId}/{artifactName}")
	public Response getReportsArtifactsData(@PathParam("company") String company,
											@PathParam("project") String project,
											@PathParam("testSuite") String testSuite,
											@PathParam("environment") String environment,
											@PathParam("reporterModule") String reporterModule,
											@PathParam("artifactName") String artifactName,
											@PathParam("correlationId") String correlationId)
			throws AetRestEndpointException {
		InputStream dataStream = null;
		try {
			ReportMetadata nodeMetadata = new ReportMetadata(company, project, testSuite, environment,
					correlationId, reporterModule);
			dataStream = storage.getReportsArtifactData(nodeMetadata, artifactName);
		} catch (Exception e) {
			throw new AetRestEndpointException(AetRestEndpointHelper.UNABLE_TO_RETRIEVE_DATA_MSG, e);
		} finally {
			IOUtils.closeQuietly(dataStream);
		}
		return AetRestEndpointHelper.reportInputStreamToResponse(dataStream, artifactName);
	}

	@Override
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}")
	public OperationStatus rebaseTestSuite(@PathParam("company") final String company,
										   @PathParam("project") final String project,
										   @PathParam("testSuite") final String testSuite,
										   @PathParam("environment") final String environment,
										   @FormParam("rebaseCorrelationId") final String correlationId) {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withCorrelationId(correlationId).build();
		return rebaser.rebase(nodeMetadata);
	}


	@Override
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}")
	public OperationStatus rebaseTestCase(@PathParam("company") String company,
										  @PathParam("project") String project,
										  @PathParam("testSuite") String testSuite,
										  @PathParam("environment") String environment,
										  @PathParam("testCase") String testCase,
										  @FormParam("rebaseCorrelationId") String correlationId) {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withCorrelationId(correlationId).build();
		return rebaser.rebase(nodeMetadata);
	}

	@Override
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}")
	public OperationStatus rebaseUrl(@PathParam("company") String company,
									 @PathParam("project") String project,
									 @PathParam("testSuite") String testSuite,
									 @PathParam("environment") String environment,
									 @PathParam("testCase") String testCase,
									 @PathParam("urlName") String urlName,
									 @FormParam("rebaseCorrelationId") String correlationId) {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withUrlName(urlName).withCorrelationId(correlationId).build();
		return rebaser.rebase(nodeMetadata);
	}

	@Override
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/data/{collectorType}/{collectorName}")
	public OperationStatus rebaseModule(@PathParam("company") String company,
										@PathParam("project") String project,
										@PathParam("testSuite") String testSuite,
										@PathParam("environment") String environment,
										@PathParam("testCase") String testCase,
										@PathParam("urlName") String urlName,
										@PathParam("collectorType") String collectorType,
										@PathParam("collectorName") String collectorName,
										@FormParam("rebaseCorrelationId") String rebaseCorrelationId) {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withUrlName(urlName).withCollectorModule(collectorType)
				.withCollectorModuleName(collectorName).withCorrelationId(rebaseCorrelationId).build();
		return rebaser.rebase(nodeMetadata);
	}

	@Override
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}")
	public OperationStatus restoreTestSuite(@PathParam("company") String company,
											@PathParam("project") String project,
											@PathParam("testSuite") String testSuite,
											@FormParam("restoreVersion") Long version) throws AetRestEndpointException {
		NodeMetadata nodeMetadata = NodeMetadataBuilder.getNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).build();
		try {
			return storage.restorePatterns(PatternMetadata.fromNodeMetadata(nodeMetadata), version);
		} catch (Exception e) {
			throw new AetRestEndpointException(e.getMessage(), e);
		}
	}

	@Override
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}")
	public OperationStatus restoreEnvironment(@PathParam("company") String company,
											  @PathParam("project") String project,
											  @PathParam("testSuite") String testSuite,
											  @PathParam("environment") String environment,
											  @FormParam("restoreVersion") Long version)
			throws AetRestEndpointException {
		NodeMetadata nodeMetadata = NodeMetadataBuilder.getNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment).build();
		try {
			return storage.restorePatterns(PatternMetadata.fromNodeMetadata(nodeMetadata), version);
		} catch (Exception e) {
			throw new AetRestEndpointException(e.getMessage(), e);
		}
	}

	@Override
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}")
	public OperationStatus restoreTestCase(@PathParam("company") String company,
										   @PathParam("project") String project,
										   @PathParam("testSuite") String testSuite,
										   @PathParam("environment") String environment,
										   @PathParam("testCase") String testCase,
										   @FormParam("restoreVersion") Long version) throws AetRestEndpointException {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).build();
		try {
			return storage.restorePatterns(PatternMetadata.fromUrlNodeMetadata(nodeMetadata), version);
		} catch (Exception e) {
			throw new AetRestEndpointException(e.getMessage(), e);
		}
	}

	@Override
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}")
	public OperationStatus restoreUrl(
			@PathParam("company") String company,
			@PathParam("project") String project,
			@PathParam("testSuite") String testSuite,
			@PathParam("environment") String environment,
			@PathParam("testCase") String testCase,
			@PathParam("urlName") String urlName,
			@FormParam("restoreVersion") Long version) throws AetRestEndpointException {
		PatternMetadata patternMetadata = new PatternMetadata(company, project, testSuite, environment,
				testCase, urlName, null, null, null);
		try {
			return storage.restorePatterns(patternMetadata, version);
		} catch (Exception e) {
			throw new AetRestEndpointException(e.getMessage(), e);
		}
	}

	@Override
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns/{collectorType}/{collectorName}")
	public OperationStatus restoreModule(
			@PathParam("company") String company,
			@PathParam("project") String project,
			@PathParam("testSuite") String testSuite,
			@PathParam("environment") String environment,
			@PathParam("testCase") String testCase,
			@PathParam("urlName") String urlName,
			@PathParam("collectorType") String collectorType,
			@PathParam("collectorName") String collectorName, @FormParam("restoreVersion") Long version)
			throws AetRestEndpointException {
		PatternMetadata patternMetadata = new PatternMetadata(company, project, testSuite, environment,
				testCase, urlName, collectorType, collectorName, null);
		patternMetadata.setVersion(version);
		try {
			return storage.restorePatterns(patternMetadata, version);
		} catch (Exception e) {
			throw new AetRestEndpointException(e.getMessage(), e);
		}
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{testCase}/urls/{urlName}/artifactTypes/patterns/{collectorType}/{collectorName}/versions")
	public Collection getPatternModuleVersions(
			@PathParam("company") String company,
			@PathParam("project") String project,
			@PathParam("testSuite") String testSuite,
			@PathParam("environment") String environment,
			@PathParam("testCase") String testCase,
			@PathParam("urlName") String urlName,
			@PathParam("collectorType") String collectorType,
			@PathParam("collectorName") String collectorName)
			throws AetRestEndpointException {
		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project).withTestSuiteName(testSuite).withEnvironment(environment)
				.withTestName(testCase).withUrlName(urlName).withCollectorModule(collectorType)
				.withCollectorModuleName(collectorName).build();
		try {
			return AetRestEndpointHelper.validate(storage.getPatternModuleVersions(nodeMetadata));
		} catch (Exception e) {
			throw new AetRestEndpointException(e.getMessage(), e);
		}
	}
}
/* @formatter:on */
