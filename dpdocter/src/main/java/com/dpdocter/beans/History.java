package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class History {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private GeneralData generalRecords;

	private List<String> familyhistory;

	private List<String> medicalhistory;

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

	public GeneralData getGeneralRecords() {
		return generalRecords;
	}

	public void setGeneralRecords(GeneralData generalRecords) {
		this.generalRecords = generalRecords;
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
		return "History [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", patientId=" + patientId + ", generalRecords=" + generalRecords + ", familyhistory="
				+ familyhistory + ", medicalhistory=" + medicalhistory + ", drugsAndAllergies=" + drugsAndAllergies
				+ ", personalHistory=" + personalHistory + ", birthHistory=" + birthHistory + ", specialNotes="
				+ specialNotes + "]";
	}

}
