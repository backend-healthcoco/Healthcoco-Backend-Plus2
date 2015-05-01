package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.Records;

public class HistoryDetailsResponse {
	
	private String id;
	
	private String doctorId;
	
	private String locationId;
	
	private String hospitalId;
	
	private String patientId;
	
	private List<Records> reports;
	
	private List<Prescription> prescriptions;
	
	private List<ClinicalNotes> clinicalNotes;
	
	private List<DiseaseListResponse> familyhistory;
	
	private List<DiseaseListResponse> medicalhistory;
	
	private List<String> specialNotes;
	
	

	public HistoryDetailsResponse(String id, String doctorId,
			String locationId, String hospitalId, String patientId) {
		this.id = id;
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

	public List<Records> getReports() {
		return reports;
	}

	public void setReports(List<Records> reports) {
		this.reports = reports;
	}

	public List<Prescription> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(List<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

	public List<ClinicalNotes> getClinicalNotes() {
		return clinicalNotes;
	}

	public void setClinicalNotes(List<ClinicalNotes> clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}

	public List<DiseaseListResponse> getFamilyhistory() {
		return familyhistory;
	}

	public void setFamilyhistory(List<DiseaseListResponse> familyhistory) {
		this.familyhistory = familyhistory;
	}

	public List<DiseaseListResponse> getMedicalhistory() {
		return medicalhistory;
	}

	public void setMedicalhistory(List<DiseaseListResponse> medicalhistory) {
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
		return "HistoryDetailsResponse [id=" + id + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", reports=" + reports
				+ ", prescriptions=" + prescriptions + ", clinicalNotes="
				+ clinicalNotes + ", familyhistory=" + familyhistory
				+ ", medicalhistory=" + medicalhistory + ", specialNotes="
				+ specialNotes + "]";
	}
	
	
	
}
