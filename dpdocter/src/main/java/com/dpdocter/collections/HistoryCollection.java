package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Addiction;
import com.dpdocter.beans.DrugsAndAllergies;
import com.dpdocter.beans.FoodAndAllergies;
import com.dpdocter.beans.GeneralData;
import com.dpdocter.beans.PersonalHistory;
import com.dpdocter.beans.PrescriptionItem;

@Document(collection = "history_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class HistoryCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Indexed
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Indexed
	private ObjectId patientId;

	@Field
	private List<GeneralData> generalRecords;

	@Field
	private List<ObjectId> familyhistory;

	@Field
	private List<ObjectId> medicalhistory;

	@Field
	private DrugsAndAllergies drugsAndAllergies;

	@Field
	private PersonalHistory personalHistory;

	@Field
	private List<ObjectId> specialNotes;

	@Transient
	private long count;

	@Field
	private Boolean isPatientDiscarded = false;

	@Field
	private Boolean isStress = false;

	@Field
	private List<Addiction> addiction;

	@Field
	private List<ObjectId> diesease;

	@Field
	private Boolean everHospitalize = false;

	@Field
	private List<String> reasons;

	@Field
	private Integer noOfTime = 0;

	@Field
	private FoodAndAllergies foodAndAllergies;

	@Field
	private List<PrescriptionItem> existingMedication;

	@Field
	private ObjectId assessmentId;

	public HistoryCollection(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId) {
		super();
		this.doctorId = doctorId;
		this.locationId = locationId;
		this.hospitalId = hospitalId;
		this.patientId = patientId;
	}

	public HistoryCollection() {
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public List<GeneralData> getGeneralRecords() {
		return generalRecords;
	}

	public void setGeneralRecords(List<GeneralData> generalRecords) {
		this.generalRecords = generalRecords;
	}

	public List<ObjectId> getFamilyhistory() {
		return familyhistory;
	}

	public void setFamilyhistory(List<ObjectId> familyhistory) {
		this.familyhistory = familyhistory;
	}

	public List<ObjectId> getMedicalhistory() {
		return medicalhistory;
	}

	public void setMedicalhistory(List<ObjectId> medicalhistory) {
		this.medicalhistory = medicalhistory;
	}

	public List<ObjectId> getSpecialNotes() {
		return specialNotes;
	}

	public void setSpecialNotes(List<ObjectId> specialNotes) {
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
		return "HistoryCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", patientId=" + patientId + ", generalRecords=" + generalRecords + ", familyhistory="
				+ familyhistory + ", medicalhistory=" + medicalhistory + ", drugsAndAllergies=" + drugsAndAllergies
				+ ", personalHistory=" + personalHistory + ", specialNotes=" + specialNotes + ", count=" + count
				+ ", isPatientDiscarded=" + isPatientDiscarded + "]";
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

	public List<ObjectId> getDiesease() {
		return diesease;
	}

	public void setDiesease(List<ObjectId> diesease) {
		this.diesease = diesease;
	}

	public Boolean getEverHospitalize() {
		return everHospitalize;
	}

	public void setEverHospitalize(Boolean everHospitalize) {
		this.everHospitalize = everHospitalize;
	}

	public List<String> getReasons() {
		return reasons;
	}

	public void setReasons(List<String> reasons) {
		this.reasons = reasons;
	}

	public Integer getNoOfTime() {
		return noOfTime;
	}

	public void setNoOfTime(Integer noOfTime) {
		this.noOfTime = noOfTime;
	}

	public FoodAndAllergies getFoodAndAllergies() {
		return foodAndAllergies;
	}

	public void setFoodAndAllergies(FoodAndAllergies foodAndAllergies) {
		this.foodAndAllergies = foodAndAllergies;
	}

	public List<PrescriptionItem> getExistingMedication() {
		return existingMedication;
	}

	public void setExistingMedication(List<PrescriptionItem> existingMedication) {
		this.existingMedication = existingMedication;
	}

	public ObjectId getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(ObjectId assessmentId) {
		this.assessmentId = assessmentId;
	}

}
