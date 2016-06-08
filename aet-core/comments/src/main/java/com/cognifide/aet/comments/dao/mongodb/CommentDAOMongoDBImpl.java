/*
 * Cognifide AET :: Comments
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
package com.cognifide.aet.comments.dao.mongodb;

import com.cognifide.aet.comments.api.Comment;
import com.cognifide.aet.comments.api.dao.CommentDAO;
import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.node.CollectMetadata;
import com.cognifide.aet.communication.api.node.CompareMetadata;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.communication.api.node.builders.UrlNodeMetadataBuilder;
import com.cognifide.aet.vs.mongodb.MongoDBClient;
import com.cognifide.aet.vs.visitors.MetadataArtifactQueryParametersVisitor;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Component(label = "AET Comments DAO implementation for MongoDB", immediate = true)
public class CommentDAOMongoDBImpl implements CommentDAO {

	private static final String METADATA_COLLECTION_SUFIX = ".metadata";

	private static final Logger LOGGER = LoggerFactory.getLogger(CommentDAOMongoDBImpl.class);

	private static final String NODEKEY_PREFIX = "nodekey.";

	private static final String NODEKEY = "nodekey";

	private static final String CONTENT_ATTRIBUTE_NAME = "content";

	private static final String TYPE_ATTRIBUTE_NAME = "type";

	private static final String TYPE_ATTRIBUTE_VALUE = "comment";

	private static final String LEVEL_ATTRIBUTE_NAME = "level";

	private static final String SET_ATTRIBUTE_NAME = "$set";

	private static final String COMMENTS_NOT_DELETED_MSG = "Comments were not deleted";

	private static final String COMMENT_NOT_DELETED_MSG = "Comment was not deleted";

	private static final String COMMENTS_DELETED_MSG = "Comments were deleted";

	private static final String COMMENT_DELETED_MSG = "Comment was deleted";

	private static final String COMMENT_NOT_CREATED_OR_UPDATED_MSG = "Comment was not created/updated";

	private static final String COMMENT_UPDATED_MSG = "Comment updated";

	private static final String COMMENT_CREATED_MSG = "Comment created";

	private static final int MAX_DB_NAME_LENGTH = 64;

	@Reference
	private MongoDBClient client;

	@Override
	public Collection<Comment> getAllComments(NodeMetadata metadata) {
		List<Comment> comments = new ArrayList<>();
		String collectionName = metadata.getProject() + METADATA_COLLECTION_SUFIX;
		final String dbName = getDBName(metadata.getCompany(), metadata.getProject());
		if (client.hasCollection(dbName, collectionName)) {
			DBCollection collection = client.getDB(dbName).getCollection(collectionName);

			DBCursor cursor = null;
			try {
				cursor = collection.find(getCommentQueryBuilder().and(getMetadataParameters(metadata)).get());
				while (cursor.hasNext()) {
					comments.add(getCommentFromDBObject(cursor.next()));
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		}
		return comments;
	}

	@Override
	public Comment getComment(NodeMetadata metadata, Comment.Level level) {
		String collectionName = metadata.getProject() + METADATA_COLLECTION_SUFIX;
		final String dbName = getDBName(metadata.getCompany(), metadata.getProject());
		if (client.hasCollection(dbName, collectionName)) {
			DBCollection collection = client.getDB(dbName).getCollection(collectionName);
			DBObject query = getCommentQueryBuilder().and(getMetadataParameters(metadata),
					new BasicDBObject(LEVEL_ATTRIBUTE_NAME, level.toString())).get();
			return getCommentFromDBObject(collection.findOne(query));
		}
		return null;
	}

	@Override
	public OperationStatus createOrUpdateComment(Comment comment) {
		OperationStatus result = null;
			try {
				String collectionName = comment.getMetadata().getProject() + METADATA_COLLECTION_SUFIX;
				final String dbName = getDBName(comment.getMetadata().getCompany(), comment.getMetadata().getProject());
				DBCollection collection = client.getDB(dbName, true).getCollection(collectionName);
				DBObject query = getCommentQueryBuilder().and(getMetadataParameters(comment.getMetadata()),
						new BasicDBObject(LEVEL_ATTRIBUTE_NAME, comment.getLevel().toString())).get();
				DBObject updatedContent = new BasicDBObject(SET_ATTRIBUTE_NAME, new BasicDBObject(
						CONTENT_ATTRIBUTE_NAME, comment.getContent()));
				WriteResult writeResult = collection.update(query, updatedContent, true, false);
				if (writeResult.getN() == 0) {
					result = new OperationStatus(false, COMMENT_NOT_CREATED_OR_UPDATED_MSG);
				} else if(writeResult.isUpdateOfExisting()) {
					result = new OperationStatus(true, COMMENT_UPDATED_MSG);
				} else {
					result = new OperationStatus(true, COMMENT_CREATED_MSG);
				}
			} catch (MongoException e) {
				LOGGER.error("Error while creating/updating comment: ", e);
				result = new OperationStatus(false, COMMENT_NOT_CREATED_OR_UPDATED_MSG);
			}
		return result;
	}

	@Override
	public OperationStatus deleteComment(Comment comment) {
		OperationStatus result;
		final String collectionName = comment.getMetadata().getProject() + METADATA_COLLECTION_SUFIX;
		final String dbName = getDBName(comment.getMetadata().getCompany(), comment.getMetadata().getProject());
		try {
			if (client.hasCollection(dbName, collectionName)) {
				DBCollection collection = client.getDB(dbName).getCollection(collectionName);
				DBObject query = getCommentQueryBuilder().and(getMetadataParameters(comment.getMetadata()),
						new BasicDBObject(LEVEL_ATTRIBUTE_NAME, comment.getLevel().toString())).get();
				collection.findAndRemove(query);
			}
			result = new OperationStatus(true, COMMENT_DELETED_MSG);
		} catch (MongoException e) {
			LOGGER.error("Error while deleting comment: ", e);
			result = new OperationStatus(false, COMMENT_NOT_DELETED_MSG);
		}
		return result;
	}

	@Override
	public OperationStatus deleteComments(NodeMetadata metadata) {
		OperationStatus result;
		final String collectionName = metadata.getProject() + METADATA_COLLECTION_SUFIX;
		final String dbName = getDBName(metadata.getCompany(), metadata.getProject());
		try {
			if (client.hasCollection(dbName, collectionName)) {
				DBCollection collection = client.getDB(dbName).getCollection(collectionName);
				DBObject query = getCommentQueryBuilder().and(getMetadataParameters(metadata)).get();
				collection.findAndRemove(query);
			}
			result = new OperationStatus(true, COMMENTS_DELETED_MSG);
		} catch (MongoException e) {
			LOGGER.error("Error while deleting comments: ", e);
			result = new OperationStatus(false, COMMENTS_NOT_DELETED_MSG);
		}
		return result;
	}

	private QueryBuilder getCommentQueryBuilder() {
		return QueryBuilder.start().put(TYPE_ATTRIBUTE_NAME).is(TYPE_ATTRIBUTE_VALUE);
	}

	private Comment getCommentFromDBObject(DBObject commentObject) {
		if (commentObject != null && commentObject.get(CONTENT_ATTRIBUTE_NAME) != null) {
			UrlNodeMetadata commentMetadata = null;
			Map<String, String> nodekey = (Map<String, String>) commentObject.get(NODEKEY);
			String content = commentObject.get(CONTENT_ATTRIBUTE_NAME).toString();
			Comment.Level level = Comment.Level.valueOf(commentObject.get(LEVEL_ATTRIBUTE_NAME).toString());
			UrlNodeMetadataBuilder urlNodeMetadataBuilder = UrlNodeMetadataBuilder.getUrlNodeMetadata()
					.withCompany(nodekey.get(UrlNodeMetadata.COMPANY_ATTRIBUTE_NAME))
					.withProject(nodekey.get(UrlNodeMetadata.PROJECT_ATTRIBUTE_NAME))
					.withTestSuiteName(nodekey.get(UrlNodeMetadata.TEST_SUITE_NAME_ATTRIBUTE_NAME))
					.withEnvironment(nodekey.get(UrlNodeMetadata.ENVIRONMENT_ATTRIBUTE_NAME))
					.withTestName(nodekey.get(UrlNodeMetadata.TEST_NAME_ATTRIBUTE_NAME))
					.withCorrelationId(nodekey.get(UrlNodeMetadata.CORRELATION_ID_ATTRIBUTE_NAME));
			switch (level) {
				case TEST:
					urlNodeMetadataBuilder.withUrlName(null);
					commentMetadata = urlNodeMetadataBuilder.build();
					break;
				case URL:
					urlNodeMetadataBuilder.withUrlName(nodekey.get(UrlNodeMetadata.URL_NAME_ATTRIBUTE_NAME));
					commentMetadata = urlNodeMetadataBuilder.build();
					break;
				case TESTCASE:
					commentMetadata = getCommentMetadataForTestcase(urlNodeMetadataBuilder, nodekey);
					break;
				default:
					throw new IllegalStateException("Illegal enum value.");
			}
			return new Comment(commentMetadata, content, level);
		}
		return null;
	}

	private DBObject getMetadataParameters(NodeMetadata nodeMetadata) {
		BasicDBObjectBuilder basicDBObjectBuilder = new BasicDBObjectBuilder();
		MetadataArtifactQueryParametersVisitor visitor = new MetadataArtifactQueryParametersVisitor();
		try {
			nodeMetadata.accept(visitor);
		} catch (AETException e) {
			LOGGER.error("Error while building query criteria: ", e);
		}

		Map<String, Object> queryParameters = visitor.getQueryParameters();
		for (Map.Entry<String, Object> entry : queryParameters.entrySet()) {
			basicDBObjectBuilder.add(NODEKEY_PREFIX + entry.getKey(), entry.getValue());
		}

		return basicDBObjectBuilder.get();
	}

	private CompareMetadata getCommentMetadataForTestcase(UrlNodeMetadataBuilder urlNodeMetadataBuilder,
			Map<String, String> nodekey) {
		urlNodeMetadataBuilder.withUrlName(nodekey.get(UrlNodeMetadata.URL_NAME_ATTRIBUTE_NAME));
		CollectMetadata collectMetadata = CollectMetadata.fromUrlNodeMetadata(
				urlNodeMetadataBuilder.build(),
				nodekey.get(CollectMetadata.COLLECTOR_ATTRIBUTE_TYPE),
				nodekey.get(CollectMetadata.COLLECTOR_ATTRIBUTE_NAME));
		return CompareMetadata.fromCollectMetadata(collectMetadata, nodekey.get(
				CompareMetadata.COMPARATOR_ATTRIBUTE_TYPE), nodekey.get(
				CompareMetadata.COMPARATOR_ATTRIBUTE_NAME));
	}

	/**
	 * Return dataBase name with applied db naming rules, based on project and company names
	 *
	 * @param companyName
	 * @param projectName
	 * @return
	 */
	private static String getDBName(String companyName, String projectName) {
		String result = companyName + "_" + projectName;
		return StringUtils.substring(result,0,MAX_DB_NAME_LENGTH);
	}
}