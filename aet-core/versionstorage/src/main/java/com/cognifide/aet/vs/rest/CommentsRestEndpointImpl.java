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
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.cognifide.aet.comments.api.Comment;
import com.cognifide.aet.comments.api.manager.CommentsManager;
import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.communication.api.node.builders.UrlNodeMetadataBuilder;
import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.vs.rest.exceptions.AetRestEndpointException;

@Path("/comments/")
@WebService
public class CommentsRestEndpointImpl implements CommentsRestEndpoint {
	/* @formatter:off */

	private CommentsManager manager;

	/**
	 * Do not remove this setter, it is used by OSGi
	 *
	 * @param manager
	 */
	public void setManager(CommentsManager manager) {
		this.manager = manager;
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{correlationId}")
	public Collection<Comment> getAllComments(@PathParam("company") String company,
											  @PathParam("project") String project,
											  @PathParam("testSuite") String testSuite,
											  @PathParam("environment") String environment,
											  @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException {
		return manager.getAllComments(UrlNodeMetadataBuilder
				.getUrlNodeMetadata()
				.withCompany(company)
				.withProject(project)
				.withEnvironment(environment)
				.withTestSuiteName(testSuite)
				.withCorrelationId(correlationId)
				.withUrlName(null).build());
	}


	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{correlationId}")
	public Comment getTestComment(@PathParam("company") String company,
								  @PathParam("project") String project,
								  @PathParam("testSuite") String testSuite,
								  @PathParam("environment") String environment,
								  @PathParam("test") String test,
								  @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException {
		return manager.getComment(UrlNodeMetadataBuilder
				.getUrlNodeMetadata()
				.withCompany(company)
				.withProject(project)
				.withTestName(test)
				.withEnvironment(environment)
				.withTestSuiteName(testSuite)
				.withCorrelationId(correlationId)
				.withUrlName(null).build(), Comment.Level.TEST);
	}


	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{correlationId}")
	public Comment getUrlComment(@PathParam("company") String company,
								 @PathParam("project") String project,
								 @PathParam("testSuite") String testSuite,
								 @PathParam("environment") String environment,
								 @PathParam("test") String test,
								 @PathParam("urlName") String urlName,
								 @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException {

		return manager.getComment(UrlNodeMetadataBuilder
				.getUrlNodeMetadata()
				.withCompany(company)
				.withProject(project)
				.withTestName(test)
				.withEnvironment(environment)
				.withTestSuiteName(testSuite)
				.withCorrelationId(correlationId)
				.withUrlName(urlName).build(), Comment.Level.URL);
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}/{correlationId}")
	public Comment getTestCaseComment(@PathParam("company") String company,
									  @PathParam("project") String project,
									  @PathParam("testSuite") String testSuite,
									  @PathParam("environment") String environment,
									  @PathParam("test") String test,
									  @PathParam("urlName") String urlName,
									  @PathParam("collectorType") String collectorType,
									  @PathParam("collectorName") String collectorName,
									  @PathParam("comparatorType") String comparatorType,
									  @PathParam("comparatorName") String comparatorName,
									  @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException {

		CompareMetadata nodeMetadata = new CompareMetadata(company, project, testSuite, environment,
				correlationId, test, null, urlName, collectorType, collectorName, comparatorType,
				comparatorName);
		return manager.getComment(nodeMetadata, Comment.Level.TESTCASE);
	}

	@Override
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{correlationId}")
	public OperationStatus createOrUpdateTestComment(@PathParam("company") String company,
													 @PathParam("project") String project,
													 @PathParam("testSuite") String testSuite, @PathParam("environment") String environment,
													 @PathParam("test") String test,
													 @PathParam("correlationId") String correlationId,
													 @FormParam("content") String content) throws AetRestEndpointException {

		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata().withCompany(company)
				.withProject(project)
				.withTestSuiteName(testSuite)
				.withEnvironment(environment)
				.withTestName(test)
				.withUrlName(null)
				.withCorrelationId(correlationId).build();

		Comment comment = new Comment(nodeMetadata, content, Comment.Level.TEST);
		return manager.createOrUpdateComment(comment);
	}

	@Override
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{correlationId}")
	public OperationStatus createOrUpdateUrlComment(@PathParam("company") String company,
										 @PathParam("project") String project,
										 @PathParam("testSuite") String testSuite,
										 @PathParam("environment") String environment,
										 @PathParam("test") String test,
										 @PathParam("urlName") String urlName,
										 @PathParam("correlationId") String correlationId,
										 @FormParam("content") String content) throws AetRestEndpointException {

		UrlNodeMetadata nodeMetadata = UrlNodeMetadataBuilder
				.getUrlNodeMetadata().withCompany(company)
				.withProject(project)
				.withTestSuiteName(testSuite)
				.withEnvironment(environment)
				.withTestName(test)
				.withUrlName(urlName)
				.withCorrelationId(correlationId).build();

		Comment comment = new Comment(nodeMetadata, content, Comment.Level.URL);
		return manager.createOrUpdateComment(comment);
	}

	@Override
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}/{correlationId}")
	public OperationStatus createOrUpdateTestCaseComment(@PathParam("company") String company,
											  @PathParam("project") String project,
											  @PathParam("testSuite") String testSuite,
											  @PathParam("environment") String environment,
											  @PathParam("test") String test,
											  @PathParam("urlName") String urlName,
											  @PathParam("collectorType") String collectorType,
											  @PathParam("collectorName") String collectorName,
											  @PathParam("comparatorType") String comparatorType,
											  @PathParam("comparatorName") String comparatorName,
											  @PathParam("correlationId") String correlationId,
											  @FormParam("content") String content) throws AetRestEndpointException {

		//TODO: CompareMetadata has hardcodded ArtifactType.Result - consider creating new Metadata for comments
		CompareMetadata nodeMetadata = new CompareMetadata(company, project, testSuite, environment,
				correlationId, test, null, urlName, collectorType, collectorName, comparatorType,
				comparatorName);
		Comment comment = new Comment(nodeMetadata, content, Comment.Level.TESTCASE);
		return manager.createOrUpdateComment(comment);

	}

	@Override
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{correlationId}")
	public OperationStatus deleteAllComments(@PathParam("company") String company,
								  @PathParam("project") String project,
								  @PathParam("testSuite") String testSuite,
								  @PathParam("environment") String environment,
								  @PathParam("correlationId") String correlationId) throws AetRestEndpointException {

		return manager.deleteComments(UrlNodeMetadataBuilder
				.getUrlNodeMetadata()
				.withCompany(company)
				.withProject(project)
				.withEnvironment(environment)
				.withTestSuiteName(testSuite)
				.withCorrelationId(correlationId)
				.withUrlName(null).build());

	}

	@Override
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{correlationId}")
	public OperationStatus deleteTestComment(@PathParam("company") String company,
								  @PathParam("project") String project,
								  @PathParam("testSuite") String testSuite,
								  @PathParam("environment") String environment,
								  @PathParam("test") String test,
								  @PathParam("correlationId") String correlationId) throws AetRestEndpointException {
		OperationStatus result;

		NodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata()
				.withCompany(company)
				.withProject(project)
				.withTestSuiteName(testSuite)
				.withEnvironment(environment)
				.withTestName(test)
				.withUrlName(null)
				.withCorrelationId(correlationId).build();

		Comment comment = manager.getComment(nodeMetadata, Comment.Level.TEST);
		if(comment != null){
			result = manager.deleteComment(comment);
		} else {
			result = new OperationStatus(false, AetRestEndpointHelper.NOT_FOUND_MSG);
		}
		return result;
	}

	@Override
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{correlationId}")
	public OperationStatus deleteUrlComment(@PathParam("company") String company,
								 @PathParam("project") String project,
								 @PathParam("testSuite") String testSuite,
								 @PathParam("environment") String environment,
								 @PathParam("test") String test,
								 @PathParam("urlName") String urlName,
								 @PathParam("correlationId") String correlationId) throws AetRestEndpointException {
		OperationStatus result;

		NodeMetadata nodeMetadata = UrlNodeMetadataBuilder.getUrlNodeMetadata()
				.withCompany(company)
				.withProject(project)
				.withTestSuiteName(testSuite)
				.withEnvironment(environment)
				.withTestName(test)
				.withUrlName(urlName)
				.withCorrelationId(correlationId).build();

		Comment comment = manager.getComment(nodeMetadata, Comment.Level.URL);
		if(comment != null){
			result = manager.deleteComment(comment);
		} else {
			result = new OperationStatus(false, AetRestEndpointHelper.NOT_FOUND_MSG);
		}

		return result;
	}

	@Override
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}/{correlationId}")
	public OperationStatus deleteTestCaseComment(@PathParam("company") String company,
									  @PathParam("project") String project,
									  @PathParam("testSuite") String testSuite,
									  @PathParam("environment") String environment,
									  @PathParam("test") String test,
									  @PathParam("urlName") String urlName,
									  @PathParam("collectorType") String collectorType,
									  @PathParam("collectorName") String collectorName,
									  @PathParam("comparatorType") String comparatorType,
									  @PathParam("comparatorName") String comparatorName,
									  @PathParam("correlationId") String correlationId) throws AetRestEndpointException {
		OperationStatus result;

		CompareMetadata nodeMetadata = new CompareMetadata(company, project, testSuite, environment,
				correlationId, test, null, urlName, collectorType, collectorName, comparatorType,
				comparatorName);

		Comment comment = manager.getComment(nodeMetadata, Comment.Level.TESTCASE);
		if(comment != null){
			result = manager.deleteComment(comment);
		} else {
			result = new OperationStatus(false, AetRestEndpointHelper.NOT_FOUND_MSG);
		}

		return result;
	}

}
/* @formatter:on */
