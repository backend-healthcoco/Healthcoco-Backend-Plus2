package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ConsultationProblemDetails;
import com.dpdocter.request.ConsultationProblemDetailsRequest;

public interface ConsultationProblemDetailsService {
	
	ConsultationProblemDetails addEditProblemDetails(ConsultationProblemDetailsRequest request);
	
	ConsultationProblemDetails getProblemDetails(String problemDetailsId);

	Integer countConsultationProblemDetails(Boolean discarded, String searchTerm);
}
