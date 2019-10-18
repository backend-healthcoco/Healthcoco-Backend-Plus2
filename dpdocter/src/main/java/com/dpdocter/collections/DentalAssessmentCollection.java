package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.TeethExamination;
import com.dpdocter.response.ImageURLResponse;

@Document(collection = "dental_assessment_cl")
public class DentalAssessmentCollection extends GenericCollection {

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
	private List<String> chiefComplaints;
	@Field
	private Boolean oralHygiene;
	@Field
	private List<String> habits;
	@Field
	private TeethExamination teethExamination;
	@Field
	private String gingivaStains;
	@Field
	private String gingivaCalculus;
	@Field
	private List<String> suggestedInvestigation;
	@Field
	private Boolean doctorConsultations = Boolean.FALSE;
	@Field
	private String remarks;
	@Field
	private List<ImageURLResponse> images;
	@Field
	private Boolean discarded = Boolean.FALSE;

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

	public List<String> getChiefComplaints() {
		return chiefComplaints;
	}

	public void setChiefComplaints(List<String> chiefComplaints) {
		this.chiefComplaints = chiefComplaints;
	}

	public Boolean getOralHygiene() {
		return oralHygiene;
	}

	public void setOralHygiene(Boolean oralHygiene) {
		this.oralHygiene = oralHygiene;
	}

	public List<String> getHabits() {
		return habits;
	}

	public void setHabits(List<String> habits) {
		this.habits = habits;
	}

	public TeethExamination getTeethExamination() {
		return teethExamination;
	}

	public void setTeethExamination(TeethExamination teethExamination) {
		this.teethExamination = teethExamination;
	}

	public String getGingivaStains() {
		return gingivaStains;
	}

	public void setGingivaStains(String gingivaStains) {
		this.gingivaStains = gingivaStains;
	}

	public String getGingivaCalculus() {
		return gingivaCalculus;
	}

	public void setGingivaCalculus(String gingivaCalculus) {
		this.gingivaCalculus = gingivaCalculus;
	}

	public List<String> getSuggestedInvestigation() {
		return suggestedInvestigation;
	}

	public void setSuggestedInvestigation(List<String> suggestedInvestigation) {
		this.suggestedInvestigation = suggestedInvestigation;
	}

	public Boolean getDoctorConsultations() {
		return doctorConsultations;
	}

	public void setDoctorConsultations(Boolean doctorConsultations) {
		this.doctorConsultations = doctorConsultations;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

}
