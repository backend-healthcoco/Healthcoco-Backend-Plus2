package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.ECGDetails;
import com.dpdocter.beans.EarsExamination;
import com.dpdocter.beans.Echo;
import com.dpdocter.beans.EyeSpecialityObservation;
import com.dpdocter.beans.GeneralExam;
import com.dpdocter.beans.Holter;
import com.dpdocter.beans.IndicationOfUSG;
import com.dpdocter.beans.IndirectLarygoscopyExamination;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.MenstrualHistory;
import com.dpdocter.beans.NeckExamination;
import com.dpdocter.beans.NoseExamination;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.ObstetricHistory;
import com.dpdocter.beans.OralCavityAndThroatExamination;
import com.dpdocter.beans.PV;
import com.dpdocter.beans.PhysioExamination;
import com.dpdocter.beans.PresentComplaint;
import com.dpdocter.beans.PresentComplaintHistory;
import com.dpdocter.beans.PresentingComplaintEars;
import com.dpdocter.beans.PresentingComplaintNose;
import com.dpdocter.beans.PresentingComplaintOralCavity;
import com.dpdocter.beans.PresentingComplaintThroat;
import com.dpdocter.beans.ProcedureNote;
import com.dpdocter.beans.ProvisionalDiagnosis;
import com.dpdocter.beans.SystemExam;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.XRayDetails;

public class ClinicalNotesAddRequest {
	private String id;

	private String patientId;

	private String observation;

	private String investigation;

	private String diagnosis;

	private String note;

	private List<String> diagrams;

	private String provisionalDiagnosis;

	private String generalExam;

	private String systemExam;

	private String complaint;

	private String presentComplaint;

	private String presentComplaintHistory;

	private String menstrualHistory;

	private String obstetricHistory;

	private String indicationOfUSG;

	private String pv;

	private String pa;

	private String ps;

	private String procedureNote;

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

	private String createdBy;

	private String visitId;

	private VitalSigns vitalSigns;

	private AppointmentRequest appointmentRequest;

	private String appointmentId;

	private WorkingHours time;

	private Date fromDate;

	private List<Diagnoses> diagnoses;

	private List<Notes> notes;

	private List<Complaint> complaints;

	private List<Observation> observations;

	private List<Investigation> investigations;

	private List<ProvisionalDiagnosis> provisionalDiagnoses;

	private List<PresentComplaint> presentComplaints;

	private List<PresentComplaintHistory> presentComplaintHistories;

	private List<GeneralExam> generalExams;

	private List<SystemExam> systemExams;

	private List<MenstrualHistory> menstrualHistories;

	private List<ObstetricHistory> obstetricHistories;

	private List<ProcedureNote> procedureNotes;

	private List<IndicationOfUSG> indicationOfUSGs;

	private List<PV> pvs;

	private List<ECGDetails> ecgDetailssecgDetails;

	private List<XRayDetails> XrayDetailss;

	private List<Echo> echos;

	private List<Holter> holters;

	private List<PresentingComplaintNose> presentingComplaintNotes;

	private List<PresentingComplaintOralCavity> presentingComplaintOralCavities;

	private List<PresentingComplaintThroat> presentingComplaintThroats;

	private List<PresentingComplaintEars> presentingComplaintEars;

	private List<NoseExamination> noseExaminations;

	private List<OralCavityAndThroatExamination> oralCavityAndThroatExaminations;

	private List<IndirectLarygoscopyExamination> indirectLarygoscopyExaminations;

	private List<NeckExamination> neckExaminations;

	private List<EarsExamination> earsExaminations;

	private String globalDiagnoses;

	private String globalNotes;

	private String globalComplaints;

	private String globalObservations;

	private String globalInvestigations;

	private String globalProvisionalDiagnoses;

	private String globalPresentComplaints;

	private String globalPresentComplaintHistories;

	private String globalGeneralExams;

	private String globalSystemExams;

	private String globalMenstrualHistories;

	private String globalObstetricHistories;

	private String globalIndicationOfUSGs;

	private String globalPVs;

	private String globalPAs;

	private String globalPSs;

	private String globalEcgDetails;

