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
	private ObjectId locationId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId hospitalId;
	@Field
	private String patientName;
	@Field
	private Boolean pharmacyRecommendation;
	@Field
	private Float overallExperience;
	@Field
	private String experienceWithPharmacy;
	@Field
	private Integer noOfRecommendation;
	@Field
	private String doctorReply;
	@Field
	private Boolean isAnonymous;
	@Field
	private Boolean isApproved = false;
	@Field
	private String adminUpdatedExperienceWithPharmacy;
	@Field
	private Boolean isDiscarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
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
		return "PharmacyFeedbackCollection [id=" + id + ", locationId=" + locationId + ", doctorId=" + doctorId
				+ ", patientId=" + patientId + ", hospitalId=" + hospitalId + ", patientName=" + patientName
				+ ", pharmacyRecommendation=" + pharmacyRecommendation + ", overallExperience=" + overallExperience
				+ ", experienceWithPharmacy=" + experienceWithPharmacy + ", noOfRecommendation=" + noOfRecommendation
				+ ", doctorReply=" + doctorReply + ", isAnonymous=" + isAnonymous + ", isApproved=" + isApproved
				+ ", adminUpdatedExperienceWithPharmacy=" + adminUpdatedExperienceWithPharmacy + ", isDiscarded="
				+ isDiscarded + "]";
	}

}
