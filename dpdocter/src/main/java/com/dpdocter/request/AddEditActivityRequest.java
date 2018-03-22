package com.request;


public class AddEditActivityRequest {
	
	private String patientId;
	private String Id;
	private String ExerciseType;
	private String LifeStyle;
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getExerciseType() {
		return ExerciseType;
	}
	public void setExerciseType(String exerciseType) {
		ExerciseType = exerciseType;
	}
	public String getLifeStyle() {
		return LifeStyle;
	}
	public void setLifeStyle(String lifeStyle) {
		LifeStyle = lifeStyle;
	}
	@Override
	public String toString() {
		return "AddEditActivityRequest [patientId=" + patientId + ", Id=" + Id + ", ExerciseType=" + ExerciseType
				+ ", LifeStyle=" + LifeStyle + "]";
	}
	
	

}
