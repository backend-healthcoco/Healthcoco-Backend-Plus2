package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.FreeAnswerRequest;
import com.dpdocter.response.FreeAnswerResponse;
import com.dpdocter.response.FreeQuestionResponse;

public interface FreeQuestionAnswerService {

	FreeAnswerResponse addFreeAnswer(FreeAnswerRequest request);

	Integer countFreeQuestion(Boolean discarded, String doctorId);

	List<FreeQuestionResponse> getUnansweredQuestionList(int size, int page, String searchTerm, boolean discarded, String doctorId,
			long updatedTime);

	List<FreeQuestionResponse> getAnsweredQuestionList(int size, int page, String searchTerm, boolean discarded, String doctorId,
			long updatedTime);

	Boolean addQueView(String questionId);

}
