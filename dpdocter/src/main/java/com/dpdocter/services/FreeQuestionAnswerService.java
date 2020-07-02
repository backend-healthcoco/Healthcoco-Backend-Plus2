package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.FreeAnswerRequest;
import com.dpdocter.response.FreeAnswerResponse;
import com.dpdocter.response.FreeQuestionResponse;

public interface FreeQuestionAnswerService {

	FreeAnswerResponse addFreeAnswer(FreeAnswerRequest request);

	Integer countFreeQuestion(Boolean discarded);

	List<FreeQuestionResponse> getFreeQuestionList(int size, int page, String searchTerm, Boolean discarded, String doctorId,
			long updatedTime);

	List<FreeQuestionResponse> getAnsweredQuestionList(int size, int page, String searchTerm, Boolean discarded, String doctorId,
			long updatedTime);

}
