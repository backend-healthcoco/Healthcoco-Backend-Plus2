package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.GoalStatus;
import com.dpdocter.enums.RegularityStatus;
import com.dpdocter.response.ImageURLResponse;

public class NutritionReference extends GenericCollection {

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private String details;
	private Integer durationInMonths;
	private List<ImageURLResponse> reports;
	private RegularityStatus regularityStatus = RegularityStatus.NO_ACTION;
	private GoalStatus goalStatus = GoalStatus.REFERRED;
	private String localPatientName;
	private String mobileNumber;
	private NutritionPlan nutritionPlan;
	private SubscriptionNutritionPlan subscriptionPlan;

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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public NutritionPlan getNutritionPlan() {
		return nutritionPlan;
	}

	public void setNutritionPlan(NutritionPlan nutritionPlan) {
		this.nutritionPlan = nutritionPlan;
	}

	public SubscriptionNutritionPlan getSubscriptionPlan() {
		return subscriptionPlan;
	}

	public void setSubscriptionPlan(SubscriptionNutritionPlan subscriptionPlan) {
		this.subscriptionPlan = subscriptionPlan;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

}
