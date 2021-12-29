package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.PatientTreatmentResponse;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicalNotes extends GenericCollection {

	private String id;

	private String uniqueEmrId;

	// private List<Diagnoses> diagnoses;

	private List<ObjectId> diagramIds;

	private List<Diagram> diagrams;

	private String note;

	private String observation;

	private String investigation;

	private String diagnosis;

	private String provisionalDiagnosis;

	private String generalExam;

	private String systemExam;

	private String complaint;

	private String presentComplaint;

	private String procedureNote;

	private String presentComplaintHistory;

	private String menstrualHistory;

	private String obstetricHistory;

	private String indicationOfUSG;

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

	private String doctorId;

	private String locationId;

	private String hospitalId;

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

	private boolean inHistory = false;

	private Boolean discarded = false;

	private String visitId;

	private String patientId;

	private VitalSigns vitalSigns;

	private String appointmentId;

	private Appointment appointmentRequest;

	private WorkingHours time;

	private Date fromDate;

	private Date lmp;

	private Date edd;

	private int noOfFemaleChildren;

	private int noOfMaleChildren;
	
	private EyeSpecialityObservation eyeObservation;
	
	private PhysioExamination physioExamination;
	
	private TreatmentObservation TreatmentObservation;
	private String vaccinationHistory;

	

	public String getVaccinationHistory() {
		return vaccinationHistory;
	}

	public void setVaccinationHistory(String vaccinationHistory) {
		this.vaccinationHistory = vaccinationHistory;
	}

	public Appointment getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(Appointment appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// public List<Diagnoses> getDiagnoses() {
	// return diagnoses;
	// }
	//
	// public void setDiagnoses(List<Diagnoses> diagnoses) {
	// this.diagnoses = diagnoses;
	// }

	public List<Diagram> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<Diagram> diagrams) {
		this.diagrams = diagrams;
	}

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

	public boolean isInHistory() {
		return inHistory;
	}

	public void setInHistory(boolean inHistory) {
		this.inHistory = inHistory;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public String getPcNose() {
		return pcNose;
	}

	public void setPcNose(String pcNose) {
		this.pcNose = pcNose;
	}

	public String getProcedureNote() {
		return procedureNote;
	}

	public void setProcedureNote(String procedureNote) {
		this.procedureNote = procedureNote;
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

	public List<ObjectId> getDiagramIds() {
		return diagramIds;
	}

	public void setDiagramIds(List<ObjectId> diagramIds) {
		this.diagramIds = diagramIds;
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
	


	

	public TreatmentObservation getTreatmentObservation() {
		return TreatmentObservation;
	}

	public void setTreatmentObservation(TreatmentObservation treatmentObservation) {
		TreatmentObservation = treatmentObservation;
	}

	public PhysioExamination getPhysioExamination() {
		return physioExamination;
	}

	public void setPhysioExamination(PhysioExamination physioExamination) {
		this.physioExamination = physioExamination;
	}

	@Override
	public String toString() {
		return "ClinicalNotes [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", diagramIds=" + diagramIds + ", diagrams="
				+ diagrams + ", note=" + note + ", observation=" + observation + ", investigation=" + investigation
				+ ", diagnosis=" + diagnosis + ", provisionalDiagnosis=" + provisionalDiagnosis + ", generalExam="
				+ generalExam + ", systemExam=" + systemExam + ", complaint=" + complaint + ", presentComplaint="
				+ presentComplaint + ", procedureNote=" + procedureNote + ", presentComplaintHistory="
				+ presentComplaintHistory + ", menstrualHistory=" + menstrualHistory + ", obstetricHistory="
				+ obstetricHistory + ", indicationOfUSG=" + indicationOfUSG + ", pv=" + pv + ", pa=" + pa + ", ps=" + ps
				+ ", ecgDetails=" + ecgDetails + ", xRayDetails=" + xRayDetails + ", echo=" + echo + ", holter="
				+ holter + ", pcNose=" + pcNose + ", pcOralCavity=" + pcOralCavity + ", pcThroat=" + pcThroat
				+ ", pcEars=" + pcEars + ", noseExam=" + noseExam + ", oralCavityThroatExam=" + oralCavityThroatExam
				+ ", indirectLarygoscopyExam=" + indirectLarygoscopyExam + ", neckExam=" + neckExam + ", earsExam="
				+ earsExam + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", pastHistory=" + pastHistory + ", familyHistory=" + familyHistory + ", personalHistoryTobacco="
				+ personalHistoryTobacco + ", personalHistoryAlcohol=" + personalHistoryAlcohol
				+ ", personalHistorySmoking=" + personalHistorySmoking + ", personalHistoryDiet=" + personalHistoryDiet
				+ ", personalHistoryOccupation=" + personalHistoryOccupation + ", generalHistoryDrugs="
				+ generalHistoryDrugs + ", generalHistoryMedicine=" + generalHistoryMedicine
				+ ", generalHistoryAllergies=" + generalHistoryAllergies + ", generalHistorySurgical="
				+ generalHistorySurgical + ", painScale=" + painScale + ", inHistory=" + inHistory + ", discarded="
				+ discarded + ", visitId=" + visitId + ", patientId=" + patientId + ", vitalSigns=" + vitalSigns
				+ ", appointmentId=" + appointmentId + ", appointmentRequest=" + appointmentRequest + ", time=" + time
				+ ", fromDate=" + fromDate + ", lmp=" + lmp + ", edd=" + edd + ", noOfFemaleChildren="
				+ noOfFemaleChildren + ", noOfMaleChildren=" + noOfMaleChildren + "]";
	}
}
