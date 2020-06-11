package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import com.dpdocter.enums.FluctuateWeightType;
import com.dpdocter.enums.StressAreaOfLife;

public class PhysicalActivityAndMedicalHistory  {
		
	private String doctorId;
	
	private String locationId;
	
	private String hospitalId;
	
	private String patientId;
	
	private Boolean discarded = false;
	
	private Map<String, Boolean> physicalMedicalHistoryBoolean;
	
	private Map<String, String> physicalMedicalHistory;
	
	private Map<String, List<String>> physicalMedicalHistoryList;
	
	private FluctuateWeightType fluctuationsInWeight;

	private Integer stressLevelRating;
	
	private List<StressAreaOfLife> stressAreaOfLifeList;

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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Map<String, Boolean> getPhysicalMedicalHistoryBoolean() {
		return physicalMedicalHistoryBoolean;
	}

	public void setPhysicalMedicalHistoryBoolean(Map<String, Boolean> physicalMedicalHistoryBoolean) {
		this.physicalMedicalHistoryBoolean = physicalMedicalHistoryBoolean;
	}

	public Map<String, String> getPhysicalMedicalHistory() {
		return physicalMedicalHistory;
	}

	public void setPhysicalMedicalHistory(Map<String, String> physicalMedicalHistory) {
		this.physicalMedicalHistory = physicalMedicalHistory;
	}

	public Map<String, List<String>> getPhysicalMedicalHistoryList() {
		return physicalMedicalHistoryList;
	}

	public void setPhysicalMedicalHistoryList(Map<String, List<String>> physicalMedicalHistoryList) {
		this.physicalMedicalHistoryList = physicalMedicalHistoryList;
	}

	public FluctuateWeightType getFluctuationsInWeight() {
		return fluctuationsInWeight;
	}

	public void setFluctuationsInWeight(FluctuateWeightType fluctuationsInWeight) {
		this.fluctuationsInWeight = fluctuationsInWeight;
	}

	public Integer getStressLevelRating() {
		return stressLevelRating;
	}

	public void setStressLevelRating(Integer stressLevelRating) {
		this.stressLevelRating = stressLevelRating;
	}

	public List<StressAreaOfLife> getStressAreaOfLifeList() {
		return stressAreaOfLifeList;
	}

	public void setStressAreaOfLifeList(List<StressAreaOfLife> stressAreaOfLifeList) {
		this.stressAreaOfLifeList = stressAreaOfLifeList;
	}

	@Override
	public String toString() {
		return "PhysicalActivityAndMedicalHistory  [doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
				+ ", physicalMedicalHistoryBoolean=" + physicalMedicalHistoryBoolean + ", physicalMedicalHistory="
				+ physicalMedicalHistory + ", physicalMedicalHistoryList=" + physicalMedicalHistoryList
				+ ", fluctuationsInWeight=" + fluctuationsInWeight + ", stressLevelRating=" + stressLevelRating
				+ ", stressAreaOfLifeList=" + stressAreaOfLifeList + "]";
	}
	
	
}
