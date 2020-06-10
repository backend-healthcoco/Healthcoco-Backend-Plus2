package com.dpdocter.request;

import com.dpdocter.beans.ExerciseAndMovement;
import com.dpdocter.beans.PhysicalActivityAndMedicalHistory;
import com.dpdocter.beans.TreatmentAndDiagnosis;

public class FitnessAssessmentRequest {

	private String id;

	private String doctorId;
	
	private String locationId;
	
	private String hospitalId;
	
	private String patientId;
	
	private Boolean discarded = false;
	
	private PhysicalActivityAndMedicalHistory  physicalActivityAndMedicalHistory;
	
	private TreatmentAndDiagnosis treatmentAndDiagnosis;
	
	private ExerciseAndMovement exerciseAndMovement;

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

	public PhysicalActivityAndMedicalHistory getPhysicalActivityAndMedicalHistory() {
		return physicalActivityAndMedicalHistory;
	}

	public TreatmentAndDiagnosis getTreatmentAndDiagnosis() {
		return treatmentAndDiagnosis;
	}

	public ExerciseAndMovement getExerciseAndMovement() {
		return exerciseAndMovement;
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
		return "FitnessAssessmentRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
				+ ", physicalActivityAndMedicalHistory=" + physicalActivityAndMedicalHistory
				+ ", treatmentAndDiagnosis=" + treatmentAndDiagnosis + ", exerciseAndMovement=" + exerciseAndMovement
				+ "]";
	}
	

}
