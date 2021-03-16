package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.EyeSpecialityObservation;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.PhysioExamination;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;

@Document(collection = "clinical_notes_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class ClinicalNotesCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String uniqueEmrId;

	@Field
	private List<ObjectId> notes;

	@Field
	private List<ObjectId> observations;

	@Field
	private List<ObjectId> investigations;

	@Field
	private List<ObjectId> diagnoses;

	@Field
	private List<ObjectId> complaints;

	@Field
	private List<ObjectId> diagrams;

	@Field
	private String note;

	@Field
	private String observation;

	@Field
	private String investigation;

	@Field
	private String diagnosis;

	@Field
	private String provisionalDiagnosis;

	@Field
	private String generalExam;

	@Field
	private String systemExam;

	@Field
	private String complaint;

	@Field
	private String presentComplaint;

	@Field
	private String presentComplaintHistory;

	@Field
	private String menstrualHistory;

	@Field
	private String obstetricHistory;

	@Field
	private String indicationOfUSG;

	@Field
	private String pv;

	@Field
	private String pa;

	@Field
	private String ps;

	@Field
	private String ecgDetails;

	@Field
	private String xRayDetails;

	@Field
	private String echo;

	@Field
	private String holter;

	@Field
	private String pcNose;

	@Field
	private String pcOralCavity;

	@Field
	private String pcThroat;

	@Field
	private String pcEars;

	@Field
	private String noseExam;

	@Field
	private String oralCavityThroatExam;

	@Field
	private String indirectLarygoscopyExam;

	@Field
	private String neckExam;

	@Field
	private String earsExam;

	@Field
	private List<String> comments;

	@Indexed
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Indexed
	private ObjectId patientId;

	@Field
	private Boolean discarded = false;

	@Field
	private boolean inHistory = false;

	@Field
	private VitalSigns vitalSigns;

	@Field
	private String appointmentId;

	@Field
	private WorkingHours time;

	@Field
	private Date fromDate;

	@Field
	private Date lmp;

	@Field
	private Date edd;

	@Field
	private int noOfFemaleChildren;

	@Field
	private int noOfMaleChildren;

	@Field
	private String procedureNote;

	@Field
	private String pastHistory;
	@Field
	private String familyHistory;
	@Field
	private String personalHistoryTobacco;
	@Field
	private String personalHistoryAlcohol;
	@Field
	private String personalHistorySmoking;
	@Field
	private String personalHistoryDiet;
	@Field
	private String personalHistoryOccupation;
	@Field
	private String generalHistoryDrugs;
	@Field
	private String generalHistoryMedicine;
	@Field
	private String generalHistoryAllergies;
	@Field
	private String generalHistorySurgical;
	@Field
	private String painScale;
	@Field
	private String priorConsultations;

	@Field
	private Boolean isPatientDiscarded = false;
	
	@Field
	private EyeSpecialityObservation eyeObservation;
	
	@Field
	private PhysioExamination physioExamination;
	
	
	//  @Field private List<Observation> treatmentObservation;
	 

	public String getProcedureNote() {
		return procedureNote;
	}

	public void setProcedureNote(String procedureNote) {
		this.procedureNote = procedureNote;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
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

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
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

	public Date getLmp() {
		return lmp;
	}

	public void setLmp(Date lmp) {
		this.lmp = lmp;
	}

	public Date getEdd() {
		return edd;
	}

	public void setEdd(Date edd) {
		this.edd = edd;
	}

	public int getNoOfFemaleChildren() {
		return noOfFemaleChildren;
	}

	public void setNoOfFemaleChildren(int noOfFemaleChildren) {
		this.noOfFemaleChildren = noOfFemaleChildren;
	}

	public int getNoOfMaleChildren() {
		return noOfMaleChildren;
	}

	public void setNoOfMaleChildren(int noOfMaleChildren) {
		this.noOfMaleChildren = noOfMaleChildren;
	}

	public String getPcNose() {
		return pcNose;
	}

	public void setPcNose(String pcNose) {
		this.pcNose = pcNose;
	}

	public String getPcOralCavity() {
		return pcOralCavity;
	}

	public void setPcOralCavity(String pcOralCavity) {
		this.pcOralCavity = pcOralCavity;
	}

	public String getPcThroat() {
		return pcThroat;
	}

	public void setPcThroat(String pcThroat) {
		this.pcThroat = pcThroat;
	}

	public String getPcEars() {
		return pcEars;
	}

	public void setPcEars(String pcEars) {
		this.pcEars = pcEars;
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

	public String getIndirectLarygoscopyExam() {
		return indirectLarygoscopyExam;
	}

	public void setIndirectLarygoscopyExam(String indirectLarygoscopyExam) {
		this.indirectLarygoscopyExam = indirectLarygoscopyExam;
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

	public void setPainScale(String painScale) {
		this.painScale = painScale;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public String getPriorConsultations() {
		return priorConsultations;
	}

	public void setPriorConsultations(String priorConsultations) {
		this.priorConsultations = priorConsultations;
	}
	
	

	

//	public List<Observation> getTreatmentObservation() {
//		return treatmentObservation;
//	}
//
//	public void setTreatmentObservation(List<Observation> treatmentObservation) {
//		this.treatmentObservation = treatmentObservation;
//	}

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

	@Override
	public String toString() {
		return "ClinicalNotesCollection [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", notes=" + notes
				+ ", observations=" + observations + ", investigations=" + investigations + ", diagnoses=" + diagnoses
				+ ", complaints=" + complaints + ", diagrams=" + diagrams + ", note=" + note + ", observation="
				+ observation + ", investigation=" + investigation + ", diagnosis=" + diagnosis
				+ ", provisionalDiagnosis=" + provisionalDiagnosis + ", generalExam=" + generalExam + ", systemExam="
				+ systemExam + ", complaint=" + complaint + ", presentComplaint=" + presentComplaint
				+ ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory=" + menstrualHistory
				+ ", obstetricHistory=" + obstetricHistory + ", indicationOfUSG=" + indicationOfUSG + ", pv=" + pv
				+ ", pa=" + pa + ", ps=" + ps + ", ecgDetails=" + ecgDetails + ", xRayDetails=" + xRayDetails
				+ ", echo=" + echo + ", holter=" + holter + ", pcNose=" + pcNose + ", pcOralCavity=" + pcOralCavity
				+ ", pcThroat=" + pcThroat + ", pcEars=" + pcEars + ", noseExam=" + noseExam + ", oralCavityThroatExam="
				+ oralCavityThroatExam + ", indirectLarygoscopyExam=" + indirectLarygoscopyExam + ", neckExam="
				+ neckExam + ", earsExam=" + earsExam + ", comments=" + comments + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
				+ ", discarded=" + discarded + ", inHistory=" + inHistory + ", vitalSigns=" + vitalSigns
				+ ", appointmentId=" + appointmentId + ", time=" + time + ", fromDate=" + fromDate + ", lmp=" + lmp
				+ ", edd=" + edd + ", noOfFemaleChildren=" + noOfFemaleChildren + ", noOfMaleChildren="
				+ noOfMaleChildren + ", procedureNote=" + procedureNote + ", pastHistory=" + pastHistory
				+ ", familyHistory=" + familyHistory + ", personalHistoryTobacco=" + personalHistoryTobacco
				+ ", personalHistoryAlcohol=" + personalHistoryAlcohol + ", personalHistorySmoking="
				+ personalHistorySmoking + ", personalHistoryDiet=" + personalHistoryDiet
				+ ", personalHistoryOccupation=" + personalHistoryOccupation + ", generalHistoryDrugs="
				+ generalHistoryDrugs + ", generalHistoryMedicine=" + generalHistoryMedicine
				+ ", generalHistoryAllergies=" + generalHistoryAllergies + ", generalHistorySurgical="
				+ generalHistorySurgical + ", painScale=" + painScale + ", isPatientDiscarded=" + isPatientDiscarded
				+ "]";
	}

}
