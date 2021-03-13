package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.NursingCareExam;
import com.dpdocter.request.InitialAdmissionRequest;
import com.dpdocter.request.InitialAssessmentRequest;
import com.dpdocter.request.PreOperationAssessmentRequest;
import com.dpdocter.response.InitialAdmissionResponse;
import com.dpdocter.response.InitialAssessmentResponse;
import com.dpdocter.response.PreOperationAssessmentResponse;

public interface InitialAssessmentService {

	// for doctor
	InitialAssessmentResponse addEditInitialAssessmentForm(InitialAssessmentRequest request);

	List<InitialAssessmentResponse> getInitialAssessmentForm(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size);

	// for nurses
	InitialAdmissionResponse addEditAdmissionAssessmentForm(InitialAdmissionRequest request);

	List<InitialAdmissionResponse> getAdmissionAssessmentForms(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size);
	NursingCareExam addEditNursingCareExam(NursingCareExam request);
	
	Boolean deleteNursingCareExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	// pre operation
	PreOperationAssessmentResponse addEditPreOperationAssessmentForm(PreOperationAssessmentRequest request);

	List<PreOperationAssessmentResponse> getPreOperationAssessmentForms(String doctorId, String locationId,
			String hospitalId, String patientId, int page, int size);

	


}
