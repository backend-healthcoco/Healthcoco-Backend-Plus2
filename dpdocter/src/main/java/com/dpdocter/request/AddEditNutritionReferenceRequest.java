package com.dpdocter.request;

import java.util.List;

import com.dpdocter.enums.GoalStatus;
import com.dpdocter.enums.NutritionPlanType;
import com.dpdocter.enums.RegularityStatus;
import com.dpdocter.response.ImageURLResponse;

public class AddEditNutritionReferenceRequest {

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private String details;
	private Integer durationInMonths;
	private NutritionPlanType type;
	private String nutritionPlanId;
	private String subscriptionPlanId;
	private List<ImageURLResponse> reports;
	private RegularityStatus regularityStatus = RegularityStatus.NO_ACTION;
	private GoalStatus goalStatus = GoalStatus.REFERRED;
	private String localPatientName;
	private String mobileNumber;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
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

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public NutritionPlanType getType() {
		return type;
	}

	public void setType(NutritionPlanType type) {
		this.type = type;
	}

	public String getNutritionPlanId() {
		return nutritionPlanId;
	}

	public void setNutritionPlanId(String nutritionPlanId) {
		this.nutritionPlanId = nutritionPlanId;
	}

	public String getSubscriptionPlanId() {
		return subscriptionPlanId;
	}

	public void setSubscriptionPlanId(String subscriptionPlanId) {
		this.subscriptionPlanId = subscriptionPlanId;
	}

}
