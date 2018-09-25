package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DietPlan;

public interface DietPlansService {

	public DietPlan addEditDietPlan(DietPlan request);

	public List<DietPlan> getDietPlans(int page, int size, String doctorId, String hospitalId, String locationId,
			String updatedTime, boolean discarded);

	public DietPlan getDietPlanById(String planId);

	public DietPlan discardDietPlan(String planId, Boolean discarded);

}
