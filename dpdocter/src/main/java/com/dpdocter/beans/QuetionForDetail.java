package com.dpdocter.beans;

import com.dpdocter.enums.TreatmentType;

public class QuetionForDetail {
	private String name;
	private String relation;
	private String gender;
	private DOB dob;
	private String email;
	private String profession;
	private String weight;
	private String height;
	private Boolean isMedication = false;
	private String medication;
	private Boolean isDiagnosedCondition = false;
	private String diagnosedCondition;
	private Boolean isAllergy = false;
	private String allergy;
	private TreatmentType treatmentType;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public Boolean getIsMedication() {
		return isMedication;
	}

	public void setIsMedication(Boolean isMedication) {
		this.isMedication = isMedication;
	}

	public String getMedication() {
		return medication;
	}

	public void setMedication(String medication) {
		this.medication = medication;
	}

	public Boolean getIsDiagnosedCondition() {
		return isDiagnosedCondition;
	}

	public void setIsDiagnosedCondition(Boolean isDiagnosedCondition) {
		this.isDiagnosedCondition = isDiagnosedCondition;
	}

	public String getDiagnosedCondition() {
		return diagnosedCondition;
	}

	public void setDiagnosedCondition(String diagnosedCondition) {
		this.diagnosedCondition = diagnosedCondition;
	}

	public Boolean getIsAllergy() {
		return isAllergy;
	}

	public void setIsAllergy(Boolean isAllergy) {
		this.isAllergy = isAllergy;
	}

	public String getAllergy() {
		return allergy;
	}

	public void setAllergy(String allergy) {
		this.allergy = allergy;
	}

	public TreatmentType getTreatmentType() {
		return treatmentType;
	}

	public void setTreatmentType(TreatmentType treatmentType) {
		this.treatmentType = treatmentType;
	}

	@Override
	public String toString() {
		return "QuetionForDetail [name=" + name + ", relation=" + relation + ", gender=" + gender + ", dob=" + dob
				+ ", email=" + email + ", profession=" + profession + ", weight=" + weight + ", height=" + height
				+ ", isMedication=" + isMedication + ", medication=" + medication + ", isDiagnosedCondition="
				+ isDiagnosedCondition + ", diagnosedCondition=" + diagnosedCondition + ", isAllergy=" + isAllergy
				+ ", allergy=" + allergy + ", treatmentType=" + treatmentType + "]";
	}

	
}
