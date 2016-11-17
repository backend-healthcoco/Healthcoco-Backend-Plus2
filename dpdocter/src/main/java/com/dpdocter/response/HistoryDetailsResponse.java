package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.BirthHistory;
import com.dpdocter.beans.DrugsAndAllergies;
import com.dpdocter.beans.GeneralData;
import com.dpdocter.beans.PersonalHistory;

public class HistoryDetailsResponse {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String doctorName;

	private List<GeneralData> generalRecords;

	private List<DiseaseListResponse> familyhistory;

	private List<DiseaseListResponse> medicalhistory;

	private DrugsAndAllergies drugsAndAllergies;

	private PersonalHistory personalHistory;

	private BirthHistory birthHistory;

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

	public DrugsAndAllergies getDrugsAndAllergies() {
		return drugsAndAllergies;
	}

	public void setDrugsAndAllergies(DrugsAndAllergies drugsAndAllergies) {
		this.drugsAndAllergies = drugsAndAllergies;
	}

	public PersonalHistory getPersonalHistory() {
		return personalHistory;
	}

	public void setPersonalHistory(PersonalHistory personalHistory) {
		this.personalHistory = personalHistory;
	}

	public BirthHistory getBirthHistory() {
		return birthHistory;
	}

	public void setBirthHistory(BirthHistory birthHistory) {
		this.birthHistory = birthHistory;
	}

	@Override
	public String toString() {
		return "HistoryDetailsResponse [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", doctorName=" + doctorName
				+ ", generalRecords=" + generalRecords + ", familyhistory=" + familyhistory + ", medicalhistory="
				+ medicalhistory + ", drugsAndAllergies=" + drugsAndAllergies + ", personalHistory=" + personalHistory
				+ ", birthHistory=" + birthHistory + ", specialNotes=" + specialNotes + "]";
	}

}
