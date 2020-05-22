package com.dpdocter.collections;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.ExerciseAndMovement;
import com.dpdocter.beans.PhysicalActivityAndMedicalHistory;
import com.dpdocter.beans.TreatmentAndDiagnosis;

public class FitnessAssessmentCollection extends GenericCollection{
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
	private ObjectId physicalActivityAndMedicalHistoryId;
	
	@Field
	private ObjectId treatmentAndDiagnosisId;
	
	@Field
	private ObjectId exerciseAndMovementId;
	
	@Field
	private Boolean discarded = false;
	
	@Field
	private PhysicalActivityAndMedicalHistory physicalActivityAndMedicalHistory;
	
	@Field
	private TreatmentAndDiagnosis treatmentAndDiagnosis;
	
	@Field
	private ExerciseAndMovement exerciseAndMovement;

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

	public ObjectId getPhysicalActivityAndMedicalHistoryId() {
		return physicalActivityAndMedicalHistoryId;
	}

	public ObjectId getTreatmentAndDiagnosisId() {
		return treatmentAndDiagnosisId;
	}

	public ObjectId getExerciseAndMovementId() {
		return exerciseAndMovementId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public PhysicalActivityAndMedicalHistory getPhysicalActivityAndMedicalHistory() {
		return physicalActivityAndMedicalHistory;
	}

	public TreatmentAndDiagnosis getTreatmentAndDiagnosis() {
		return treatmentAndDiagnosis;
	}

	public ExerciseAndMovement getExerciseAndMovement() {
		return exerciseAndMovement;
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

	public void setPhysicalActivityAndMedicalHistoryId(ObjectId physicalActivityAndMedicalHistoryId) {
		this.physicalActivityAndMedicalHistoryId = physicalActivityAndMedicalHistoryId;
	}

	public void setTreatmentAndDiagnosisId(ObjectId treatmentAndDiagnosisId) {
		this.treatmentAndDiagnosisId = treatmentAndDiagnosisId;
	}

	public void setExerciseAndMovementId(ObjectId exerciseAndMovementId) {
		this.exerciseAndMovementId = exerciseAndMovementId;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public void setPhysicalActivityAndMedicalHistory(PhysicalActivityAndMedicalHistory physicalActivityAndMedicalHistory) {
		this.physicalActivityAndMedicalHistory = physicalActivityAndMedicalHistory;
	}

	public void setTreatmentAndDiagnosis(TreatmentAndDiagnosis treatmentAndDiagnosis) {
		this.treatmentAndDiagnosis = treatmentAndDiagnosis;
	}

	public void setExerciseAndMovement(ExerciseAndMovement exerciseAndMovement) {
		this.exerciseAndMovement = exerciseAndMovement;
	}

	@Override
	public String toString() {
		return "FitnessAssessmentCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", physicalActivityAndMedicalHistoryId="
				+ physicalActivityAndMedicalHistoryId + ", treatmentAndDiagnosisId=" + treatmentAndDiagnosisId
				+ ", exerciseAndMovementId=" + exerciseAndMovementId + ", discarded=" + discarded
				+ ", physicalActivityAndMedicalHistory=" + physicalActivityAndMedicalHistory
				+ ", treatmentAndDiagnosis=" + treatmentAndDiagnosis + ", exerciseAndMovement=" + exerciseAndMovement
				+ "]";
	}
	
	

}