	private String globalXRayDetails;

	private String globalEchoes;

	private String globalHolters;

	private String globalProcedureNotes;

	private String globalPCNose;

	private String globalPCOralCavity;

	private String globalPCThroat;

	private String globalPCEars;

	private String globalNoseExam;

	private String globalOralCavityThroatExam;

	private String globalIndirectLarygoscopyExam;

	private String globalNeckExam;

	private String globalEarsExam;

	private Date lmp;

	private Date edd;

	private int noOfFemaleChildren;

	private int noOfMaleChildren;

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
	private Date createdTime;

	private String priorConsultations;

	private Boolean sendNotificationToDoctor = true;

	private EyeSpecialityObservation eyeObservation;

	private PhysioExamination physioExamination;

	private String vaccinationHistory;

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
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

	public String getGlobalPCNose() {
		return globalPCNose;
	}

	public void setGlobalPCNose(String globalPCNose) {
		this.globalPCNose = globalPCNose;
	}

	public String getGlobalPCOralCavity() {
		return globalPCOralCavity;
	}

	public void setGlobalPCOralCavity(String globalPCOralCavity) {
		this.globalPCOralCavity = globalPCOralCavity;
	}

	public String getGlobalPCThroat() {
		return globalPCThroat;
	}

	public void setGlobalPCThroat(String globalPCThroat) {
		this.globalPCThroat = globalPCThroat;
	}

	public String getGlobalPCEars() {
		return globalPCEars;
	}

	public void setGlobalPCEars(String globalPCEars) {
		this.globalPCEars = globalPCEars;
	}

	public String getGlobalNoseExam() {
		return globalNoseExam;
	}

	public void setGlobalNoseExam(String globalNoseExam) {
		this.globalNoseExam = globalNoseExam;
	}

	public String getGlobalOralCavityThroatExam() {
		return globalOralCavityThroatExam;
	}

	public void setGlobalOralCavityThroatExam(String globalOralCavityThroatExam) {
		this.globalOralCavityThroatExam = globalOralCavityThroatExam;
	}

	public String getGlobalIndirectLarygoscopyExam() {
		return globalIndirectLarygoscopyExam;
	}

	public void setGlobalIndirectLarygoscopyExam(String globalIndirectLarygoscopyExam) {
		this.globalIndirectLarygoscopyExam = globalIndirectLarygoscopyExam;
	}

	public String getGlobalNeckExam() {
		return globalNeckExam;
	}

	public void setGlobalNeckExam(String globalNeckExam) {
		this.globalNeckExam = globalNeckExam;
	}

	public String getGlobalEarsExam() {
		return globalEarsExam;
	}

