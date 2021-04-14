package com.dpdocter.collections;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;



@Document(collection = "patient_initial_assessment_forms_cl")
public class InitialAssessmentCollection extends GenericCollection {

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
	private String observation;

	@Field
	private String investigation;
	
	@Field
	private String provisionalDiagnosis;

	@Field
	private String generalExam;

	@Field
	private String presentComplaint;
	
	@Field
	private String noseExam;

	@Field
	private String oralCavityThroatExam;

	@Field
	private String neckExam;

	@Field
	private String earsExam;

	@Field
	private String pastHistory;

	@Field
	private String psychologicalAssessment;
	
	@Field
	private boolean discarded = false;

	@Field
	private String uniqueEmrId;
	
	@Field
	private String treatmentsPlan;
	
	@Field
	private String IPDNumber;

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

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public String getInvestigation() {
		return investigation;
	}

	public void setInvestigation(String investigation) {
		this.investigation = investigation;
	}

	public String getProvisionalDiagnosis() {
		return provisionalDiagnosis;
	}

	public void setProvisionalDiagnosis(String provisionalDiagnosis) {
		this.provisionalDiagnosis = provisionalDiagnosis;
	}

	public String getGeneralExam() {
		return generalExam;
	}

	public void setGeneralExam(String generalExam) {
		this.generalExam = generalExam;
	}

	public String getPresentComplaint() {
		return presentComplaint;
	}

	public void setPresentComplaint(String presentComplaint) {
		this.presentComplaint = presentComplaint;
	}

	public String getNoseExam() {
		return noseExam;
	}

	public void setNoseExam(String noseExam) {
		this.noseExam = noseExam;
	}

	public String getOralCavityThroatExam() {
		return oralCavityThroatExam;
	}

	public void setOralCavityThroatExam(String oralCavityThroatExam) {
		this.oralCavityThroatExam = oralCavityThroatExam;
	}

	
	public String getNeckExam() {
		return neckExam;
	}

	public void setNeckExam(String neckExam) {
		this.neckExam = neckExam;
	}

	public String getEarsExam() {
		return earsExam;
	}

	public void setEarsExam(String earsExam) {
		this.earsExam = earsExam;
	}

	public String getPastHistory() {
		return pastHistory;
	}

	public void setPastHistory(String pastHistory) {
		this.pastHistory = pastHistory;
	}

	public String getPsychologicalAssessment() {
		return psychologicalAssessment;
	}

	public void setPsychologicalAssessment(String psychologicalAssessment) {
		this.psychologicalAssessment = psychologicalAssessment;
	}

	public boolean isDiscarded() {
		return discarded;
	}

	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getTreatmentsPlan() {
		return treatmentsPlan;
	}

	public void setTreatmentsPlan(String treatmentsPlan) {
		this.treatmentsPlan = treatmentsPlan;
	}

	public String getIPDNumber() {
		return IPDNumber;
	}

	public void setIPDNumber(String iPDNumber) {
		IPDNumber = iPDNumber;
	}
	
	
	
	
}
