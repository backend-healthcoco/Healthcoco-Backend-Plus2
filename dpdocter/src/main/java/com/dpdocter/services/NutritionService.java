package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.NutritionGoalAnalytics;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.UserNutritionSubscription;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.request.AddEditNutritionReferenceRequest;
import com.dpdocter.response.NutritionPlanResponse;
import com.dpdocter.response.NutritionReferenceResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;

public interface NutritionService {

	NutritionReferenceResponse addEditNutritionReference(AddEditNutritionReferenceRequest request);

	List<NutritionReferenceResponse> getNutritionReferenceList(String doctorId, String locationId, String role,
			int page, int size);

	NutritionGoalAnalytics getGoalAnalytics(String doctorId, String locationId, String role, Long fromDate,
			Long toDate);

	public List<NutritionPlanType> getPlanType();

	public NutritionPlanResponse getNutritionPlan(String id);

	public List<NutritionPlan> getNutritionPlans(int page, int size, String type, long updatedTime, boolean discarded);

	public SubscriptionNutritionPlan getSubscritionPlan(String id);

	public List<SubscriptionNutritionPlan> getSubscritionPlans(int page, int size, String nutritionplanId,
			Boolean discarded);
	
	public List<UserNutritionSubscriptionResponse> getUserSubscritionPlans(int page, int size, long updatedTime, boolean discarded);

	public UserNutritionSubscriptionResponse getUserSubscritionPlan(String id);
	
	public UserNutritionSubscriptionResponse AddEditUserSubscritionPlan(UserNutritionSubscription request);
	
	public UserNutritionSubscription deleteUserSubscritionPlan(String id);
}
