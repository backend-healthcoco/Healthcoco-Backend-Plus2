package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.GoalStatus;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.enums.RegularityStatus;
import com.dpdocter.response.ImageURLResponse;

@Document(collection = "nutrition_reference_cl")
public class NutritionReferenceCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private String details;
	@Field
	private Integer durationInMonths;
	@Field
	private NutritionPlanType type;
	@Field
	private ObjectId nutritionPlanId;
	@Field
	private ObjectId subscriptionPlanId;
	@Field
	private List<ImageURLResponse> reports;
	@Field
	private RegularityStatus regularityStatus = RegularityStatus.NO_ACTION;
	@Field
	private GoalStatus goalStatus = GoalStatus.REFERRED;

	@Field
	private Boolean isPatientDiscarded = false;
	@Field
	private String mobileNumber;
	@Field
	private String localPatientName;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Integer getDurationInMonths() {
		return durationInMonths;
	}

	public void setDurationInMonths(Integer durationInMonths) {
		this.durationInMonths = durationInMonths;
	}

	public List<ImageURLResponse> getReports() {
		return reports;
	}

	public void setReports(List<ImageURLResponse> reports) {
		this.reports = reports;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public NutritionPlanType getType() {
		return type;
	}

	public void setType(NutritionPlanType type) {
		this.type = type;
	}

	public ObjectId getNutritionPlanId() {
		return nutritionPlanId;
	}

	public void setNutritionPlanId(ObjectId nutritionPlanId) {
		this.nutritionPlanId = nutritionPlanId;
	}

	public ObjectId getSubscriptionPlanId() {
		return subscriptionPlanId;
	}

	public void setSubscriptionPlanId(ObjectId subscriptionPlanId) {
		this.subscriptionPlanId = subscriptionPlanId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public RegularityStatus getRegularityStatus() {
		return regularityStatus;
	}

	public void setRegularityStatus(RegularityStatus regularityStatus) {
		this.regularityStatus = regularityStatus;
	}

	public GoalStatus getGoalStatus() {
		return goalStatus;
	}

	public void setGoalStatus(GoalStatus goalStatus) {
		this.goalStatus = goalStatus;
	}

}
