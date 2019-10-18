package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.ImageURLResponse;

public class DentalAssessment extends GenericCollection {

	private String id;
	private String academicProfileId;
	private String branchId;
	private String schoolId;
	private String doctorId;
	private List<String> chiefComplaints;
	private Boolean oralHygiene;
	private List<String> habits;
	private TeethExamination teethExamination;
	private String gingivaStains;
	private String gingivaCalculus;
	private List<String> suggestedInvestigation;
	private Boolean doctorConsultations = Boolean.FALSE;
	private String remarks;
	private List<ImageURLResponse> images;
	private Boolean discarded = Boolean.FALSE;

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

	@Override
	public String toString() {
		return "DentalAssessment [id=" + id + ", academicProfileId=" + academicProfileId + ", branchId=" + branchId
				+ ", schoolId=" + schoolId + ", doctorId=" + doctorId + ", chiefComplaints=" + chiefComplaints
				+ ", oralHygiene=" + oralHygiene + ", habits=" + habits + ", teethExamination=" + teethExamination
				+ ", gingivaStains=" + gingivaStains + ", gingivaCalculus=" + gingivaCalculus
				+ ", suggestedInvestigation=" + suggestedInvestigation + ", doctorConsultations=" + doctorConsultations
				+ ", remarks=" + remarks + ", images=" + images + ", discarded=" + discarded + "]";
	}

}
