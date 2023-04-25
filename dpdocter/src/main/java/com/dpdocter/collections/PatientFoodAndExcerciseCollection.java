package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Excercise;
import com.dpdocter.beans.FoodCraving;
import com.dpdocter.beans.MealTimeAndPattern;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.enums.FoodPreferenceEnum;

@Document(collection = "patient_food_and_excercise_cl")
public class PatientFoodAndExcerciseCollection extends GenericCollection {
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
	private ObjectId assessmentId;
	@Field
	private List<FoodPreferenceEnum> foodPrefer;
	@Field
	private List<MealTimeAndPattern> mealTimeAndPattern;
	@Field
	private List<FoodCraving> foodCravings;
	@Field
	private List<Excercise> exercise;
	@Field
	private List<PrescriptionItem> drugs;
	@Field
	private Boolean isPatientDiscarded = false;

	public List<PrescriptionItem> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<PrescriptionItem> drugs) {
		this.drugs = drugs;
	}

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

	public List<FoodPreferenceEnum> getFoodPrefer() {
		return foodPrefer;
	}

	public void setFoodPrefer(List<FoodPreferenceEnum> foodPrefer) {
		this.foodPrefer = foodPrefer;
	}

	public List<MealTimeAndPattern> getMealTimeAndPattern() {
		return mealTimeAndPattern;
	}

	public void setMealTimeAndPattern(List<MealTimeAndPattern> mealTimeAndPattern) {
		this.mealTimeAndPattern = mealTimeAndPattern;
	}

	public List<FoodCraving> getFoodCravings() {
		return foodCravings;
	}

	public void setFoodCravings(List<FoodCraving> foodCravings) {
		this.foodCravings = foodCravings;
	}

	public List<Excercise> getExercise() {
		return exercise;
	}

	public void setExercise(List<Excercise> exercise) {
		this.exercise = exercise;
	}

	public ObjectId getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(ObjectId assessmentId) {
		this.assessmentId = assessmentId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "PatientFoodAndExcerciseCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", assessmentId=" + assessmentId
				+ ", foodPrefer=" + foodPrefer + ", mealTimeAndPattern=" + mealTimeAndPattern + ", foodCravings="
				+ foodCravings + ", exercise=" + exercise + ", drugs=" + drugs + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}

}
