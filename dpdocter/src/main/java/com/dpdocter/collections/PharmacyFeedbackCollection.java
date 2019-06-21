package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "pharmacy_feedback_cl")
public class PharmacyFeedbackCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId patientId;
	@Field
	private ObjectId localeId;
	@Field
	private String patientName;
	@Field
	private Boolean pharmacyRecommendation;
	@Field
	private Double overallExperience = 0.0;
	@Field
	private String experienceWithPharmacy;
	@Field
	private Integer noOfRecommendation;
	@Field
	private String pharmacyReply;
	@Field
	private Boolean isAnonymous;
	@Field
	private Boolean isApproved = false;
	@Field
	private String adminUpdatedExperienceWithPharmacy;
	@Field
	private Boolean isDiscarded = false;

	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
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

	public ObjectId getLocaleId() {
		return localeId;
	}

	public void setLocaleId(ObjectId localeId) {
		this.localeId = localeId;
	}

	public String getPharmacyReply() {
		return pharmacyReply;
	}

	public void setPharmacyReply(String pharmacyReply) {
		this.pharmacyReply = pharmacyReply;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "PharmacyFeedbackCollection [id=" + id + ", patientId=" + patientId + ", localeId=" + localeId
				+ ", patientName=" + patientName + ", pharmacyRecommendation=" + pharmacyRecommendation
				+ ", overallExperience=" + overallExperience + ", experienceWithPharmacy=" + experienceWithPharmacy
				+ ", noOfRecommendation=" + noOfRecommendation + ", pharmacyReply=" + pharmacyReply + ", isAnonymous="
				+ isAnonymous + ", isApproved=" + isApproved + ", adminUpdatedExperienceWithPharmacy="
				+ adminUpdatedExperienceWithPharmacy + ", isDiscarded=" + isDiscarded + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}
}