	public void setGlobalEarsExam(String globalEarsExam) {
		this.globalEarsExam = globalEarsExam;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public List<String> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<String> diagrams) {
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
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

	public AppointmentRequest getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(AppointmentRequest appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
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

	public List<Diagnoses> getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(List<Diagnoses> diagnoses) {
		this.diagnoses = diagnoses;
	}

	public List<Notes> getNotes() {
		return notes;
	}

	public void setNotes(List<Notes> notes) {
		this.notes = notes;
	}

	public List<Complaint> getComplaints() {
		return complaints;
	}

	public void setComplaints(List<Complaint> complaints) {
		this.complaints = complaints;
	}

	public List<Observation> getObservations() {
		return observations;
	}

	public void setObservations(List<Observation> observations) {
		this.observations = observations;
	}

	public List<Investigation> getInvestigations() {
		return investigations;
	}

	public void setInvestigations(List<Investigation> investigations) {
		this.investigations = investigations;
	}

	public List<ProvisionalDiagnosis> getProvisionalDiagnoses() {
		return provisionalDiagnoses;
	}

	public void setProvisionalDiagnoses(List<ProvisionalDiagnosis> provisionalDiagnoses) {
		this.provisionalDiagnoses = provisionalDiagnoses;
	}

	public List<PresentComplaint> getPresentComplaints() {
		return presentComplaints;
	}

	public void setPresentComplaints(List<PresentComplaint> presentComplaints) {
		this.presentComplaints = presentComplaints;
	}

	public List<PresentComplaintHistory> getPresentComplaintHistories() {
		return presentComplaintHistories;
	}

	public void setPresentComplaintHistories(List<PresentComplaintHistory> presentComplaintHistories) {
		this.presentComplaintHistories = presentComplaintHistories;
	}

	public List<GeneralExam> getGeneralExams() {
		return generalExams;
	}

	public void setGeneralExams(List<GeneralExam> generalExams) {
		this.generalExams = generalExams;
	}

	public List<SystemExam> getSystemExams() {
		return systemExams;
	}

	public void setSystemExams(List<SystemExam> systemExams) {
		this.systemExams = systemExams;
	}

	public List<MenstrualHistory> getMenstrualHistories() {
		return menstrualHistories;
	}

	public void setMenstrualHistories(List<MenstrualHistory> menstrualHistories) {
		this.menstrualHistories = menstrualHistories;
	}

	public List<ObstetricHistory> getObstetricHistories() {
		return obstetricHistories;
	}

	public void setObstetricHistories(List<ObstetricHistory> obstetricHistories) {
		this.obstetricHistories = obstetricHistories;
	}

	public String getGlobalDiagnoses() {
		return globalDiagnoses;
	}

	public void setGlobalDiagnoses(String globalDiagnoses) {
		this.globalDiagnoses = globalDiagnoses;
	}

	public String getGlobalNotes() {
		return globalNotes;
	}

	public void setGlobalNotes(String globalNotes) {
		this.globalNotes = globalNotes;
	}

	public String getGlobalComplaints() {
		return globalComplaints;
	}

	public void setGlobalComplaints(String globalComplaints) {
		this.globalComplaints = globalComplaints;
	}

	public String getGlobalObservations() {
		return globalObservations;
	}

	public void setGlobalObservations(String globalObservations) {
		this.globalObservations = globalObservations;
	}

	public String getGlobalInvestigations() {
		return globalInvestigations;
	}

	public void setGlobalInvestigations(String globalInvestigations) {
		this.globalInvestigations = globalInvestigations;
	}

	public String getGlobalProvisionalDiagnoses() {
		return globalProvisionalDiagnoses;
	}

	public void setGlobalProvisionalDiagnoses(String globalProvisionalDiagnoses) {
		this.globalProvisionalDiagnoses = globalProvisionalDiagnoses;
	}

	public String getGlobalPresentComplaints() {
		return globalPresentComplaints;
	}

	public void setGlobalPresentComplaints(String globalPresentComplaints) {
		this.globalPresentComplaints = globalPresentComplaints;
	}

	public String getGlobalPresentComplaintHistories() {
		return globalPresentComplaintHistories;
	}

	public void setGlobalPresentComplaintHistories(String globalPresentComplaintHistories) {
		this.globalPresentComplaintHistories = globalPresentComplaintHistories;
	}

	public String getGlobalGeneralExams() {
		return globalGeneralExams;
	}

	public void setGlobalGeneralExams(String globalGeneralExams) {
		this.globalGeneralExams = globalGeneralExams;
	}

	public String getGlobalSystemExams() {
		return globalSystemExams;
	}

	public void setGlobalSystemExams(String globalSystemExams) {
		this.globalSystemExams = globalSystemExams;
	}

	public String getGlobalMenstrualHistories() {
		return globalMenstrualHistories;
	}

	public void setGlobalMenstrualHistories(String globalMenstrualHistories) {
		this.globalMenstrualHistories = globalMenstrualHistories;
	}

	public String getGlobalObstetricHistories() {
		return globalObstetricHistories;
	}

	public void setGlobalObstetricHistories(String globalObstetricHistories) {
		this.globalObstetricHistories = globalObstetricHistories;
	}

	public String getGlobalIndicationOfUSGs() {
		return globalIndicationOfUSGs;
	}

	public void setGlobalIndicationOfUSGs(String globalIndicationOfUSGs) {
		this.globalIndicationOfUSGs = globalIndicationOfUSGs;
	}

	public String getGlobalPVs() {
		return globalPVs;
	}

	public void setGlobalPVs(String globalPVs) {
		this.globalPVs = globalPVs;
	}

	public String getGlobalPAs() {
		return globalPAs;
	}

	public void setGlobalPAs(String globalPAs) {
		this.globalPAs = globalPAs;
	}

	public String getGlobalPSs() {
		return globalPSs;
	}

	public void setGlobalPSs(String globalPSs) {
		this.globalPSs = globalPSs;
	}

	public String getGlobalEcgDetails() {
		return globalEcgDetails;
	}

	public void setGlobalEcgDetails(String globalEcgDetails) {
		this.globalEcgDetails = globalEcgDetails;
	}

	public String getGlobalXRayDetails() {
		return globalXRayDetails;
	}

	public void setGlobalXRayDetails(String globalXRayDetails) {
		this.globalXRayDetails = globalXRayDetails;
	}

	public String getGlobalEchoes() {
		return globalEchoes;
	}

	public void setGlobalEchoes(String globalEchoes) {
		this.globalEchoes = globalEchoes;
	}

	public String getGlobalHolters() {
		return globalHolters;
	}

	public void setGlobalHolters(String globalHolters) {
		this.globalHolters = globalHolters;
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

	public String getProcedureNote() {
		return procedureNote;
	}

	public void setProcedureNote(String procedureNote) {
		this.procedureNote = procedureNote;
	}

	public List<ProcedureNote> getProcedureNotes() {
		return procedureNotes;
	}

	public void setProcedureNotes(List<ProcedureNote> procedureNotes) {
		this.procedureNotes = procedureNotes;
	}

	public List<IndicationOfUSG> getIndicationOfUSGs() {
		return indicationOfUSGs;
	}

	public void setIndicationOfUSGs(List<IndicationOfUSG> indicationOfUSGs) {
		this.indicationOfUSGs = indicationOfUSGs;
	}

	public List<PV> getPvs() {
		return pvs;
	}

	public void setPvs(List<PV> pvs) {
		this.pvs = pvs;
	}

	public List<ECGDetails> getEcgDetailssecgDetails() {
		return ecgDetailssecgDetails;
	}

	public void setEcgDetailssecgDetails(List<ECGDetails> ecgDetailssecgDetails) {
		this.ecgDetailssecgDetails = ecgDetailssecgDetails;
	}

	public List<XRayDetails> getXrayDetailss() {
		return XrayDetailss;
	}

	public void setXrayDetailss(List<XRayDetails> xrayDetailss) {
		XrayDetailss = xrayDetailss;
	}

	public List<Echo> getEchos() {
		return echos;
	}

	public void setEchos(List<Echo> echos) {
		this.echos = echos;
	}

	public List<Holter> getHolters() {
		return holters;
	}

	public void setHolters(List<Holter> holters) {
		this.holters = holters;
	}

	public String getGlobalProcedureNotes() {
		return globalProcedureNotes;
	}

	public void setGlobalProcedureNotes(String globalProcedureNotes) {
		this.globalProcedureNotes = globalProcedureNotes;
	}

	public List<PresentingComplaintNose> getPresentingComplaintNotes() {
		return presentingComplaintNotes;
	}

	public void setPresentingComplaintNotes(List<PresentingComplaintNose> presentingComplaintNotes) {
		this.presentingComplaintNotes = presentingComplaintNotes;
	}

	public List<PresentingComplaintOralCavity> getPresentingComplaintOralCavities() {
		return presentingComplaintOralCavities;
	}

	public void setPresentingComplaintOralCavities(
			List<PresentingComplaintOralCavity> presentingComplaintOralCavities) {
		this.presentingComplaintOralCavities = presentingComplaintOralCavities;
	}

	public List<PresentingComplaintThroat> getPresentingComplaintThroats() {
		return presentingComplaintThroats;
	}

	public void setPresentingComplaintThroats(List<PresentingComplaintThroat> presentingComplaintThroats) {
		this.presentingComplaintThroats = presentingComplaintThroats;
	}

	public List<PresentingComplaintEars> getPresentingComplaintEars() {
		return presentingComplaintEars;
	}

	public void setPresentingComplaintEars(List<PresentingComplaintEars> presentingComplaintEars) {
		this.presentingComplaintEars = presentingComplaintEars;
	}

	public List<NoseExamination> getNoseExaminations() {
		return noseExaminations;
	}

	public void setNoseExaminations(List<NoseExamination> noseExaminations) {
		this.noseExaminations = noseExaminations;
	}

	public List<OralCavityAndThroatExamination> getOralCavityAndThroatExaminations() {
		return oralCavityAndThroatExaminations;
	}

	public void setOralCavityAndThroatExaminations(
			List<OralCavityAndThroatExamination> oralCavityAndThroatExaminations) {
		this.oralCavityAndThroatExaminations = oralCavityAndThroatExaminations;
	}

	public List<IndirectLarygoscopyExamination> getIndirectLarygoscopyExaminations() {
		return indirectLarygoscopyExaminations;
	}

	public void setIndirectLarygoscopyExaminations(
			List<IndirectLarygoscopyExamination> indirectLarygoscopyExaminations) {
		this.indirectLarygoscopyExaminations = indirectLarygoscopyExaminations;
	}

	public List<NeckExamination> getNeckExaminations() {
		return neckExaminations;
	}

	public void setNeckExaminations(List<NeckExamination> neckExaminations) {
		this.neckExaminations = neckExaminations;
	}

	public List<EarsExamination> getEarsExaminations() {
		return earsExaminations;
	}

	public void setEarsExaminations(List<EarsExamination> earsExaminations) {
		this.earsExaminations = earsExaminations;
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

	public String getPriorConsultations() {
		return priorConsultations;
	}

	public void setPriorConsultations(String priorConsultations) {
		this.priorConsultations = priorConsultations;
	}

	public Boolean getSendNotificationToDoctor() {
		return sendNotificationToDoctor;
	}

	public void setSendNotificationToDoctor(Boolean sendNotificationToDoctor) {
		this.sendNotificationToDoctor = sendNotificationToDoctor;
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

//	public TreatmentObservationRequest getTreatmentObservation() {
//		return treatmentObservation;
//	}
//
//	public void setTreatmentObservation(TreatmentObservationRequest treatmentObservation) {
//		this.treatmentObservation = treatmentObservation;
//	}

	public String getVaccinationHistory() {
		return vaccinationHistory;
	}

	public void setVaccinationHistory(String vaccinationHistory) {
		this.vaccinationHistory = vaccinationHistory;
	}

	@Override
	public String toString() {
		return "ClinicalNotesAddRequest [id=" + id + ", patientId=" + patientId + ", observation=" + observation
				+ ", investigation=" + investigation + ", diagnosis=" + diagnosis + ", note=" + note + ", diagrams="
				+ diagrams + ", provisionalDiagnosis=" + provisionalDiagnosis + ", generalExam=" + generalExam
				+ ", systemExam=" + systemExam + ", complaint=" + complaint + ", presentComplaint=" + presentComplaint
				+ ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory=" + menstrualHistory
				+ ", obstetricHistory=" + obstetricHistory + ", indicationOfUSG=" + indicationOfUSG + ", pv=" + pv
				+ ", pa=" + pa + ", ps=" + ps + ", procedureNote=" + procedureNote + ", ecgDetails=" + ecgDetails
				+ ", xRayDetails=" + xRayDetails + ", echo=" + echo + ", holter=" + holter + ", pcNose=" + pcNose
				+ ", pcOralCavity=" + pcOralCavity + ", pcThroat=" + pcThroat + ", pcEars=" + pcEars + ", noseExam="
				+ noseExam + ", oralCavityThroatExam=" + oralCavityThroatExam + ", indirectLarygoscopyExam="
				+ indirectLarygoscopyExam + ", neckExam=" + neckExam + ", earsExam=" + earsExam + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", createdBy=" + createdBy
				+ ", visitId=" + visitId + ", vitalSigns=" + vitalSigns + ", appointmentRequest=" + appointmentRequest
				+ ", appointmentId=" + appointmentId + ", time=" + time + ", fromDate=" + fromDate + ", diagnoses="
				+ diagnoses + ", notes=" + notes + ", complaints=" + complaints + ", observations=" + observations
				+ ", investigations=" + investigations + ", provisionalDiagnoses=" + provisionalDiagnoses
				+ ", presentComplaints=" + presentComplaints + ", presentComplaintHistories="
				+ presentComplaintHistories + ", generalExams=" + generalExams + ", systemExams=" + systemExams
				+ ", menstrualHistories=" + menstrualHistories + ", obstetricHistories=" + obstetricHistories
				+ ", procedureNotes=" + procedureNotes + ", indicationOfUSGs=" + indicationOfUSGs + ", pvs=" + pvs
				+ ", ecgDetailssecgDetails=" + ecgDetailssecgDetails + ", XrayDetailss=" + XrayDetailss + ", echos="
				+ echos + ", holters=" + holters + ", presentingComplaintNotes=" + presentingComplaintNotes
				+ ", presentingComplaintOralCavities=" + presentingComplaintOralCavities
				+ ", presentingComplaintThroats=" + presentingComplaintThroats + ", presentingComplaintEars="
				+ presentingComplaintEars + ", noseExaminations=" + noseExaminations
				+ ", oralCavityAndThroatExaminations=" + oralCavityAndThroatExaminations
				+ ", indirectLarygoscopyExaminations=" + indirectLarygoscopyExaminations + ", neckExaminations="
				+ neckExaminations + ", earsExaminations=" + earsExaminations + ", globalDiagnoses=" + globalDiagnoses
				+ ", globalNotes=" + globalNotes + ", globalComplaints=" + globalComplaints + ", globalObservations="
				+ globalObservations + ", globalInvestigations=" + globalInvestigations
				+ ", globalProvisionalDiagnoses=" + globalProvisionalDiagnoses + ", globalPresentComplaints="
				+ globalPresentComplaints + ", globalPresentComplaintHistories=" + globalPresentComplaintHistories
				+ ", globalGeneralExams=" + globalGeneralExams + ", globalSystemExams=" + globalSystemExams
				+ ", globalMenstrualHistories=" + globalMenstrualHistories + ", globalObstetricHistories="
				+ globalObstetricHistories + ", globalIndicationOfUSGs=" + globalIndicationOfUSGs + ", globalPVs="
				+ globalPVs + ", globalPAs=" + globalPAs + ", globalPSs=" + globalPSs + ", globalEcgDetails="
				+ globalEcgDetails + ", globalXRayDetails=" + globalXRayDetails + ", globalEchoes=" + globalEchoes
				+ ", globalHolters=" + globalHolters + ", globalProcedureNotes=" + globalProcedureNotes
				+ ", globalPCNose=" + globalPCNose + ", globalPCOralCavity=" + globalPCOralCavity + ", globalPCThroat="
				+ globalPCThroat + ", globalPCEars=" + globalPCEars + ", globalNoseExam=" + globalNoseExam
				+ ", globalOralCavityThroatExam=" + globalOralCavityThroatExam + ", globalIndirectLarygoscopyExam="
				+ globalIndirectLarygoscopyExam + ", globalNeckExam=" + globalNeckExam + ", globalEarsExam="
				+ globalEarsExam + ", lmp=" + lmp + ", edd=" + edd + ", noOfFemaleChildren=" + noOfFemaleChildren
				+ ", noOfMaleChildren=" + noOfMaleChildren + ", pastHistory=" + pastHistory + ", familyHistory="
				+ familyHistory + ", personalHistoryTobacco=" + personalHistoryTobacco + ", personalHistoryAlcohol="
				+ personalHistoryAlcohol + ", personalHistorySmoking=" + personalHistorySmoking
				+ ", personalHistoryDiet=" + personalHistoryDiet + ", personalHistoryOccupation="
				+ personalHistoryOccupation + ", generalHistoryDrugs=" + generalHistoryDrugs
				+ ", generalHistoryMedicine=" + generalHistoryMedicine + ", generalHistoryAllergies="
				+ generalHistoryAllergies + ", generalHistorySurgical=" + generalHistorySurgical + ", painScale="
				+ painScale + ", createdTime=" + createdTime + ", sendNotificationToDoctor=" + sendNotificationToDoctor
				+ "]";
	}
}
