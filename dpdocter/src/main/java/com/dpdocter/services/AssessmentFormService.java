package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AssessmentPersonalDetail;
import com.dpdocter.beans.PatientAssesentmentHistoryRequest;
import com.dpdocter.beans.PatientFoodAndExcercise;
import com.dpdocter.beans.PatientLifeStyle;
import com.dpdocter.beans.PatientMeasurementInfo;
import com.dpdocter.response.AssessmentFormHistoryResponse;

public interface AssessmentFormService {
	public PatientMeasurementInfo addEditPatientMeasurementInfo(PatientMeasurementInfo request);

	public PatientLifeStyle addEditAssessmentLifeStyle(PatientLifeStyle request);

	public AssessmentFormHistoryResponse addEditAssessmentHistory(PatientAssesentmentHistoryRequest request);

	public PatientFoodAndExcercise addEditFoodAndExcercise(PatientFoodAndExcercise request);

	public AssessmentPersonalDetail addEditAssessmentPersonalDetail(AssessmentPersonalDetail request);

	public List<AssessmentPersonalDetail> getAssessmentPatientDetail(int page, int size, boolean discarded,
			long updateTime, String patientId, String doctorId, String locationId, String hospitalId);

	public PatientLifeStyle getAssessmentLifeStyle(String assessmentId);

	public AssessmentFormHistoryResponse getAssessmentHistory(String assessmentId);

	public PatientMeasurementInfo getPatientMeasurementInfo(String assessmentId);

	public PatientFoodAndExcercise getPatientFoodAndExcercise(String assessmentId);

	public Integer getAssessmentPatientDetailCount(int page, int size, boolean discarded, long updateTime,
			String patientId, String doctorId, String locationId, String hospitalId);

}
