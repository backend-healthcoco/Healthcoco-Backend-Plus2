package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.beans.StructuredCardiorespiratoryProgram;
import com.dpdocter.enums.ResistanceTrainingProgramType;

@Document(collection = "exercise_movement_cl")
public class ExerciseMovementCollection extends GenericCollection {
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
	private Map<String, Boolean> exerciseAndMovementBoolen;
	@Field
	private Map<String, String> exerciseAndMovementString;
	@Field
	private Map<String, List<String>> exerciseAndMovementList;
	@Field
	private Boolean isPartInStructuredCardiorespiratoryProgram;
	@Field
	private StructuredCardiorespiratoryProgram structuredCardiorespiratoryProgram;
	@Field
	private ResistanceTrainingProgramType resistancetrainingProgramType;
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
	public Map<String, Boolean> getExerciseAndMovementBoolen() {
		return exerciseAndMovementBoolen;
	}
	public Map<String, String> getExerciseAndMovementString() {
		return exerciseAndMovementString;
	}
	public Map<String, List<String>> getExerciseAndMovementList() {
		return exerciseAndMovementList;
	}
	public Boolean getIsPartInStructuredCardiorespiratoryProgram() {
		return isPartInStructuredCardiorespiratoryProgram;
	}
	public StructuredCardiorespiratoryProgram getStructuredCardiorespiratoryProgram() {
		return structuredCardiorespiratoryProgram;
	}
	public ResistanceTrainingProgramType getResistancetrainingProgramType() {
		return resistancetrainingProgramType;
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
	public void setExerciseAndMovementBoolen(Map<String, Boolean> exerciseAndMovementBoolen) {
		this.exerciseAndMovementBoolen = exerciseAndMovementBoolen;
	}
	public void setExerciseAndMovementString(Map<String, String> exerciseAndMovementString) {
		this.exerciseAndMovementString = exerciseAndMovementString;
	}
	public void setExerciseAndMovementList(Map<String, List<String>> exerciseAndMovementList) {
		this.exerciseAndMovementList = exerciseAndMovementList;
	}
	public void setIsPartInStructuredCardiorespiratoryProgram(Boolean isPartInStructuredCardiorespiratoryProgram) {
		this.isPartInStructuredCardiorespiratoryProgram = isPartInStructuredCardiorespiratoryProgram;
	}
	public void setStructuredCardiorespiratoryProgram(
			StructuredCardiorespiratoryProgram structuredCardiorespiratoryProgram) {
		this.structuredCardiorespiratoryProgram = structuredCardiorespiratoryProgram;
	}
	public void setResistancetrainingProgramType(ResistanceTrainingProgramType resistancetrainingProgramType) {
		this.resistancetrainingProgramType = resistancetrainingProgramType;
	}
	@Override
	public String toString() {
		return "ExerciseMovementCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
				+ ", exerciseAndMovementBoolen=" + exerciseAndMovementBoolen + ", exerciseAndMovementString="
				+ exerciseAndMovementString + ", exerciseAndMovementList=" + exerciseAndMovementList
				+ ", isPartInStructuredCardiorespiratoryProgram=" + isPartInStructuredCardiorespiratoryProgram
				+ ", structuredCardiorespiratoryProgram=" + structuredCardiorespiratoryProgram
				+ ", resistancetrainingProgramType=" + resistancetrainingProgramType + "]";
	}

}
