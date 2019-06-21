package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.QuestionAnswers;
import com.dpdocter.enums.AppointmentWaitTime;
import com.dpdocter.enums.FeedbackType;
import com.dpdocter.enums.MedicationEffectType;

public class PatientFeedbackIOSResponse {

	private String id;
	private String locationId;
	private String doctorId;
	private String patientId;
	private String hospitalId;
	private String localeId;
	private String appointmentId;
	private String prescriptionId;
	private Boolean isRecommended = false;
	private Boolean isAppointmentStartedOnTime = false;
	private Integer howLateWasAppointmentInMinutes = 0;
	private Float overallExperience = 0.0F;
	private String reasonOfVisit;
	private String experience;
	private String reply;
	private Boolean isAnonymous = false;
	private Boolean isApproved = false;
	private String adminUpdatedExperience;
	private Boolean isDiscarded = false;
	private Boolean isMedicationOnTime;
	private List<QuestionAnswers> questionAnswers;
	private MedicationEffectType medicationEffectType;
	private FeedbackType feedbackType;
	private Boolean printPdfProvided = false;
	private List<String> services;
	private AppointmentWaitTime appointmentTiming;

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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
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

	public List<QuestionAnswers> getQuestionAnswers() {
		return questionAnswers;
	}

	public void setQuestionAnswers(List<QuestionAnswers> questionAnswers) {
		this.questionAnswers = questionAnswers;
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

	public Boolean getPrintPdfProvided() {
		return printPdfProvided;
	}

	public void setPrintPdfProvided(Boolean printPdfProvided) {
		this.printPdfProvided = printPdfProvided;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
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
		return "PatientFeedbackIOSResponse [id=" + id + ", locationId=" + locationId + ", doctorId=" + doctorId
				+ ", patientId=" + patientId + ", hospitalId=" + hospitalId + ", localeId=" + localeId
				+ ", appointmentId=" + appointmentId + ", prescriptionId=" + prescriptionId + ", isRecommended="
				+ isRecommended + ", isAppointmentStartedOnTime=" + isAppointmentStartedOnTime
				+ ", howLateWasAppointmentInMinutes=" + howLateWasAppointmentInMinutes + ", overallExperience="
				+ overallExperience + ", reasonOfVisit=" + reasonOfVisit + ", experience=" + experience + ", reply="
				+ reply + ", isAnonymous=" + isAnonymous + ", isApproved=" + isApproved + ", adminUpdatedExperience="
				+ adminUpdatedExperience + ", isDiscarded=" + isDiscarded + ", isMedicationOnTime=" + isMedicationOnTime
				+ ", questionAnswers=" + questionAnswers + ", medicationEffectType=" + medicationEffectType
				+ ", feedbackType=" + feedbackType + ", printPdfProvided=" + printPdfProvided + ", services=" + services
				+ ", time=" + appointmentTiming + "]";
	}

}
