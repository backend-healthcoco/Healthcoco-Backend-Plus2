package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.FitnessAssessment;

public interface FitnessAssessmentService {

	Boolean discardFitnessAssessment(String fitnessId, Boolean discarded);

	List<?> getFitnessAssessmentList(int size, int page, boolean discarded, String doctorId, String locationId,
			String hospitalId, String patientId, long updatedTime);

	FitnessAssessment getFitnessAssessmentById(String fitnessId);

	FitnessAssessment addEditFitnessAssessment(FitnessAssessment request);

	String getFitnessAssessmentFile(String fitnessId);

	Integer countFitnessAssessment(Boolean isDiscarded);

}
