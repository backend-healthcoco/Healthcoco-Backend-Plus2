package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.request.FoodAndAllergiesRequest;

public class PatientAssesentmentHistoryRequest extends GenericCollection {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private List<GeneralData> generalRecords;

	private List<String> familyhistory;

	private List<String> medicalhistory;

	private DrugsAndAllergies drugsAndAllergies;

	private PersonalHistory personalHistory;

	private long count;

	private Boolean isPatientDiscarded = false;
	
	private Boolean isStress = false;

	private List<Addiction> addiction;

	private List<String> diesease;

	private Boolean everHospitalize = false;

	private String reason;

	private String stressReason;
	
	private String specialNotes;

	private Integer noOfTime = 0;

	private FoodAndAllergiesRequest foodAndAllergies;

	private List<PrescriptionAddItem> existingMedication;

	private String assessmentId;

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
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

	public Boolean getIsStress() {
		return isStress;
	}

	public void setIsStress(Boolean isStress) {
		this.isStress = isStress;
	}

	public List<Addiction> getAddiction() {
		return addiction;
	}

	public void setAddiction(List<Addiction> addiction) {
		this.addiction = addiction;
	}

	public List<String> getDiesease() {
		return diesease;
	}

	public void setDiesease(List<String> diesease) {
		this.diesease = diesease;
	}

	public Boolean getEverHospitalize() {
		return everHospitalize;
	}

	public void setEverHospitalize(Boolean everHospitalize) {
		this.everHospitalize = everHospitalize;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStressReason() {
		return stressReason;
	}

	public void setStressReason(String stressReason) {
		this.stressReason = stressReason;
	}

	public String getSpecialNotes() {
		return specialNotes;
	}

	public void setSpecialNotes(String specialNotes) {
		this.specialNotes = specialNotes;
	}

	public Integer getNoOfTime() {
		return noOfTime;
	}

	public void setNoOfTime(Integer noOfTime) {
		this.noOfTime = noOfTime;
	}

	public FoodAndAllergiesRequest getFoodAndAllergies() {
		return foodAndAllergies;
	}

	public void setFoodAndAllergies(FoodAndAllergiesRequest foodAndAllergies) {
		this.foodAndAllergies = foodAndAllergies;
	}

	public List<PrescriptionAddItem> getExistingMedication() {
		return existingMedication;
	}

	public void setExistingMedication(List<PrescriptionAddItem> existingMedication) {
		this.existingMedication = existingMedication;
	}

	public List<GeneralData> getGeneralRecords() {
		return generalRecords;
	}

	public void setGeneralRecords(List<GeneralData> generalRecords) {
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

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "PatientAssesentmentHistoryRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", generalRecords=" + generalRecords
				+ ", familyhistory=" + familyhistory + ", medicalhistory=" + medicalhistory + ", drugsAndAllergies="
				+ drugsAndAllergies + ", personalHistory=" + personalHistory + ", count=" + count
				+ ", isPatientDiscarded=" + isPatientDiscarded + ", isStress=" + isStress + ", addiction=" + addiction
				+ ", diesease=" + diesease + ", everHospitalize=" + everHospitalize + ", reason=" + reason
				+ ", stressReason=" + stressReason + ", specialNotes=" + specialNotes + ", noOfTime=" + noOfTime
				+ ", foodAndAllergies=" + foodAndAllergies + ", existingMedication=" + existingMedication
				+ ", assessmentId=" + assessmentId + "]";
	}
}
