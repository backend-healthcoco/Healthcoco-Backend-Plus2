package com.dpdocter.request;

public class PharmacyFeedbackRequest {

	private String patientId;
	private String localeId;
	private String patientName;
	private Boolean pharmacyRecommendation = false;
	private Double overallExperience;
	private String experienceWithPharmacy;
	private Integer noOfRecommendation;
	private String pharmacyReply;
	private Boolean isAnonymous = false;
	private Boolean isDiscarded = false;

	public String getPatientId() {
		return patientId;
	}

	public String getLocaleId() {
		return localeId;
	}

	public String getPatientName() {
		return patientName;
	}

	public Boolean getPharmacyRecommendation() {
		return pharmacyRecommendation;
	}

	public Double getOverallExperience() {
		return overallExperience;
	}

	public String getExperienceWithPharmacy() {
		return experienceWithPharmacy;
	}

	public Integer getNoOfRecommendation() {
		return noOfRecommendation;
	}

	public String getPharmacyReply() {
		return pharmacyReply;
	}

	public Boolean getIsAnonymous() {
		return isAnonymous;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public void setPharmacyRecommendation(Boolean pharmacyRecommendation) {
		this.pharmacyRecommendation = pharmacyRecommendation;
	}

	public void setOverallExperience(Double overallExperience) {
		this.overallExperience = overallExperience;
	}

	public void setExperienceWithPharmacy(String experienceWithPharmacy) {
		this.experienceWithPharmacy = experienceWithPharmacy;
	}

	public void setNoOfRecommendation(Integer noOfRecommendation) {
		this.noOfRecommendation = noOfRecommendation;
	}

	public void setPharmacyReply(String pharmacyReply) {
		this.pharmacyReply = pharmacyReply;
	}

	public void setIsAnonymous(Boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

}
