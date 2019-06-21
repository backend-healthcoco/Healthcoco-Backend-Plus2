package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DrugsAndAllergies;
import com.dpdocter.beans.PersonalHistory;

public class NutritionReferralRequest {

	private String id;

	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String referralDoctorId;

	private String referralLocationId;

	private String referralHospitalId;

	private String note;

	private List<String> familyhistory;

	private List<String> medicalhistory;

	private DrugsAndAllergies drugsAndAllergies;

	private PersonalHistory personalHistory;

	private Boolean everHospitalized = false;

	private List<String> tests;

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

	public String getReferralDoctorId() {
		return referralDoctorId;
	}

	public void setReferralDoctorId(String referralDoctorId) {
		this.referralDoctorId = referralDoctorId;
	}

	public String getReferralLocationId() {
		return referralLocationId;
	}

	public void setReferralLocationId(String referralLocationId) {
		this.referralLocationId = referralLocationId;
	}

	public String getReferralHospitalId() {
		return referralHospitalId;
	}

	public void setReferralHospitalId(String referralHospitalId) {
		this.referralHospitalId = referralHospitalId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

	public Boolean getEverHospitalized() {
		return everHospitalized;
	}

	public void setEverHospitalized(Boolean everHospitalized) {
		this.everHospitalized = everHospitalized;
	}

	public List<String> getTests() {
		return tests;
	}

	public void setTests(List<String> tests) {
		this.tests = tests;
	}
	
	
	
}
