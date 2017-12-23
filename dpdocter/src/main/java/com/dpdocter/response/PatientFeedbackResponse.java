package com.dpdocter.response;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.Prescription;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FeedbackType;
import com.dpdocter.enums.MedicationEffectType;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PatientFeedbackResponse extends GenericCollection {

	private String id;
	private String locationId;
	private String locationName;
	private String doctorId;
	private String doctorName;
	private String patientId;
	private PatientShortCard patientCard;
	private String hospitalId;
	private String hospitalName;
	private String localeId;
	private String localeName;
	private Prescription prescription;
	private Appointment appointment;
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
	private String appointmentId;
	private String prescriptionId;

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

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public PatientShortCard getPatientCard() {
		return patientCard;
	}

	public void setPatientCard(PatientShortCard patientCard) {
		this.patientCard = patientCard;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
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

	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
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

	@Override
	public String toString() {
		return "PatientFeedbackResponse [id=" + id + ", locationId=" + locationId + ", locationName=" + locationName
				+ ", doctorId=" + doctorId + ", doctorName=" + doctorName + ", patientId=" + patientId
				+ ", patientCard=" + patientCard + ", hospitalId=" + hospitalId + ", hospitalName=" + hospitalName
				+ ", localeId=" + localeId + ", localeName=" + localeName + ", isRecommended=" + isRecommended
				+ ", isAppointmentStartedOnTime=" + isAppointmentStartedOnTime + ", howLateWasAppointment="
				+ howLateWasAppointment + ", overallExperience=" + overallExperience + ", reasonOfVisit="
				+ reasonOfVisit + ", experience=" + experience + ", reply=" + reply + ", isAnonymous=" + isAnonymous
				+ ", isApproved=" + isApproved + ", adminUpdatedExperience=" + adminUpdatedExperience + ", isDiscarded="
				+ isDiscarded + ", isMedicationOnTime=" + isMedicationOnTime + ", medicationEffectType="
				+ medicationEffectType + ", feedbackType=" + feedbackType + "]";
	}

}
