package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.response.ImageURLResponse;

@Document(collection = "physical_assessment_cl")
public class PhysicalAssessmentCollection extends GenericCollection {

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
	private Boolean handAndNailHygiene;
	@Field
	private Boolean hairHygiene;
	@Field
	private List<String> generalSigns;
	@Field
	private List<String> head;
	@Field
	private List<String> lymphatic;
	@Field
	private List<String> skin;
	@Field
	private List<String> cardiovascular;
	@Field
	private List<String> respiratory;
	@Field
	private List<String> abdomen;
	@Field
	private List<String> nuerological;
	@Field
	private List<String> orthopedics;
	@Field
	private List<String> deficienciesSuspected;
	@Field
	private List<String> nervousSystem;
	@Field
	private String otherConditions;
	@Field
	private List<ImageURLResponse> images;
	@Field
	private List<ImageURLResponse> suggestedImages;
	@Field
	private Boolean discarded;
	@Field
	private Boolean doctorConsultation;
	@Field
	private Boolean fitnessProgram;
	@Field
	private Boolean nutritionConsultation;
	@Field
	private List<String> xRay;
	@Field
	private String ctMRIScanRegion;
	@Field
	private List<String> bloodTest;
	@Field
	private List<String> stoolTest;
	@Field
	private List<String> urineTest;
	@Field
	private String otherTests;
	@Field
	private String remarks;

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
