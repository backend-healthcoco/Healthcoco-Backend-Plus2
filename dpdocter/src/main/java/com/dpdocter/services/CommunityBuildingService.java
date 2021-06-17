package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Comment;
import com.dpdocter.beans.CommentRequest;
import com.dpdocter.beans.Feeds;
import com.dpdocter.beans.FeedsRequest;
import com.dpdocter.beans.FeedsResponse;
import com.dpdocter.beans.Forum;
import com.dpdocter.beans.ForumRequest;
import com.dpdocter.beans.ForumResponse;

import common.util.web.Response;

public interface CommunityBuildingService {

	ForumResponse addEditForumResponse(ForumRequest request);
	
	Response<Object> getForumResponse(int page, int size, String searchTerm, Boolean discarded);

	ForumResponse getForumResponseById(String id);

	Comment addEditComment(CommentRequest request);

	Comment deleteCommentsById(String id, String userId);

	ForumResponse deleteForumResponseById(String id, String userId);

	FeedsResponse getArticleById(String id, String languageId);

	FeedsResponse deleteFeedsById(String id, String doctorId);

	Response<Object> getLearningSession(int page, int size, Boolean discarded, String searchTerm, String languageId,
			String type);

	FeedsResponse addEditPost(FeedsRequest request);

	Integer getForumCount(String searchTerm, Boolean discarded);

	Integer getArticlesCount(Boolean discarded, String searchTerm, String languageId, String type);

	Response<Object> getComments(int size, int page, Boolean discarded, String searchTerm, String feedId, String doctorId);
}
