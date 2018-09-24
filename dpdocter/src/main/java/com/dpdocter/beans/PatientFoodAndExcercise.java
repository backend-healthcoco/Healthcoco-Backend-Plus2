package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.FoodPreferenceEnum;
import com.dpdocter.request.FoodCravingRequest;

public class PatientFoodAndExcercise {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String assessmentId;

	private List<FoodPreferenceEnum> foodPrefer;

	private List<MealTimeAndPatternRequest> mealTimeAndPattern;

	private List<FoodCravingRequest> foodCravings;

	private List<Excercise> exercise;

	public List<FoodPreferenceEnum> getFoodPrefer() {
		return foodPrefer;
	}

	public void setFoodPrefer(List<FoodPreferenceEnum> foodPrefer) {
		this.foodPrefer = foodPrefer;
	}

	public List<MealTimeAndPatternRequest> getMealTimeAndPattern() {
		return mealTimeAndPattern;
	}

	public void setMealTimeAndPattern(List<MealTimeAndPatternRequest> mealTimeAndPattern) {
		this.mealTimeAndPattern = mealTimeAndPattern;
	}

	public List<FoodCravingRequest> getFoodCravings() {
		return foodCravings;
	}

	public void setFoodCravings(List<FoodCravingRequest> foodCravings) {
		this.foodCravings = foodCravings;
	}

	public List<Excercise> getExercise() {
		return exercise;
	}

	public void setExercise(List<Excercise> exercise) {
		this.exercise = exercise;
	}

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

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

}
