package com.dpdocter.collections;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.QuestionAnswers;
import com.dpdocter.enums.AppointmentWaitTime;
import com.dpdocter.enums.FeedbackType;
import com.dpdocter.enums.MedicationEffectType;

@Document(collection = "patient_feedback_cl")
public class PatientFeedbackCollection extends GenericCollection {

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
	private ObjectId localeId;
	@Field
	private Boolean isRecommended;
	@Field
	private Boolean isAppointmentStartedOnTime;
	@Field
	private Integer howLateWasAppointmentInMinutes;
	@Field
	private Float overallExperience;
	@Field
	private String reasonOfVisit;
	@Field
	private String experience;
	@Field
	private String reply;
	@Field
	private Boolean isAnonymous;
	@Field
	private Boolean isApproved = false;
	@Field
	private String adminUpdatedExperience;
	@Field
	private Boolean isDiscarded = false;
	@Field
	private Boolean isMedicationOnTime;
	@Field
	private MedicationEffectType medicationEffectType; // how patient feeling after taking medicine
	@Field
	private FeedbackType feedbackType;
	@Field
	private ObjectId appointmentId;
	@Field
	private ObjectId prescriptionId;
	@Field
	private List<QuestionAnswers> questionAnswers;
	@Field
	private Boolean isPatientDiscarded = false;
	@Field
	private Boolean printPdfProvided = false;
	@Field
	private Set<ObjectId> services;
	@Field
	private AppointmentWaitTime appointmentTiming;

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

	public Integer getHowLateWasAppointmentInMinutes() {
		return howLateWasAppointmentInMinutes;
	}

	public void setHowLateWasAppointmentInMinutes(Integer howLateWasAppointmentInMinutes) {
		this.howLateWasAppointmentInMinutes = howLateWasAppointmentInMinutes;
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

	public ObjectId getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(ObjectId appointmentId) {
		this.appointmentId = appointmentId;
	}

	public ObjectId getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(ObjectId prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public List<QuestionAnswers> getQuestionAnswers() {
		return questionAnswers;
	}

	public void setQuestionAnswers(List<QuestionAnswers> questionAnswers) {
		this.questionAnswers = questionAnswers;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public Boolean getPrintPdfProvided() {
		return printPdfProvided;
	}

	public void setPrintPdfProvided(Boolean printPdfProvided) {
		this.printPdfProvided = printPdfProvided;
	}

	public Set<ObjectId> getServices() {
		return services;
	}

	public void setServices(Set<ObjectId> services) {
		this.services = services;
	}

	public AppointmentWaitTime getAppointmentTiming() {
		return appointmentTiming;
	}

	public void setAppointmentTiming(AppointmentWaitTime appointmentTiming) {
		this.appointmentTiming = appointmentTiming;
	}

	@Override
	public String toString() {
		return "PatientFeedbackCollection [id=" + id + ", locationId=" + locationId + ", doctorId=" + doctorId
				+ ", patientId=" + patientId + ", hospitalId=" + hospitalId + ", localeId=" + localeId
				+ ", isRecommended=" + isRecommended + ", isAppointmentStartedOnTime=" + isAppointmentStartedOnTime
				+ ", howLateWasAppointmentInMinutes=" + howLateWasAppointmentInMinutes + ", overallExperience="
				+ overallExperience + ", reasonOfVisit=" + reasonOfVisit + ", experience=" + experience + ", reply="
				+ reply + ", isAnonymous=" + isAnonymous + ", isApproved=" + isApproved + ", adminUpdatedExperience="
				+ adminUpdatedExperience + ", isDiscarded=" + isDiscarded + ", isMedicationOnTime=" + isMedicationOnTime
				+ ", medicationEffectType=" + medicationEffectType + ", feedbackType=" + feedbackType
				+ ", appointmentId=" + appointmentId + ", prescriptionId=" + prescriptionId + ", questionAnswers="
				+ questionAnswers + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}
}
