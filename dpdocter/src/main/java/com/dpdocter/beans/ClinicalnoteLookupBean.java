package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.response.TreatmentResponse;

public class ClinicalnoteLookupBean extends GenericCollection {

	private ObjectId id;

	private String uniqueEmrId;

	private List<ObjectId> notes;

	private List<ObjectId> observations;

	private List<ObjectId> investigations;

	private List<ObjectId> diagnoses;

	private List<ObjectId> complaints;

	private List<ObjectId> diagrams;

	private String note;

	private String observation;

	private String investigation;

	private String diagnosis;

	private String provisionalDiagnosis;

	private String generalExam;

	private String systemExam;

	private String complaint;

	private String presentComplaint;

	private String presentComplaintHistory;

	private String menstrualHistory;

	private String obstetricHistory;

	private String indicationOfUSG;

	private String procedureNote;

	private String pv;

	private String pa;

	private String ps;

	private String ecgDetails;

	private String xRayDetails;

	private String echo;

	private String holter;

	private String pcNose;

	private String pcOralCavity;

	private String pcThroat;

	private String pcEars;

	private String noseExam;

	private String oralCavityThroatExam;

	private String indirectLarygoscopyExam;

	private String neckExam;

	private String earsExam;

	private List<String> comments;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private ObjectId patientId;

	private Boolean discarded = false;

	private boolean inHistory = false;

	private VitalSigns vitalSigns;

	private String appointmentId;

	private WorkingHours time;

	private Date fromDate;

	private Appointment appointmentRequest;

	private String pastHistory;

	private String familyHistory;

	private String personalHistoryTobacco;
	private String personalHistoryAlcohol;
	private String personalHistorySmoking;
	private String personalHistoryDiet;
	private String personalHistoryOccupation;
	private String generalHistoryDrugs;
	private String generalHistoryMedicine;
	private String generalHistoryAllergies;
	private String generalHistorySurgical;
	private String painScale;
	private String priorConsultations;

	private PatientCollection patient;

	private UserCollection patientUser;
	
	private EyeSpecialityObservation eyeObservation;
	
	private PhysioExamination physioExamination;
	
	private List<TreatmentResponse> treatments;
	
	private List<Observation> treatmentObservation;
	
	@Field
	private List<FieldsCollection> treatmentFields;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public List<ObjectId> getNotes() {
		return notes;
	}

	public void setNotes(List<ObjectId> notes) {
		this.notes = notes;
	}

	public List<ObjectId> getObservations() {
		return observations;
	}

	public void setObservations(List<ObjectId> observations) {
		this.observations = observations;
	}

	public List<ObjectId> getInvestigations() {
		return investigations;
	}

	public void setInvestigations(List<ObjectId> investigations) {
		this.investigations = investigations;
	}

	public List<ObjectId> getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(List<ObjectId> diagnoses) {
		this.diagnoses = diagnoses;
	}

	public List<ObjectId> getComplaints() {
		return complaints;
	}

	public void setComplaints(List<ObjectId> complaints) {
		this.complaints = complaints;
	}

