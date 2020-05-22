package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.FitnessAssessment;

public interface FitnessAssessmentService {

	FitnessAssessment discardFitnessAssessment(String fitnessId, Boolean discarded);

	List<?> getFitnessAssessmentList(int size, int page, boolean discarded, String searchTerm, String doctorId,
			String locationId, String hospitalId, String patientId,long updatedTime);

	FitnessAssessment getFitnessAssessmentById(String fitnessId);

	FitnessAssessment addEditFitnessAssessment(FitnessAssessment request);

	String getFitnessAssessmentFile(String fitnessId);

	
}
