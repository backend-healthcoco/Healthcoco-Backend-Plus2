package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ConsultationProblemDetails;
import com.dpdocter.request.ConsultationProblemDetailsRequest;

public interface ConsultationProblemDetailsService {
	
	ConsultationProblemDetails addEditProblemDetails(ConsultationProblemDetailsRequest request);
	
	List<ConsultationProblemDetails> getProblemDetails(int page,int size,String searchTerm,Boolean discarded);

	Integer countConsultationProblemDetails(Boolean discarded, String searchTerm);
}
