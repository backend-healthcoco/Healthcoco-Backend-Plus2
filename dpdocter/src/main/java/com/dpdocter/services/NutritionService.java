package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.BloodGlucose;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.NutritionRDA;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.SugarMedicineReminder;
import com.dpdocter.beans.SugarSetting;
import com.dpdocter.beans.Testimonial;
import com.dpdocter.beans.UserNutritionSubscription;
import com.dpdocter.elasticsearch.response.NutritionPlanWithCategoryShortResponse;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.request.NutritionPlanRequest;
import com.dpdocter.response.NutritionPlanResponse;
import com.dpdocter.response.NutritionPlanWithCategoryResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;

public interface NutritionService {

	public List<NutritionPlanType> getPlanType();

	public NutritionPlanResponse getNutritionPlan(String id);

	public List<NutritionPlan> getNutritionPlans(int page, int size, String type, long updatedTime, boolean discarded);

	public SubscriptionNutritionPlan getSubscritionPlan(String id);

	public List<SubscriptionNutritionPlan> getSubscritionPlans(int page, int size, String nutritionplanId,
			Boolean discarded);

	public List<UserNutritionSubscriptionResponse> getUserSubscritionPlans(int page, int size, long updatedTime,
			boolean discarded, String userId);

	public UserNutritionSubscriptionResponse getUserSubscritionPlan(String id);

	public UserNutritionSubscriptionResponse addEditUserSubscritionPlan(UserNutritionSubscription request);

	public UserNutritionSubscription deleteUserSubscritionPlan(String id);

	public List<NutritionPlanWithCategoryResponse> getNutritionPlanByCategory(NutritionPlanRequest request);

	public void updateUserSubscritionPlan();

	public List<SubscriptionNutritionPlan> getSubscritionPlans(List<ObjectId> idList);

	public List<NutritionPlan> getNutritionPlans(List<ObjectId> idList);

	public List<NutritionPlanWithCategoryShortResponse> getNutritionPlanDetailsByCategory(NutritionPlanRequest request);

	public NutritionPlan getNutritionPlanById(String id);

	List<Testimonial> getTestimonialsByPlanId(String planId, int size, int page);

	SugarSetting getSugarSettingById(String id);

	BloodGlucose getBloodGlucoseById(String id);

	List<BloodGlucose> getBloodGlucoseList(String patientId, int size, int page, Long from, Long to);

	List<SugarMedicineReminder> getSugarMedicineReminders(String patientId, int size, int page, Long from, Long to);

	SugarMedicineReminder getSugarMedicineReminderById(String id);

	SugarMedicineReminder addEditSugarMedicineReminder(SugarMedicineReminder request);

	BloodGlucose addEditBloodGlucose(BloodGlucose request);

	SugarSetting addEditSugarSetting(SugarSetting request);

	public NutritionRDA getRDAForPatient(String patientId, String doctorId, String locationId, String hospitalId);

}
