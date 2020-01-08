package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FoodPattern;
import com.dpdocter.beans.MealTiming;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.Drug;

@Document(collection = "nutrition_assessment_cl")
public class NutritionAssessmentCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId academicProfileId;
	@Field
	private ObjectId branchId;
	@Field
	private ObjectId schoolId;
	@Field
	private ObjectId doctorId;
	@Field
	private String nutritionGoal;
	@Field
	private String foodPreference;
	@Field
	private List<MealTiming> mealTimings;
	@Field
	private List<FoodPattern> foodPatterns;
	@Field
	private Integer waterIntakePerDay;
	@Field
	private String drinkingWaterType;
	@Field
	private WorkingHours schoolHours;
	@Field
	private WorkingHours sleepTime;
	@Field
	private String sleepingHours;
	@Field
	private String exerciseType;
	@Field
	private String otherExerciseType;
	@Field
	private List<String> addictionOfParents;
	@Field
	private String foodDrugAllergy;
	@Field
	private Boolean everHospitalized = Boolean.FALSE;
	@Field
	private List<ImageURLResponse> images;
	@Field
	private List<Drug> drugs;
	@Field
	private Integer noOfFamilyMembers;
	@Field
	private Integer oilConsumpationPerMonth;
	@Field
	private String foodSource;
	@Field
	private String cravingItems;
	@Field
	private Long idealTimeForFeedback;
	@Field
	private List<String> clinicalManifestation;
	@Field
	private Long familyIncomePerMonth;
	@Field
	private Boolean discarded = Boolean.FALSE;
	@Field
	private String otherTests;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getAcademicProfileId() {
		return academicProfileId;
	}

	public void setAcademicProfileId(ObjectId academicProfileId) {
		this.academicProfileId = academicProfileId;
	}

	public ObjectId getBranchId() {
		return branchId;
	}

	public void setBranchId(ObjectId branchId) {
		this.branchId = branchId;
	}

	public ObjectId getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(ObjectId schoolId) {
		this.schoolId = schoolId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public String getNutritionGoal() {
		return nutritionGoal;
	}

	public void setNutritionGoal(String nutritionGoal) {
		this.nutritionGoal = nutritionGoal;
	}

	public String getFoodPreference() {
		return foodPreference;
	}

	public void setFoodPreference(String foodPreference) {
		this.foodPreference = foodPreference;
	}

	public List<MealTiming> getMealTimings() {
		return mealTimings;
	}

	public void setMealTimings(List<MealTiming> mealTimings) {
		this.mealTimings = mealTimings;
	}

	public List<FoodPattern> getFoodPatterns() {
		return foodPatterns;
	}

	public void setFoodPatterns(List<FoodPattern> foodPatterns) {
		this.foodPatterns = foodPatterns;
	}

	public Integer getWaterIntakePerDay() {
		return waterIntakePerDay;
	}

	public void setWaterIntakePerDay(Integer waterIntakePerDay) {
		this.waterIntakePerDay = waterIntakePerDay;
	}

	public String getDrinkingWaterType() {
		return drinkingWaterType;
	}

	public void setDrinkingWaterType(String drinkingWaterType) {
		this.drinkingWaterType = drinkingWaterType;
	}

	public WorkingHours getSchoolHours() {
		return schoolHours;
	}

	public void setSchoolHours(WorkingHours schoolHours) {
		this.schoolHours = schoolHours;
	}

	public WorkingHours getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(WorkingHours sleepTime) {
		this.sleepTime = sleepTime;
	}

	public String getSleepingHours() {
		return sleepingHours;
	}

	public void setSleepingHours(String sleepingHours) {
		this.sleepingHours = sleepingHours;
	}

	public String getExerciseType() {
		return exerciseType;
	}

	public void setExerciseType(String exerciseType) {
		this.exerciseType = exerciseType;
	}

	public List<String> getAddictionOfParents() {
		return addictionOfParents;
	}

	public void setAddictionOfParents(List<String> addictionOfParents) {
		this.addictionOfParents = addictionOfParents;
	}

	public String getFoodDrugAllergy() {
		return foodDrugAllergy;
	}

	public void setFoodDrugAllergy(String foodDrugAllergy) {
		this.foodDrugAllergy = foodDrugAllergy;
	}

	public Boolean getEverHospitalized() {
		return everHospitalized;
	}

	public void setEverHospitalized(Boolean everHospitalized) {
		this.everHospitalized = everHospitalized;
	}

	public List<ImageURLResponse> getImages() {
		return images;
	}

	public void setImages(List<ImageURLResponse> images) {
		this.images = images;
	}

	public List<Drug> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}

	public Integer getNoOfFamilyMembers() {
		return noOfFamilyMembers;
	}

	public void setNoOfFamilyMembers(Integer noOfFamilyMembers) {
		this.noOfFamilyMembers = noOfFamilyMembers;
	}

	public Integer getOilConsumpationPerMonth() {
		return oilConsumpationPerMonth;
	}

	public void setOilConsumpationPerMonth(Integer oilConsumpationPerMonth) {
		this.oilConsumpationPerMonth = oilConsumpationPerMonth;
	}

	public String getFoodSource() {
		return foodSource;
	}

	public void setFoodSource(String foodSource) {
		this.foodSource = foodSource;
	}

	public String getCravingItems() {
		return cravingItems;
	}

	public void setCravingItems(String cravingItems) {
		this.cravingItems = cravingItems;
	}

	public Long getIdealTimeForFeedback() {
		return idealTimeForFeedback;
	}

	public void setIdealTimeForFeedback(Long idealTimeForFeedback) {
		this.idealTimeForFeedback = idealTimeForFeedback;
	}

	public List<String> getClinicalManifestation() {
		return clinicalManifestation;
	}

	public void setClinicalManifestation(List<String> clinicalManifestation) {
		this.clinicalManifestation = clinicalManifestation;
	}

	public Long getFamilyIncomePerMonth() {
		return familyIncomePerMonth;
	}

	public void setFamilyIncomePerMonth(Long familyIncomePerMonth) {
		this.familyIncomePerMonth = familyIncomePerMonth;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getOtherExerciseType() {
		return otherExerciseType;
	}

	public void setOtherExerciseType(String otherExerciseType) {
		this.otherExerciseType = otherExerciseType;
	}

	public String getOtherTests() {
		return otherTests;
	}

	public void setOtherTests(String otherTests) {
		this.otherTests = otherTests;
	}

}
