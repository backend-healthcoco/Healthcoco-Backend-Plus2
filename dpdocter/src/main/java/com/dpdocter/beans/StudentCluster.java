package com.dpdocter.beans;

import java.util.List;

public class StudentCluster {

	private String id;

	private String branchName;

	private String acadamicClassName;

	private String sectionName;

	private String studentName;

	private String gender;

	private DOB dob;

	private Integer height;

	private Double weight;

	private List<FoodCommunity> communities;

	private String foodPreference;

	private String lifestyle;

	private List<NutritionDisease> diseases;

	private List<String> generalSigns;

	private List<String> deficienciesSuspected;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getAcadamicClassName() {
		return acadamicClassName;
	}

	public void setAcadamicClassName(String acadamicClassName) {
		this.acadamicClassName = acadamicClassName;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public List<FoodCommunity> getCommunities() {
		return communities;
	}

	public void setCommunities(List<FoodCommunity> communities) {
		this.communities = communities;
	}

	public String getFoodPreference() {
		return foodPreference;
	}

	public void setFoodPreference(String foodPreference) {
		this.foodPreference = foodPreference;
	}

	public String getLifestyle() {
		return lifestyle;
	}

	public void setLifestyle(String lifestyle) {
		this.lifestyle = lifestyle;
	}

	public List<NutritionDisease> getDiseases() {
		return diseases;
	}

	public void setDiseases(List<NutritionDisease> diseases) {
		this.diseases = diseases;
	}

	public List<String> getGeneralSigns() {
		return generalSigns;
	}

	public void setGeneralSigns(List<String> generalSigns) {
		this.generalSigns = generalSigns;
	}

	public List<String> getDeficienciesSuspected() {
		return deficienciesSuspected;
	}

	public void setDeficienciesSuspected(List<String> deficienciesSuspected) {
		this.deficienciesSuspected = deficienciesSuspected;
	}

	@Override
	public String toString() {
		return "StudentCluster [id=" + id + ", branchName=" + branchName + ", acadamicClassName=" + acadamicClassName
				+ ", sectionName=" + sectionName + ", studentName=" + studentName + ", gender=" + gender + ", dob="
				+ dob + ", height=" + height + ", weight=" + weight + ", communities=" + communities
				+ ", foodPreference=" + foodPreference + ", lifestyle=" + lifestyle + ", diseases=" + diseases
				+ ", generalSigns=" + generalSigns + ", deficienciesSuspected=" + deficienciesSuspected + "]";
	}
}