	public List<ObjectId> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<ObjectId> diagrams) {
		this.diagrams = diagrams;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
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

	public String getSystemExam() {
		return systemExam;
	}

	public void setSystemExam(String systemExam) {
		this.systemExam = systemExam;
	}

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public String getPresentComplaint() {
		return presentComplaint;
	}

	public void setPresentComplaint(String presentComplaint) {
		this.presentComplaint = presentComplaint;
	}

	public String getPresentComplaintHistory() {
		return presentComplaintHistory;
	}

	public void setPresentComplaintHistory(String presentComplaintHistory) {
		this.presentComplaintHistory = presentComplaintHistory;
	}

	public String getMenstrualHistory() {
		return menstrualHistory;
	}

	public void setMenstrualHistory(String menstrualHistory) {
		this.menstrualHistory = menstrualHistory;
	}

	public String getObstetricHistory() {
		return obstetricHistory;
	}

	public void setObstetricHistory(String obstetricHistory) {
		this.obstetricHistory = obstetricHistory;
	}

	public String getIndicationOfUSG() {
		return indicationOfUSG;
	}

	public void setIndicationOfUSG(String indicationOfUSG) {
		this.indicationOfUSG = indicationOfUSG;
	}

	public String getPv() {
		return pv;
	}

	public void setPv(String pv) {
		this.pv = pv;
	}

	public String getPa() {
		return pa;
	}

	public void setPa(String pa) {
		this.pa = pa;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	public String getEcgDetails() {
		return ecgDetails;
	}

	public void setEcgDetails(String ecgDetails) {
		this.ecgDetails = ecgDetails;
	}

	public String getxRayDetails() {
		return xRayDetails;
	}

	public void setxRayDetails(String xRayDetails) {
		this.xRayDetails = xRayDetails;
	}

	public String getEcho() {
		return echo;
	}

	public void setEcho(String echo) {
		this.echo = echo;
	}

	public String getHolter() {
		return holter;
	}

	public void setHolter(String holter) {
		this.holter = holter;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
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

	public boolean isInHistory() {
		return inHistory;
	}

	public void setInHistory(boolean inHistory) {
		this.inHistory = inHistory;
	}

	public VitalSigns getVitalSigns() {
		return vitalSigns;
	}

	public void setVitalSigns(VitalSigns vitalSigns) {
		this.vitalSigns = vitalSigns;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Appointment getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(Appointment appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	public String getProcedureNote() {
		return procedureNote;
	}

	public void setProcedureNote(String procedureNote) {
		this.procedureNote = procedureNote;
	}

	public String getPcNose() {
		return pcNose;
	}

	public String getPcOralCavity() {
		return pcOralCavity;
	}

	public String getPcThroat() {
		return pcThroat;
	}

	public String getPcEars() {
		return pcEars;
	}

	public String getNoseExam() {
		return noseExam;
	}

	public String getOralCavityThroatExam() {
		return oralCavityThroatExam;
	}

	public String getIndirectLarygoscopyExam() {
		return indirectLarygoscopyExam;
	}

	public String getNeckExam() {
		return neckExam;
	}

	public String getEarsExam() {
		return earsExam;
	}

	public void setPcNose(String pcNose) {
		this.pcNose = pcNose;
	}

	public void setPcOralCavity(String pcOralCavity) {
		this.pcOralCavity = pcOralCavity;
	}

	public void setPcThroat(String pcThroat) {
		this.pcThroat = pcThroat;
	}

	public void setPcEars(String pcEars) {
		this.pcEars = pcEars;
	}

	public void setNoseExam(String noseExam) {
		this.noseExam = noseExam;
	}

	public void setOralCavityThroatExam(String oralCavityThroatExam) {
		this.oralCavityThroatExam = oralCavityThroatExam;
	}

	public void setIndirectLarygoscopyExam(String indirectLarygoscopyExam) {
		this.indirectLarygoscopyExam = indirectLarygoscopyExam;
	}

	public void setNeckExam(String neckExam) {
		this.neckExam = neckExam;
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

	public String getFamilyHistory() {
		return familyHistory;
	}

	public void setFamilyHistory(String familyHistory) {
		this.familyHistory = familyHistory;
	}

	public String getPersonalHistoryTobacco() {
		return personalHistoryTobacco;
	}

	public void setPersonalHistoryTobacco(String personalHistoryTobacco) {
		this.personalHistoryTobacco = personalHistoryTobacco;
	}

	public String getPersonalHistoryAlcohol() {
		return personalHistoryAlcohol;
	}

	public void setPersonalHistoryAlcohol(String personalHistoryAlcohol) {
		this.personalHistoryAlcohol = personalHistoryAlcohol;
	}

	public String getPersonalHistorySmoking() {
		return personalHistorySmoking;
	}

	public void setPersonalHistorySmoking(String personalHistorySmoking) {
		this.personalHistorySmoking = personalHistorySmoking;
	}

	public String getPersonalHistoryDiet() {
		return personalHistoryDiet;
	}

	public void setPersonalHistoryDiet(String personalHistoryDiet) {
		this.personalHistoryDiet = personalHistoryDiet;
	}

	public String getPersonalHistoryOccupation() {
		return personalHistoryOccupation;
	}

	public void setPersonalHistoryOccupation(String personalHistoryOccupation) {
		this.personalHistoryOccupation = personalHistoryOccupation;
	}

	public String getGeneralHistoryDrugs() {
		return generalHistoryDrugs;
	}

	public void setGeneralHistoryDrugs(String generalHistoryDrugs) {
		this.generalHistoryDrugs = generalHistoryDrugs;
	}

	public String getGeneralHistoryMedicine() {
		return generalHistoryMedicine;
	}

	public void setGeneralHistoryMedicine(String generalHistoryMedicine) {
		this.generalHistoryMedicine = generalHistoryMedicine;
	}

	public String getGeneralHistoryAllergies() {
		return generalHistoryAllergies;
	}

	public void setGeneralHistoryAllergies(String generalHistoryAllergies) {
		this.generalHistoryAllergies = generalHistoryAllergies;
	}

	public String getGeneralHistorySurgical() {
		return generalHistorySurgical;
	}

	public void setGeneralHistorySurgical(String generalHistorySurgical) {
		this.generalHistorySurgical = generalHistorySurgical;
	}

	public String getPainScale() {
		return painScale;
	}

	public PatientCollection getPatient() {
		return patient;
	}

	public void setPatient(PatientCollection patient) {
		this.patient = patient;
	}

	public UserCollection getPatientUser() {
		return patientUser;
	}

	public void setPatientUser(UserCollection patientUser) {
		this.patientUser = patientUser;
	}

	public void setPainScale(String painScale) {
		this.painScale = painScale;
	}

	public String getPriorConsultations() {
		return priorConsultations;
	}

	public void setPriorConsultations(String priorConsultations) {
		this.priorConsultations = priorConsultations;
	}

	
	
	public EyeSpecialityObservation getEyeObservation() {
		return eyeObservation;
	}

	public void setEyeObservation(EyeSpecialityObservation eyeObservation) {
		this.eyeObservation = eyeObservation;
	}
	
	

	public PhysioExamination getPhysioExamination() {
		return physioExamination;
	}

	public void setPhysioExamination(PhysioExamination physioExamination) {
		this.physioExamination = physioExamination;
	}
	
	

	public List<TreatmentResponse> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<TreatmentResponse> treatments) {
		this.treatments = treatments;
	}
	
	

	public List<FieldsCollection> getTreatmentFields() {
		return treatmentFields;
	}

	public void setTreatmentFields(List<FieldsCollection> treatmentFields) {
		this.treatmentFields = treatmentFields;
	}

	public List<Observation> getTreatmentObservation() {
		return treatmentObservation;
	}

	public void setTreatmentObservation(List<Observation> treatmentObservation) {
		this.treatmentObservation = treatmentObservation;
	}

	@Override
	public String toString() {
		return "ClinicalnoteLookupBean [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", notes=" + notes
				+ ", observations=" + observations + ", investigations=" + investigations + ", diagnoses=" + diagnoses
				+ ", complaints=" + complaints + ", diagrams=" + diagrams + ", note=" + note + ", observation="
				+ observation + ", investigation=" + investigation + ", diagnosis=" + diagnosis
				+ ", provisionalDiagnosis=" + provisionalDiagnosis + ", generalExam=" + generalExam + ", systemExam="
				+ systemExam + ", complaint=" + complaint + ", presentComplaint=" + presentComplaint
				+ ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory=" + menstrualHistory
				+ ", obstetricHistory=" + obstetricHistory + ", indicationOfUSG=" + indicationOfUSG + ", procedureNote="
				+ procedureNote + ", pv=" + pv + ", pa=" + pa + ", ps=" + ps + ", ecgDetails=" + ecgDetails
				+ ", xRayDetails=" + xRayDetails + ", echo=" + echo + ", holter=" + holter + ", pcNose=" + pcNose
				+ ", pcOralCavity=" + pcOralCavity + ", pcThroat=" + pcThroat + ", pcEars=" + pcEars + ", noseExam="
				+ noseExam + ", oralCavityThroatExam=" + oralCavityThroatExam + ", indirectLarygoscopyExam="
				+ indirectLarygoscopyExam + ", neckExam=" + neckExam + ", earsExam=" + earsExam + ", comments="
				+ comments + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", discarded=" + discarded + ", inHistory=" + inHistory + ", vitalSigns="
				+ vitalSigns + ", appointmentId=" + appointmentId + ", time=" + time + ", fromDate=" + fromDate
				+ ", appointmentRequest=" + appointmentRequest + ", pastHistory=" + pastHistory + ", familyHistory="
				+ familyHistory + ", personalHistoryTobacco=" + personalHistoryTobacco + ", personalHistoryAlcohol="
				+ personalHistoryAlcohol + ", personalHistorySmoking=" + personalHistorySmoking
				+ ", personalHistoryDiet=" + personalHistoryDiet + ", personalHistoryOccupation="
				+ personalHistoryOccupation + ", generalHistoryDrugs=" + generalHistoryDrugs
				+ ", generalHistoryMedicine=" + generalHistoryMedicine + ", generalHistoryAllergies="
				+ generalHistoryAllergies + ", generalHistorySurgical=" + generalHistorySurgical + ", painScale="
				+ painScale + ", patient=" + patient + ", patientUser=" + patientUser + "]";
	}

}
