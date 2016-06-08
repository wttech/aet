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
import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.vs.rest.exceptions.AetRestEndpointException;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@WebService
@Path("/comments/")
@Produces(MediaType.APPLICATION_JSON)
interface CommentsRestEndpoint {
	/* @formatter:off */

	@GET
	@ApiOperation(value = "returns collection of comments for given test suite and correlation id", response = Collection.class)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{correlationId}")
	Collection<Comment> getAllComments(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "Correlation Id", required = true) @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException;

	@GET
	@ApiOperation(value = "returns comment for given test and correlationId", response = Comment.class)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{correlationId}")
	Comment getTestComment(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("test") String test,
			@ApiParam(value = "Correlation Id", required = true) @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException;

	@GET
	@ApiOperation(value = "returns comment for given url and correlationId", response = Comment.class)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{correlationId}")
	Comment getUrlComment(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam(
					"environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("test") String test,
			@ApiParam(value = "Url name", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Correlation Id", required = true) @PathParam(
					"correlationId") String correlationId)
			throws AetRestEndpointException;

	@GET
	@ApiOperation(value = "returns comment for given test case and correlationId", response = Comment.class)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}/{correlationId}")
	Comment getTestCaseComment(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("test") String test,
			@ApiParam(value = "Url name", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "Comparator type", required = true) @PathParam("comparatorType") String comparatorType,
			@ApiParam(value = "Comparator name", required = true) @PathParam("comparatorName") String comparatorName,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException;

	@POST
	@ApiOperation(value = "creates a comment on test case level or updates if comment already exists")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{correlationId}") OperationStatus createOrUpdateTestComment(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam(
					"environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("test") String test,
			@ApiParam(value = "CorrelationId", required = true) @PathParam(
					"correlationId") String correlationId,
			@ApiParam(value = "Content", required = true) @FormParam("content") String content)
			throws AetRestEndpointException;

	@POST
	@ApiOperation(value = "creates a comment on url level or updates if comment already exists")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{correlationId}")
	OperationStatus createOrUpdateUrlComment(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("test") String test,
			@ApiParam(value = "Url name", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId,
			@ApiParam(value = "Content", required = true) @FormParam("content") String content)
			throws AetRestEndpointException;
			

	@POST
	@ApiOperation(value = "creates a comment on test level or updates if comment already exists")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}/{correlationId}")
	OperationStatus createOrUpdateTestCaseComment(	@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("test") String test,
			@ApiParam(value = "Url name", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "Comparator type", required = true) @PathParam("comparatorType") String comparatorType,
			@ApiParam(value = "Comparator name", required = true) @PathParam("comparatorName") String comparatorName,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId,
			@ApiParam(value = "Content", required = true) @FormParam("content") String content) throws AetRestEndpointException;

	@DELETE
	@ApiOperation(value = "deletes all comments for given test suite and correlationId")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{correlationId}")
	OperationStatus deleteAllComments(	@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId) throws AetRestEndpointException;


	@DELETE
	@ApiOperation(value = "deletes a comment on test level")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{correlationId}")
	OperationStatus deleteTestComment(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("test") String test,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId)		
			throws AetRestEndpointException;

	@DELETE
	@ApiOperation(value = "deletes a comment on url level")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{correlationId}")
	OperationStatus deleteUrlComment(
			@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("test") String test,
			@ApiParam(value = "Url name", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId)
			throws AetRestEndpointException;
			

	@DELETE
	@ApiOperation(value = "deletes a comment on test case level")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{company}/{project}/{testSuite}/{environment}/{test}/{urlName}/{collectorType}/{collectorName}/{comparatorType}/{comparatorName}/{correlationId}")
	OperationStatus deleteTestCaseComment(	@ApiParam(value = "Company name", required = true) @PathParam("company") String company,
			@ApiParam(value = "Project name", required = true) @PathParam("project") String project,
			@ApiParam(value = "TestSuite name", required = true) @PathParam("testSuite") String testSuite,
			@ApiParam(value = "Environment name", required = true) @PathParam("environment") String environment,
			@ApiParam(value = "TestCase name", required = true) @PathParam("test") String test,
			@ApiParam(value = "Url name", required = true) @PathParam("urlName") String urlName,
			@ApiParam(value = "Collector type", required = true) @PathParam("collectorType") String collectorType,
			@ApiParam(value = "Collector name", required = true) @PathParam("collectorName") String collectorName,
			@ApiParam(value = "Comparator type", required = true) @PathParam("comparatorType") String comparatorType,
			@ApiParam(value = "Comparator name", required = true) @PathParam("comparatorName") String comparatorName,
			@ApiParam(value = "CorrelationId", required = true) @PathParam("correlationId") String correlationId) throws AetRestEndpointException;
		
	
}
/* @formatter:on */