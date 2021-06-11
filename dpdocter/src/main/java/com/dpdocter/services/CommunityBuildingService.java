package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Comment;
import com.dpdocter.beans.CommentRequest;
import com.dpdocter.beans.FeedsRequest;
import com.dpdocter.beans.FeedsResponse;
import com.dpdocter.beans.ForumRequest;
import com.dpdocter.beans.ForumResponse;

public interface CommunityBuildingService {

	ForumResponse addEditForumResponse(ForumRequest request);
	
	List<ForumResponse> getForumResponse(int page, int size, String searchTerm, Boolean discarded);

	ForumResponse getForumResponseById(String id);

	Comment addEditComment(CommentRequest request);

	Comment deleteCommentsById(String id, String userId);

	ForumResponse deleteForumResponseById(String id, String userId);

	FeedsResponse getArticleById(String id, String languageId);

	FeedsResponse deleteFeedsById(String id, String doctorId);

	List<FeedsResponse> getLearningSession(int page, int size, Boolean discarded, String searchTerm, String languageId,
			String type);

	FeedsResponse addEditPost(FeedsRequest request);
}
