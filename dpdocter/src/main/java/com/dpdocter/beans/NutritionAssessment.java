package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.Drug;
import com.dpdocter.response.ImageURLResponse;
public class NutritionAssessment extends GenericCollection {

	private String id;
	private String academicProfileId;
	private String branchId;
	private String schoolId;
	private String doctorId;
	private String nutritionGoal;
	private String foodPreference;
	private List<MealTiming> mealTimings;
	private List<FoodPattern> foodPatterns;
	private Integer waterIntakePerDay;
	private String drinkingWaterType;
	private WorkingHours schoolHours;
	private WorkingHours sleepTime;
	private String sleepingHours;
	private String exerciseType;
	private String otherExerciseType;
	private List<String> addictionOfParents;
	private String foodDrugAllergy;
	private Boolean everHospitalized = Boolean.FALSE;
	private List<ImageURLResponse> images;
	private List<Drug> drugs;
	private Integer noOfFamilyMembers;
	private Integer oilConsumpationPerMonth;
	private String foodSource;
	private String cravingItems;
	private Long idealTimeForFeedback;
	private List<String> clinicalManifestation;
	private Long familyIncomePerMonth;
	private Boolean discarded = Boolean.FALSE;
	private String otherTests;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAcademicProfileId() {
		return academicProfileId;
	}

	public void setAcademicProfileId(String academicProfileId) {
		this.academicProfileId = academicProfileId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
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
