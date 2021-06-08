package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Comment;
import com.dpdocter.beans.CommentRequest;
import com.dpdocter.beans.ForumRequest;
import com.dpdocter.beans.ForumResponse;

public interface CommunityBuildingService {

	ForumResponse addEditForumResponse(ForumRequest request);
	
	List<ForumResponse> getForumResponse(int page, int size, String searchTerm, Boolean discarded);

	ForumResponse getForumResponseById(String id);

	Comment addEditComment(CommentRequest request);
}
