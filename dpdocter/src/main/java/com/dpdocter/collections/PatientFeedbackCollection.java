package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.enums.FeedbackType;
import com.dpdocter.enums.MedicationEffectType;

@Document(collection = "patient_feedback_cl")
public class PatientFeedbackCollection extends GenericCollection {

	private ObjectId id;
	private ObjectId locationId;
	private ObjectId doctorId;
	private ObjectId patientId;
	private ObjectId hospitalId;
	private ObjectId localeId;
	private Boolean isRecommended;
	private Boolean isAppointmentStartedOnTime;
	private String howLateWasAppointment;
	private Float overallExperience;
	private String reasonOfVisit;
	private String experience;
	private String reply;
	private Boolean isAnonymous;
	private Boolean isApproved = false;
	private String adminUpdatedExperience;
	private Boolean isDiscarded = false;
	private Boolean isMedicationOnTime;
	private MedicationEffectType medicationEffectType; // how patient feeling
	// after taking medicine
	private FeedbackType feedbackType;

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

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getLocaleId() {
		return localeId;
	}

	public void setLocaleId(ObjectId localeId) {
		this.localeId = localeId;
	}

	public Boolean getIsRecommended() {
		return isRecommended;
	}

	public void setIsRecommended(Boolean isRecommended) {
		this.isRecommended = isRecommended;
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

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
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

	public String getAdminUpdatedExperience() {
		return adminUpdatedExperience;
	}

	public void setAdminUpdatedExperience(String adminUpdatedExperience) {
		this.adminUpdatedExperience = adminUpdatedExperience;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public Boolean getIsMedicationOnTime() {
		return isMedicationOnTime;
	}

	public void setIsMedicationOnTime(Boolean isMedicationOnTime) {
		this.isMedicationOnTime = isMedicationOnTime;
	}

	public MedicationEffectType getMedicationEffectType() {
		return medicationEffectType;
	}

	public void setMedicationEffectType(MedicationEffectType medicationEffectType) {
		this.medicationEffectType = medicationEffectType;
	}

	public FeedbackType getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(FeedbackType feedbackType) {
		this.feedbackType = feedbackType;
	}

	@Override
	public String toString() {
		return "PatientFeedbackCollection [id=" + id + ", locationId=" + locationId + ", doctorId=" + doctorId
				+ ", patientId=" + patientId + ", hospitalId=" + hospitalId + ", localeId=" + localeId
				+ ", isRecommended=" + isRecommended + ", isAppointmentStartedOnTime=" + isAppointmentStartedOnTime
				+ ", howLateWasAppointment=" + howLateWasAppointment + ", overallExperience=" + overallExperience
				+ ", reasonOfVisit=" + reasonOfVisit + ", experience=" + experience + ", reply=" + reply
				+ ", isAnonymous=" + isAnonymous + ", isApproved=" + isApproved + ", adminUpdatedExperience="
				+ adminUpdatedExperience + ", isDiscarded=" + isDiscarded + ", isMedicationOnTime=" + isMedicationOnTime
				+ ", medicationEffectType=" + medicationEffectType + ", feedbackType=" + feedbackType + "]";
	}

}
