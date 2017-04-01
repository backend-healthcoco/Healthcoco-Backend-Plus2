package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class AppointmentGeneralFeedback extends GenericCollection {

	private String id;
	private String locationId;
	private String locationName;
	private String doctorId;
	private String doctorName;
	private String patientId;
	private PatientCard patientCard;
	private String hospitalId;
	private String hospitalName;
	private Boolean doctorRecommendation;
	private Boolean isAppointmentStartedOnTime;
	private String howLateWasAppointment;
	private Float overallExperience;
	private String reasonOfVisit;
	private String experienceWithDoctor;
	private Integer noOfRecommendation;
	private String doctorReply;
	private Boolean isAnonymous;
	private Boolean isApproved = false;
	private String adminUpdatedExperienceWithDoctor;
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

	public String getHowLateWasAppointment() {
		return howLateWasAppointment;
	}

	public void setHowLateWasAppointment(String howLateWasAppointment) {
		this.howLateWasAppointment = howLateWasAppointment;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public String getAdminUpdatedExperienceWithDoctor() {
		return adminUpdatedExperienceWithDoctor;
	}

	public void setAdminUpdatedExperienceWithDoctor(String adminUpdatedExperienceWithDoctor) {
		this.adminUpdatedExperienceWithDoctor = adminUpdatedExperienceWithDoctor;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public PatientCard getPatientCard() {
		return patientCard;
	}

	public void setPatientCard(PatientCard patientCard) {
		this.patientCard = patientCard;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	@Override
	public String toString() {
		return "AppointmentGeneralFeedback [id=" + id + ", locationId=" + locationId + ", locationName=" + locationName
				+ ", doctorId=" + doctorId + ", doctorName=" + doctorName + ", patientId=" + patientId
				+ ", patientCard=" + patientCard + ", hospitalId=" + hospitalId + ", hospitalName=" + hospitalName
				+ ", doctorRecommendation=" + doctorRecommendation + ", isAppointmentStartedOnTime="
				+ isAppointmentStartedOnTime + ", howLateWasAppointment=" + howLateWasAppointment
				+ ", overallExperience=" + overallExperience + ", reasonOfVisit=" + reasonOfVisit
				+ ", experienceWithDoctor=" + experienceWithDoctor + ", noOfRecommendation=" + noOfRecommendation
				+ ", doctorReply=" + doctorReply + ", isAnonymous=" + isAnonymous + ", isApproved=" + isApproved
				+ ", adminUpdatedExperienceWithDoctor=" + adminUpdatedExperienceWithDoctor + ", isDiscarded="
				+ isDiscarded + "]";
	}

}
