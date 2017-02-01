package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.IndicationOfUSG;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;

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

	private String ecgDetail;

	private String xRayDetail;

	private String echo;

	private String holter;

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

	private String diagnoses;

	private String notes;

	private String complaints;

	private String observations;

	private String investigations;

	private String provisionalDiagnoses;

	private String presentComplaints;

	private String presentComplaintHistories;

	private String generalExams;

	private String systemExams;

	private String menstrualHistories;

	private String obstetricHistories;

	private String indicationOfUSGs;

	private String pvs;

	private String pas;

	private String pss;

	private String ecgDetails;

	private String xRayDetails;

	private String echoes;

	private String holters;

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

	public String getEcgDetail() {
		return ecgDetail;
	}

	public void setEcgDetail(String ecgDetail) {
		this.ecgDetail = ecgDetail;
	}

	public String getxRayDetail() {
		return xRayDetail;
	}

	public void setxRayDetail(String xRayDetail) {
		this.xRayDetail = xRayDetail;
	}

	public String getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(String diagnoses) {
		this.diagnoses = diagnoses;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getComplaints() {
		return complaints;
	}

	public void setComplaints(String complaints) {
		this.complaints = complaints;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getInvestigations() {
		return investigations;
	}

	public void setInvestigations(String investigations) {
		this.investigations = investigations;
	}

	public String getProvisionalDiagnoses() {
		return provisionalDiagnoses;
	}

	public void setProvisionalDiagnoses(String provisionalDiagnoses) {
		this.provisionalDiagnoses = provisionalDiagnoses;
	}

	public String getPresentComplaints() {
		return presentComplaints;
	}

	public void setPresentComplaints(String presentComplaints) {
		this.presentComplaints = presentComplaints;
	}

	public String getPresentComplaintHistories() {
		return presentComplaintHistories;
	}

	public void setPresentComplaintHistories(String presentComplaintHistories) {
		this.presentComplaintHistories = presentComplaintHistories;
	}

	public String getGeneralExams() {
		return generalExams;
	}

	public void setGeneralExams(String generalExams) {
		this.generalExams = generalExams;
	}

	public String getSystemExams() {
		return systemExams;
	}

	public void setSystemExams(String systemExams) {
		this.systemExams = systemExams;
	}

	public String getMenstrualHistories() {
		return menstrualHistories;
	}

	public void setMenstrualHistories(String menstrualHistories) {
		this.menstrualHistories = menstrualHistories;
	}

	public String getObstetricHistories() {
		return obstetricHistories;
	}

	public void setObstetricHistories(String obstetricHistories) {
		this.obstetricHistories = obstetricHistories;
	}

	public String getIndicationOfUSGs() {
		return indicationOfUSGs;
	}

	public void setIndicationOfUSGs(String indicationOfUSGs) {
		this.indicationOfUSGs = indicationOfUSGs;
	}

	public String getPvs() {
		return pvs;
	}

	public void setPvs(String pvs) {
		this.pvs = pvs;
	}

	public String getPas() {
		return pas;
	}

	public void setPas(String pas) {
		this.pas = pas;
	}

	public String getPss() {
		return pss;
	}

	public void setPss(String pss) {
		this.pss = pss;
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

	public String getEchoes() {
		return echoes;
	}

	public void setEchoes(String echoes) {
		this.echoes = echoes;
	}

	public String getHolters() {
		return holters;
	}

	public void setHolters(String holters) {
		this.holters = holters;
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

	@Override
	public String toString() {
		return "ClinicalNotesAddRequest [id=" + id + ", patientId=" + patientId + ", observation=" + observation
				+ ", investigation=" + investigation + ", diagnosis=" + diagnosis + ", note=" + note + ", diagrams="
				+ diagrams + ", provisionalDiagnosis=" + provisionalDiagnosis + ", generalExam=" + generalExam
				+ ", systemExam=" + systemExam + ", complaint=" + complaint + ", presentComplaint=" + presentComplaint
				+ ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory=" + menstrualHistory
				+ ", obstetricHistory=" + obstetricHistory + ", indicationOfUSG=" + indicationOfUSG + ", pv=" + pv
				+ ", pa=" + pa + ", ps=" + ps + ", ecgDetail=" + ecgDetail + ", xRayDetail=" + xRayDetail + ", echo="
				+ echo + ", holter=" + holter + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", createdBy=" + createdBy + ", visitId=" + visitId + ", vitalSigns="
				+ vitalSigns + ", appointmentRequest=" + appointmentRequest + ", appointmentId=" + appointmentId
				+ ", time=" + time + ", fromDate=" + fromDate + ", diagnoses=" + diagnoses + ", notes=" + notes
				+ ", complaints=" + complaints + ", observations=" + observations + ", investigations=" + investigations
				+ ", provisionalDiagnoses=" + provisionalDiagnoses + ", presentComplaints=" + presentComplaints
				+ ", presentComplaintHistories=" + presentComplaintHistories + ", generalExams=" + generalExams
				+ ", systemExams=" + systemExams + ", menstrualHistories=" + menstrualHistories
				+ ", obstetricHistories=" + obstetricHistories + ", indicationOfUSGs=" + indicationOfUSGs + ", pvs="
				+ pvs + ", pas=" + pas + ", pss=" + pss + ", ecgDetails=" + ecgDetails + ", xRayDetails=" + xRayDetails
				+ ", echoes=" + echoes + ", holters=" + holters + ", globalDiagnoses=" + globalDiagnoses
				+ ", globalNotes=" + globalNotes + ", globalComplaints=" + globalComplaints + ", globalObservations="
				+ globalObservations + ", globalInvestigations=" + globalInvestigations
				+ ", globalProvisionalDiagnoses=" + globalProvisionalDiagnoses + ", globalPresentComplaints="
				+ globalPresentComplaints + ", globalPresentComplaintHistories=" + globalPresentComplaintHistories
				+ ", globalGeneralExams=" + globalGeneralExams + ", globalSystemExams=" + globalSystemExams
				+ ", globalMenstrualHistories=" + globalMenstrualHistories + ", globalObstetricHistories="
				+ globalObstetricHistories + ", globalIndicationOfUSGs=" + globalIndicationOfUSGs + ", globalPVs="
				+ globalPVs + ", globalPAs=" + globalPAs + ", globalPSs=" + globalPSs + ", globalEcgDetails="
				+ globalEcgDetails + ", globalXRayDetails=" + globalXRayDetails + ", globalEchoes=" + globalEchoes
				+ ", globalHolters=" + globalHolters + "]";
	}
}
