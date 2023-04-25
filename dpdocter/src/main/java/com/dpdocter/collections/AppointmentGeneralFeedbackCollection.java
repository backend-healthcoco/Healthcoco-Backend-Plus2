package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "appointment_general_feedback_cl")
public class AppointmentGeneralFeedbackCollection extends GenericCollection {

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
	private Boolean doctorRecommendation;
	@Field
	private Boolean isAppointmentStartedOnTime;
	@Field
	private String howLateWasAppointment;
	@Field
	private Float overallExperience;
	@Field
	private String reasonOfVisit;
	@Field
	private String experienceWithDoctor;
	@Field
	private Integer noOfRecommendation;
	@Field
	private String doctorReply;
	@Field
	private Boolean isAnonymous;
	@Field
	private Boolean isApproved;
	@Field
	private String adminUpdatedExperienceWithDoctor;
	@Field
	private Boolean isPatientDiscarded = false;

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

	public Boolean getDoctorRecommendation() {
		return doctorRecommendation;
	}

	public void setDoctorRecommendation(Boolean doctorRecommendation) {
		this.doctorRecommendation = doctorRecommendation;
	}

	public Boolean getIsAppointmentStartedOnTime() {
		return isAppointmentStartedOnTime;
	}

	public void setIsAppointmentStartedOnTime(Boolean isAppointmentStartedOnTime) {
		this.isAppointmentStartedOnTime = isAppointmentStartedOnTime;
	}

	public String getHowLateWasAppointment() {
		return howLateWasAppointment;
	}

	public void setHowLateWasAppointment(String howLateWasAppointment) {
		this.howLateWasAppointment = howLateWasAppointment;
	}

	public Float getOverallExperience() {
		return overallExperience;
	}

	public void setOverallExperience(Float overallExperience) {
		this.overallExperience = overallExperience;
	}

	public String getReasonOfVisit() {
		return reasonOfVisit;
	}

	public void setReasonOfVisit(String reasonOfVisit) {
		this.reasonOfVisit = reasonOfVisit;
	}

	public String getExperienceWithDoctor() {
		return experienceWithDoctor;
	}

	public void setExperienceWithDoctor(String experienceWithDoctor) {
		this.experienceWithDoctor = experienceWithDoctor;
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

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public Boolean getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(Boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getAdminUpdatedExperienceWithDoctor() {
		return adminUpdatedExperienceWithDoctor;
	}

	public void setAdminUpdatedExperienceWithDoctor(String adminUpdatedExperienceWithDoctor) {
		this.adminUpdatedExperienceWithDoctor = adminUpdatedExperienceWithDoctor;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "AppointmentGeneralFeedbackCollection [id=" + id + ", locationId=" + locationId + ", doctorId="
				+ doctorId + ", patientId=" + patientId + ", hospitalId=" + hospitalId + ", patientName=" + patientName
				+ ", doctorRecommendation=" + doctorRecommendation + ", isAppointmentStartedOnTime="
				+ isAppointmentStartedOnTime + ", howLateWasAppointment=" + howLateWasAppointment
				+ ", overallExperience=" + overallExperience + ", reasonOfVisit=" + reasonOfVisit
				+ ", experienceWithDoctor=" + experienceWithDoctor + ", noOfRecommendation=" + noOfRecommendation
				+ ", doctorReply=" + doctorReply + ", isAnonymous=" + isAnonymous + ", isApproved=" + isApproved
				+ ", adminUpdatedExperienceWithDoctor=" + adminUpdatedExperienceWithDoctor + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}

}
