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
package com.cognifide.aet.comments.manager;

import com.cognifide.aet.comments.api.Comment;
import com.cognifide.aet.comments.api.dao.CommentDAO;
import com.cognifide.aet.comments.api.manager.CommentsManager;
import com.cognifide.aet.communication.api.OperationStatus;
import com.cognifide.aet.communication.api.exceptions.AETException;
import com.cognifide.aet.communication.api.node.NodeMetadata;
import com.cognifide.aet.communication.api.node.ReportMetadata;
import com.cognifide.aet.communication.api.node.UrlNodeMetadata;
import com.cognifide.aet.vs.VersionStorage;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@Service
@Component(label = "AET Comments Manager", description = "AET Comments Manager", immediate = true)
public class CommentsManagerImpl implements CommentsManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommentsManagerImpl.class);

	private static final String REPORT_NOT_EXISTS = "Corresponding report does not exist";

	@Reference
	private CommentDAO commentDAO;

	@Reference
	private VersionStorage storage;

	@Override
	public Collection<Comment> getAllComments(NodeMetadata metadata) {
		return commentDAO.getAllComments(metadata);
	}

	@Override
	public Comment getComment(NodeMetadata metadata, Comment.Level level) {
		return commentDAO.getComment(metadata, level);
	}

	public OperationStatus createOrUpdateComment(Comment comment) {
		OperationStatus result;
		if (correspondingReportExists(comment.getMetadata())) {
			result = commentDAO.createOrUpdateComment(comment);
		} else {
			LOGGER.warn(
					"Comment could not be created/updated because corresponding report does not exist. Metadata: {}",
					comment.getMetadata());
			result = new OperationStatus(false, REPORT_NOT_EXISTS);
		}
		return result;
	}

	@Override
	public OperationStatus deleteComment(Comment comment) {
		return commentDAO.deleteComment(comment);
	}

	@Override
	public OperationStatus deleteComments(NodeMetadata metadata) {
		return commentDAO.deleteComments(metadata);
	}

	@Override
	public void copyReportComments(NodeMetadata lastReportNodeMetadata, String newCorrelationId)
			throws AETException {
		if (lastReportNodeMetadata != null && newCorrelationId != null) {
			for (Comment comment : commentDAO.getAllComments(lastReportNodeMetadata)) {
				comment.getMetadata().setCorrelationId(newCorrelationId);
				OperationStatus status = commentDAO.createOrUpdateComment(comment);
				if (!status.isSuccess()) {
					throw new AETException("Coping Comments failed: " + status.getMessage());
				}
			}
		}
	}

	private boolean correspondingReportExists(UrlNodeMetadata metadata) {
		ReportMetadata reportMetadata = ReportMetadata.fromUrlNodeMetadata(metadata, null);
		return !storage.getReportsExecutions(reportMetadata).isEmpty();
	}

}