package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.ExerciseAndMovement;
import com.dpdocter.beans.PhysicalActivityAndMedicalHistory;
import com.dpdocter.beans.TreatmentAndDiagnosis;

@Document(collection = "fitness_assessment_cl")
public class FitnessAssessmentCollection extends GenericCollection {
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
	private PhysicalActivityAndMedicalHistory physicalActivityAndMedicalHistory;

	@Field
	private TreatmentAndDiagnosis treatmentAndDiagnosis;

	@Field
	private ExerciseAndMovement exerciseAndMovement;

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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public PhysicalActivityAndMedicalHistory getPhysicalActivityAndMedicalHistory() {
		return physicalActivityAndMedicalHistory;
	}

	public void setPhysicalActivityAndMedicalHistory(
			PhysicalActivityAndMedicalHistory physicalActivityAndMedicalHistory) {
		this.physicalActivityAndMedicalHistory = physicalActivityAndMedicalHistory;
	}

	public TreatmentAndDiagnosis getTreatmentAndDiagnosis() {
		return treatmentAndDiagnosis;
	}

	public void setTreatmentAndDiagnosis(TreatmentAndDiagnosis treatmentAndDiagnosis) {
		this.treatmentAndDiagnosis = treatmentAndDiagnosis;
	}

	public ExerciseAndMovement getExerciseAndMovement() {
		return exerciseAndMovement;
	}

	public void setExerciseAndMovement(ExerciseAndMovement exerciseAndMovement) {
		this.exerciseAndMovement = exerciseAndMovement;
	}

	@Override
	public String toString() {
		return "FitnessAssessmentCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
				+ ", physicalActivityAndMedicalHistory=" + physicalActivityAndMedicalHistory
				+ ", treatmentAndDiagnosis=" + treatmentAndDiagnosis + ", exerciseAndMovement=" + exerciseAndMovement
				+ "]";
	}

}
