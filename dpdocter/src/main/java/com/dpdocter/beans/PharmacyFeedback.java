package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class PharmacyFeedback extends GenericCollection {

	/*
	 * Please enter your name.* Drop Down Select or enter name as text Would you
	 * recommend the pharmacy to your family or friends ?* Yes/No Your overall
	 * experience at the pharmacy?* 5 Star can be in points look at zomato
	 * Describe your experience at the pharmacy. Text from patient
	 */
	private String id;
	private String patientId;
	private String localeId;
	private String patientName;
	private Boolean pharmacyRecommendation;
	private Double overallExperience;
	private String experienceWithPharmacy;
	private Integer noOfRecommendation;
	private String pharmacyReply;
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

	public Double getOverallExperience() {
		return overallExperience;
	}

	public void setOverallExperience(Double overallExperience) {
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

	public String getPharmacyReply() {
		return pharmacyReply;
	}

	public void setPharmacyReply(String pharmacyReply) {
		this.pharmacyReply = pharmacyReply;
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

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	@Override
	public String toString() {
		return "PharmacyFeedback [id=" + id + ", patientId=" + patientId + ", localeId=" + localeId + ", patientName="
				+ patientName + ", pharmacyRecommendation=" + pharmacyRecommendation + ", overallExperience="
				+ overallExperience + ", experienceWithPharmacy=" + experienceWithPharmacy + ", noOfRecommendation="
				+ noOfRecommendation + ", pharmacyReply=" + pharmacyReply + ", isAnonymous=" + isAnonymous
				+ ", isApproved=" + isApproved + ", adminUpdatedExperienceWithPharmacy="
				+ adminUpdatedExperienceWithPharmacy + ", isDiscarded=" + isDiscarded + "]";
	}

}
