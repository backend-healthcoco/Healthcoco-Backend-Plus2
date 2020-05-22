package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.BloodPressureType;
import com.dpdocter.enums.ResistanceTrainingProgramType;

public class ExerciseAndMovement extends GenericCollection {
	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private Boolean discarded = false;
	private Map<String, Boolean> exerciseAndMovementBoolen;
	private Map<String, String> exerciseAndMovementString;
	private Map<String, List<String>> exerciseAndMovementList;
	private Boolean isPartInStructuredCardiorespiratoryProgram;
	private StructuredCardiorespiratoryProgram structuredCardiorespiratoryProgram; 
	private ResistanceTrainingProgramType resistancetrainingProgramType;

	public String getId() {
		return id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public String getPatientId() {
		return patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public Map<String, Boolean> getExerciseAndMovementBoolen() {
		return exerciseAndMovementBoolen;
	}

	public Map<String, String> getExerciseAndMovementString() {
		return exerciseAndMovementString;
	}

	public Map<String, List<String>> getExerciseAndMovementList() {
		return exerciseAndMovementList;
	}

	public ResistanceTrainingProgramType getResistancetrainingProgramType() {
		return resistancetrainingProgramType;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public void setExerciseAndMovementBoolen(Map<String, Boolean> exerciseAndMovementBoolen) {
		this.exerciseAndMovementBoolen = exerciseAndMovementBoolen;
	}

	public void setExerciseAndMovementString(Map<String, String> exerciseAndMovementString) {
		this.exerciseAndMovementString = exerciseAndMovementString;
	}

	public void setExerciseAndMovementList(Map<String, List<String>> exerciseAndMovementList) {
		this.exerciseAndMovementList = exerciseAndMovementList;
	}

	public void setResistancetrainingProgramType(ResistanceTrainingProgramType resistancetrainingProgramType) {
		this.resistancetrainingProgramType = resistancetrainingProgramType;
	}

	public Boolean getIsPartInStructuredCardiorespiratoryProgram() {
		return isPartInStructuredCardiorespiratoryProgram;
	}

	public StructuredCardiorespiratoryProgram getStructuredCardiorespiratoryProgram() {
		return structuredCardiorespiratoryProgram;
	}

	public void setIsPartInStructuredCardiorespiratoryProgram(Boolean isPartInStructuredCardiorespiratoryProgram) {
		this.isPartInStructuredCardiorespiratoryProgram = isPartInStructuredCardiorespiratoryProgram;
	}

	public void setStructuredCardiorespiratoryProgram(
			StructuredCardiorespiratoryProgram structuredCardiorespiratoryProgram) {
		this.structuredCardiorespiratoryProgram = structuredCardiorespiratoryProgram;
	}

	@Override
	public String toString() {
		return "ExerciseAndMovement [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
				+ ", exerciseAndMovementBoolen=" + exerciseAndMovementBoolen + ", exerciseAndMovementString="
				+ exerciseAndMovementString + ", exerciseAndMovementList=" + exerciseAndMovementList
				+ ", isPartInStructuredCardiorespiratoryProgram=" + isPartInStructuredCardiorespiratoryProgram
				+ ", structuredCardiorespiratoryProgram=" + structuredCardiorespiratoryProgram
				+ ", resistancetrainingProgramType=" + resistancetrainingProgramType + "]";
	}

}
