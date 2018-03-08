package com.dpdocter.elasticsearch.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.beans.DrugsAndAllergies;
import com.dpdocter.beans.PersonalHistory;
import com.dpdocter.collections.GenericCollection;

@Document(collection = "nutrition_referral_cl")
public class NutritionReferralCollection extends GenericCollection{

	private ObjectId id;

	private ObjectId patientId;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private ObjectId referralDoctorId;

	private ObjectId referralLocationId;

	private ObjectId referralHospitalId;

	private String note;

	private List<String> familyhistory;

	private List<String> medicalhistory;

	private DrugsAndAllergies drugsAndAllergies;

	private PersonalHistory personalHistory;

	private Boolean everHospitalized = false;

	private List<String> tests;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getReferralDoctorId() {
		return referralDoctorId;
	}

	public void setReferralDoctorId(ObjectId referralDoctorId) {
		this.referralDoctorId = referralDoctorId;
	}

	public ObjectId getReferralLocationId() {
		return referralLocationId;
	}

	public void setReferralLocationId(ObjectId referralLocationId) {
		this.referralLocationId = referralLocationId;
	}

	public ObjectId getReferralHospitalId() {
		return referralHospitalId;
	}

	public void setReferralHospitalId(ObjectId referralHospitalId) {
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
