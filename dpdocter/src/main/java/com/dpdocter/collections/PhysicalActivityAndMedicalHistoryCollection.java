package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.enums.FluctuateWeightType;
import com.dpdocter.enums.StressAreaOfLife;

@Document(collection = "physical_activity_medical_history_cl")
public class PhysicalActivityAndMedicalHistoryCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private Boolean discarded = false;
	@Field
	private Map<String, Boolean> physicalMedicalHistoryBoolean;
	@Field
	private Map<String, String> physicalMedicalHistory;
	@Field
	private Map<String, List<String>> physicalMedicalHistoryList;
	@Field
	private FluctuateWeightType fluctuationsInWeight;
	@Field
	private Integer stressLevelRating;
	@Field
	private List<StressAreaOfLife> stressAreaOfLifeList;

	public ObjectId getId() {
		return id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public Map<String, Boolean> getPhysicalMedicalHistoryBoolean() {
		return physicalMedicalHistoryBoolean;
	}

	public Map<String, String> getPhysicalMedicalHistory() {
		return physicalMedicalHistory;
	}

	public Map<String, List<String>> getPhysicalMedicalHistoryList() {
		return physicalMedicalHistoryList;
	}

	public FluctuateWeightType getFluctuationsInWeight() {
		return fluctuationsInWeight;
	}

	public Integer getStressLevelRating() {
		return stressLevelRating;
	}

	public List<StressAreaOfLife> getStressAreaOfLifeList() {
		return stressAreaOfLifeList;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public void setPhysicalMedicalHistoryBoolean(Map<String, Boolean> physicalMedicalHistoryBoolean) {
		this.physicalMedicalHistoryBoolean = physicalMedicalHistoryBoolean;
	}

	public void setPhysicalMedicalHistory(Map<String, String> physicalMedicalHistory) {
		this.physicalMedicalHistory = physicalMedicalHistory;
	}

	public void setPhysicalMedicalHistoryList(Map<String, List<String>> physicalMedicalHistoryList) {
		this.physicalMedicalHistoryList = physicalMedicalHistoryList;
	}

	public void setFluctuationsInWeight(FluctuateWeightType fluctuationsInWeight) {
		this.fluctuationsInWeight = fluctuationsInWeight;
	}

	public void setStressLevelRating(Integer stressLevelRating) {
		this.stressLevelRating = stressLevelRating;
	}

	public void setStressAreaOfLifeList(List<StressAreaOfLife> stressAreaOfLifeList) {
		this.stressAreaOfLifeList = stressAreaOfLifeList;
	}

	@Override
	public String toString() {
		return "PhysicalActivityAndMedicalHistoryCollection [id=" + id + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
				+ ", physicalMedicalHistoryBoolean=" + physicalMedicalHistoryBoolean + ", physicalMedicalHistory="
				+ physicalMedicalHistory + ", physicalMedicalHistoryList=" + physicalMedicalHistoryList
				+ ", fluctuationsInWeight=" + fluctuationsInWeight + ", stressLevelRating=" + stressLevelRating
				+ ", stressAreaOfLifeList=" + stressAreaOfLifeList + "]";
	}

}
