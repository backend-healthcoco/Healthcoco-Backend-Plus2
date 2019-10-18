package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.ImageURLResponse;

public class PhysicalAssessment extends GenericCollection {

	private String id;
	private String academicProfileId;
	private String branchId;
	private String schoolId;
	private String doctorId;
	private Boolean handAndNailHygiene = Boolean.FALSE;
	private Boolean hairHygiene = Boolean.FALSE;
	private List<String> generalSigns;
	private List<String> head;
	private List<String> lymphatic;
	private List<String> skin;
	private List<String> cardiovascular;
	private List<String> respiratory;
	private List<String> abdomen;
	private List<String> nuerological;
	private List<String> orthopedics;
	private List<String> deficienciesSuspected;
	private List<String> nervousSystem;
	private String otherConditions;
	private List<ImageURLResponse> images;
	private List<ImageURLResponse> suggestedImages;
	private Boolean discarded = Boolean.FALSE;
	private Boolean doctorConsultation;
	private Boolean fitnessProgram;
	private Boolean nutritionConsultation;
	private List<String> xRay;
	private String ctMRIScanRegion;
	private List<String> bloodTest;
	private List<String> stoolTest;
	private List<String> urineTest;
	private String otherTests;
	private String remarks;

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

	public Boolean getHandAndNailHygiene() {
		return handAndNailHygiene;
	}

	public void setHandAndNailHygiene(Boolean handAndNailHygiene) {
		this.handAndNailHygiene = handAndNailHygiene;
	}

	public Boolean getHairHygiene() {
		return hairHygiene;
	}

	public void setHairHygiene(Boolean hairHygiene) {
		this.hairHygiene = hairHygiene;
	}

	public List<String> getGeneralSigns() {
		return generalSigns;
	}

	public void setGeneralSigns(List<String> generalSigns) {
		this.generalSigns = generalSigns;
	}

	public List<String> getHead() {
		return head;
	}

	public void setHead(List<String> head) {
		this.head = head;
	}

	public List<String> getLymphatic() {
		return lymphatic;
	}

	public void setLymphatic(List<String> lymphatic) {
		this.lymphatic = lymphatic;
	}

	public List<String> getSkin() {
		return skin;
	}

	public void setSkin(List<String> skin) {
		this.skin = skin;
	}

	public List<String> getCardiovascular() {
		return cardiovascular;
	}

	public void setCardiovascular(List<String> cardiovascular) {
		this.cardiovascular = cardiovascular;
	}

	public List<String> getRespiratory() {
		return respiratory;
	}

	public void setRespiratory(List<String> respiratory) {
		this.respiratory = respiratory;
	}

	public List<String> getAbdomen() {
		return abdomen;
	}

	public void setAbdomen(List<String> abdomen) {
		this.abdomen = abdomen;
	}

	public List<String> getNuerological() {
		return nuerological;
	}

	public void setNuerological(List<String> nuerological) {
		this.nuerological = nuerological;
	}

	public List<String> getOrthopedics() {
		return orthopedics;
	}

	public void setOrthopedics(List<String> orthopedics) {
		this.orthopedics = orthopedics;
	}

	public List<String> getDeficienciesSuspected() {
		return deficienciesSuspected;
	}

	public void setDeficienciesSuspected(List<String> deficienciesSuspected) {
		this.deficienciesSuspected = deficienciesSuspected;
	}

	public List<String> getNervousSystem() {
		return nervousSystem;
	}

	public void setNervousSystem(List<String> nervousSystem) {
		this.nervousSystem = nervousSystem;
	}

	public String getOtherConditions() {
		return otherConditions;
	}

	public void setOtherConditions(String otherConditions) {
		this.otherConditions = otherConditions;
	}

	public List<ImageURLResponse> getImages() {
		return images;
	}

	public void setImages(List<ImageURLResponse> images) {
		this.images = images;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getDoctorConsultation() {
		return doctorConsultation;
	}

	public void setDoctorConsultation(Boolean doctorConsultation) {
		this.doctorConsultation = doctorConsultation;
	}

	public Boolean getFitnessProgram() {
		return fitnessProgram;
	}

	public void setFitnessProgram(Boolean fitnessProgram) {
		this.fitnessProgram = fitnessProgram;
	}

	public Boolean getNutritionConsultation() {
		return nutritionConsultation;
	}

	public void setNutritionConsultation(Boolean nutritionConsultation) {
		this.nutritionConsultation = nutritionConsultation;
	}

	public List<String> getxRay() {
		return xRay;
	}

	public void setxRay(List<String> xRay) {
		this.xRay = xRay;
	}

	public String getCtMRIScanRegion() {
		return ctMRIScanRegion;
	}

	public void setCtMRIScanRegion(String ctMRIScanRegion) {
		this.ctMRIScanRegion = ctMRIScanRegion;
	}

	public List<String> getBloodTest() {
		return bloodTest;
	}

	public void setBloodTest(List<String> bloodTest) {
		this.bloodTest = bloodTest;
	}

	public List<String> getStoolTest() {
		return stoolTest;
	}

	public void setStoolTest(List<String> stoolTest) {
		this.stoolTest = stoolTest;
	}

	public List<String> getUrineTest() {
		return urineTest;
	}

	public void setUrineTest(List<String> urineTest) {
		this.urineTest = urineTest;
	}

	public String getOtherTests() {
		return otherTests;
	}

	public void setOtherTests(String otherTests) {
		this.otherTests = otherTests;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<ImageURLResponse> getSuggestedImages() {
		return suggestedImages;
	}

	public void setSuggestedImages(List<ImageURLResponse> suggestedImages) {
		this.suggestedImages = suggestedImages;
	}

}
