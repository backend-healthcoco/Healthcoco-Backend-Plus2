package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DrugsAndAllergies;
import com.dpdocter.beans.PersonalHistory;

@Document(collection = "nutrition_referral_cl")
public class NutritionReferralCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private ObjectId patientId;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId referralDoctorId;

	@Field
	private ObjectId referralLocationId;

	@Field
	private ObjectId referralHospitalId;

	@Field
	private String note;

	@Field
	private List<String> familyhistory;

	@Field
	private List<String> medicalhistory;

	@Field
	private DrugsAndAllergies drugsAndAllergies;

	@Field
	private PersonalHistory personalHistory;

	@Field
	private Boolean everHospitalized = false;

	@Field
	private List<String> tests;

	@Field
	private Boolean isPatientDiscarded = false;

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

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "NutritionReferralCollection [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", referralDoctorId=" + referralDoctorId
				+ ", referralLocationId=" + referralLocationId + ", referralHospitalId=" + referralHospitalId
				+ ", note=" + note + ", familyhistory=" + familyhistory + ", medicalhistory=" + medicalhistory
				+ ", drugsAndAllergies=" + drugsAndAllergies + ", personalHistory=" + personalHistory
				+ ", everHospitalized=" + everHospitalized + ", tests=" + tests + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}
}
