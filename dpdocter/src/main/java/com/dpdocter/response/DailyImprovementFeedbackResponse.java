package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DailyPatientFeedback;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.Prescription;
import com.dpdocter.collections.GenericCollection;

public class DailyImprovementFeedbackResponse extends GenericCollection {

	private String id;
	private String locationId;
	private String locationName;
	private String doctorId;
	private String doctorName;
	private String patientId;
	private PatientShortCard patientCard;
	private String hospitalId;
	private String hospitalName;
	private String prescriptionId;
	private List<DailyPatientFeedback> dailyPatientFeedbacks;
	private Boolean discarded;
	private Prescription prescription;
	private String uniqueEmrId;

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

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public List<DailyPatientFeedback> getDailyPatientFeedbacks() {
		return dailyPatientFeedbacks;
	}

	public void setDailyPatientFeedbacks(List<DailyPatientFeedback> dailyPatientFeedbacks) {
		this.dailyPatientFeedbacks = dailyPatientFeedbacks;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Prescription getPrescription() {
		return prescription;
	}

	public void setPrescription(Prescription prescription) {
		this.prescription = prescription;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	@Override
	public String toString() {
		return "DailyImprovementFeedbackResponse [id=" + id + ", locationId=" + locationId + ", locationName="
				+ locationName + ", doctorId=" + doctorId + ", doctorName=" + doctorName + ", patientId=" + patientId
				+ ", patientCard=" + patientCard + ", hospitalId=" + hospitalId + ", hospitalName=" + hospitalName
				+ ", prescriptionId=" + prescriptionId + ", dailyPatientFeedbacks=" + dailyPatientFeedbacks
				+ ", discarded=" + discarded + "]";
	}

}
