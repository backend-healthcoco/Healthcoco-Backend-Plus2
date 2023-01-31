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
			String patientId, int page, int size,Boolean discarded);

	// for nurses
	InitialAdmissionResponse addEditAdmissionAssessmentForm(InitialAdmissionRequest request);

	List<InitialAdmissionResponse> getAdmissionAssessmentForms(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size,Boolean discarded);
	NursingCareExam addEditNursingCareExam(NursingCareExam request);
	
	Boolean deleteNursingCareExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	// pre operation
	PreOperationAssessmentResponse addEditPreOperationAssessmentForm(PreOperationAssessmentRequest request);

	List<PreOperationAssessmentResponse> getPreOperationAssessmentForms(String doctorId, String locationId,
			String hospitalId, String patientId, int page, int size,Boolean discarded );

	InitialAdmissionResponse getAdmissionFormById(String id);

	PreOperationAssessmentResponse getPreOprationFormById(String id);

	InitialAssessmentResponse getInitialAssessmentFormById(String id);

	Boolean deleteAdmissionAssessment(String nurseAdmissionFormId, String doctorId, String hospitalId,
			String locationId, Boolean discarded);

	Boolean deletePreOperationForm(String preOperationFormId, String doctorId, String hospitalId, String locationId,
			Boolean discarded);

	Boolean deleteInitialAssessment(String initialAssessmentId, String doctorId, String hospitalId, String locationId,
			Boolean discarded);

	String downloadInitialAssessmentFormById(String initialAssessmentId);

	String downloadPreOprationFormById(String preOperationFormId);

	String downloadNurseAdmissionFormById(String nurseAdmissionFormId);

	


}
