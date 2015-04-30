package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "history_cl")
public class HistoryCollection {

	@Id
	private String id;
	@Field
	private String doctorId;
	@Field
	private String locationId;
	@Field
	private String hospitalId;
	@Field
	private String patientId;
	@Field
	private List<String> reports;
	@Field
	private List<String> prescriptions;
	@Field
	private List<String> clinicalNotes;
	@Field
	private List<String> familyhistory;
	@Field
	private List<String> medicalhistory;
	@Field
	private List<String> specialNotes;

	public HistoryCollection(String doctorId, String locationId, String hospitalId, String patientId) {
		super();
		this.doctorId = doctorId;
		this.locationId = locationId;
		this.hospitalId = hospitalId;
		this.patientId = patientId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public List<String> getReports() {
		return reports;
	}

	public void setReports(List<String> reports) {
		this.reports = reports;
	}

	public List<String> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(List<String> prescriptions) {
		this.prescriptions = prescriptions;
	}

	public List<String> getClinicalNotes() {
		return clinicalNotes;
	}

	public void setClinicalNotes(List<String> clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}

	public List<String> getFamilyhistory() {
		return familyhistory;
	}

	public void setFamilyhistory(List<String> familyhistory) {
		this.familyhistory = familyhistory;
	}

	public List<String> getMedicalhistory() {
		return medicalhistory;
	}

	public void setMedicalhistory(List<String> medicalhistory) {
		this.medicalhistory = medicalhistory;
	}

	public List<String> getSpecialNotes() {
		return specialNotes;
	}

	public void setSpecialNotes(List<String> specialNotes) {
		this.specialNotes = specialNotes;
	}

	@Override
	public String toString() {
		return "HistoryCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId="
				+ patientId + ", reports=" + reports + ", prescriptions=" + prescriptions + ", clinicalNotes=" + clinicalNotes + ", familyhistory="
				+ familyhistory + ", medicalhistory=" + medicalhistory + ", specialNotes=" + specialNotes + "]";
	}

}
