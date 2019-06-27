package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DietPlan;

public interface DietPlansService {

	public DietPlan addEditDietPlan(DietPlan request);

	public DietPlan getDietPlanById(String planId);

	public DietPlan discardDietPlan(String planId, Boolean discarded);

	List<DietPlan> getDietPlans(int page, int size, String patientId, String doctorId, String hospitalId,
			String locationId, long updatedTime, boolean discarded);

	public String downloadDietPlan(String planId);

	public Boolean emailDietPlan(String emailAddress, String planId);

	public List<DietPlan> getDietPlansForPatient(int page, int size, String patientId, long updatedTime, boolean discarded);

}
