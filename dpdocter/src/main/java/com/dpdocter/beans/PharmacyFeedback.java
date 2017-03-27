package com.dpdocter.beans;

import org.bson.types.ObjectId;

import com.dpdocter.collections.GenericCollection;

public class PharmacyFeedback extends GenericCollection {

	/*
	 * Please enter your name.* Drop Down Select or enter name as text Would you
	 * recommend the pharmacy to your family or friends ?* Yes/No Your overall
	 * experience at the pharmacy?* 5 Star can be in points look at zomato
	 * Describe your experience at the pharmacy. Text from patient
	 */

	private String id;
	private String locationId;
	private String doctorId;
	private String patientId;
	private ObjectId hospitalId;
	private String patientName;
	private Boolean pharmacyRecommendation;
	private Float overallExperience;
	private String experienceWithPharmacy;
	private Integer noOfRecommendation;
	private String doctorReply;
	private Boolean isAnonymous;
	private Boolean isApproved = false;
	private String adminUpdatedExperienceWithPharmacy;
	private Boolean isDiscarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public Boolean getPharmacyRecommendation() {
		return pharmacyRecommendation;
	}

	public void setPharmacyRecommendation(Boolean pharmacyRecommendation) {
		this.pharmacyRecommendation = pharmacyRecommendation;
	}

	public Float getOverallExperience() {
		return overallExperience;
	}

	public void setOverallExperience(Float overallExperience) {
		this.overallExperience = overallExperience;
	}

	public String getExperienceWithPharmacy() {
		return experienceWithPharmacy;
	}

	public void setExperienceWithPharmacy(String experienceWithPharmacy) {
		this.experienceWithPharmacy = experienceWithPharmacy;
	}

	public Integer getNoOfRecommendation() {
		return noOfRecommendation;
	}

	public void setNoOfRecommendation(Integer noOfRecommendation) {
		this.noOfRecommendation = noOfRecommendation;
	}

	public String getDoctorReply() {
		return doctorReply;
	}

	public void setDoctorReply(String doctorReply) {
		this.doctorReply = doctorReply;
	}

	public Boolean getIsAnonymous() {
		return isAnonymous;
	}

	public void setIsAnonymous(Boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getAdminUpdatedExperienceWithPharmacy() {
		return adminUpdatedExperienceWithPharmacy;
	}

	public void setAdminUpdatedExperienceWithPharmacy(String adminUpdatedExperienceWithPharmacy) {
		this.adminUpdatedExperienceWithPharmacy = adminUpdatedExperienceWithPharmacy;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public String toString() {
		return "PharmacyFeedback [id=" + id + ", locationId=" + locationId + ", doctorId=" + doctorId + ", patientId="
				+ patientId + ", hospitalId=" + hospitalId + ", patientName=" + patientName
				+ ", pharmacyRecommendation=" + pharmacyRecommendation + ", overallExperience=" + overallExperience
				+ ", experienceWithPharmacy=" + experienceWithPharmacy + ", noOfRecommendation=" + noOfRecommendation
				+ ", doctorReply=" + doctorReply + ", isAnonymous=" + isAnonymous + ", isApproved=" + isApproved
				+ ", adminUpdatedExperienceWithPharmacy=" + adminUpdatedExperienceWithPharmacy + ", isDiscarded="
				+ isDiscarded + "]";
	}

}
