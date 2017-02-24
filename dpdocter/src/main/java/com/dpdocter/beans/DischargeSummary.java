package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class DischargeSummary extends GenericCollection {

	private String id;
	private String patientId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private PatientCard patient;
	private List<ClinicalNotes> clinicalNotes;
	private List<Prescription> prescriptions;
	private List<Treatment> treatments;
	private Long admissionDate;
	private Long dischargeDate;
	private String labourNotes;
	private String babyWeight;
	private String babyNotes;
	private String conditionsAtDischarge;
	private String summary;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public PatientCard getPatient() {
		return patient;
	}

	public void setPatient(PatientCard patient) {
		this.patient = patient;
	}

	public List<ClinicalNotes> getClinicalNotes() {
		return clinicalNotes;
	}

	public void setClinicalNotes(List<ClinicalNotes> clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}

	public List<Prescription> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(List<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

	public List<Treatment> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<Treatment> treatments) {
		this.treatments = treatments;
	}

	public Long getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Long admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Long getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Long dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public String getLabourNotes() {
		return labourNotes;
	}

	public void setLabourNotes(String labourNotes) {
		this.labourNotes = labourNotes;
	}

	public String getBabyWeight() {
		return babyWeight;
	}

	public void setBabyWeight(String babyWeight) {
		this.babyWeight = babyWeight;
	}

	public String getBabyNotes() {
		return babyNotes;
	}

	public void setBabyNotes(String babyNotes) {
		this.babyNotes = babyNotes;
	}

	public String getConditionsAtDischarge() {
		return conditionsAtDischarge;
	}

	public void setConditionsAtDischarge(String conditionsAtDischarge) {
		this.conditionsAtDischarge = conditionsAtDischarge;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public String toString() {
		return "DischargeSummary [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patient=" + patient + ", clinicalNotes="
				+ clinicalNotes + ", prescriptions=" + prescriptions + ", treatments=" + treatments + ", admissionDate="
				+ admissionDate + ", dischargeDate=" + dischargeDate + ", labourNotes=" + labourNotes + ", babyWeight="
				+ babyWeight + ", babyNotes=" + babyNotes + ", conditionsAtDischarge=" + conditionsAtDischarge
				+ ", summary=" + summary + "]";
	}

}
