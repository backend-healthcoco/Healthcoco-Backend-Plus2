package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.GeneralData;

public class HistoryDetailsResponse {

    private String id;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String patientId;

    private String doctorName;

    private List<GeneralData> generalRecords;

    /*private List<Records> reports;

    private List<Prescription> prescriptions;

    private List<ClinicalNotes> clinicalNotes;*/

    private List<DiseaseListResponse> familyhistory;

    private List<DiseaseListResponse> medicalhistory;

    private List<String> specialNotes;
    
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

    public String getDoctorName() {
	return doctorName;
    }

    public void setDoctorName(String doctorName) {
	this.doctorName = doctorName;
    }

    public List<GeneralData> getGeneralRecords() {
	return generalRecords;
    }

    public void setGeneralRecords(List<GeneralData> generalRecords) {
	this.generalRecords = generalRecords;
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
		return "HistoryDetailsResponse [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", doctorName=" + doctorName
				+ ", generalRecords=" + generalRecords + ", familyhistory=" + familyhistory + ", medicalhistory="
				+ medicalhistory + ", specialNotes=" + specialNotes + "]";
	}

}
