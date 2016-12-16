package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.GeneralExam;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.MenstrualHistory;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.ObstetricHistory;
import com.dpdocter.beans.PresentComplaint;
import com.dpdocter.beans.PresentComplaintHistory;
import com.dpdocter.beans.ProvisionalDiagnosis;
import com.dpdocter.beans.SystemExam;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;

public class ClinicalNotesAddRequest {
	private String id;

	private String patientId;

	private List<Diagnoses> diagnoses;

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

	public List<Diagnoses> getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(List<Diagnoses> diagnoses) {
		this.diagnoses = diagnoses;
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

	@Override
	public String toString() {
		return "ClinicalNotesAddRequest [id=" + id + ", patientId=" + patientId + ", diagnoses=" + diagnoses
				+ ", diagrams=" + diagrams + ", note=" + note + ", observation=" + observation + ", investigation="
				+ investigation + ", diagnosis=" + diagnosis + ", provisionalDiagnosis=" + provisionalDiagnosis
				+ ", generalExam=" + generalExam + ", systemExam=" + systemExam + ", complaint=" + complaint
				+ ", presentComplaint=" + presentComplaint + ", presentComplaintHistory=" + presentComplaintHistory
				+ ", menstrualHistory=" + menstrualHistory + ", obstetricHistory=" + obstetricHistory + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", createdBy=" + createdBy
				+ ", visitId=" + visitId + ", vitalSigns=" + vitalSigns + ", appointmentRequest=" + appointmentRequest
				+ ", appointmentId=" + appointmentId + ", time=" + time + ", fromDate=" + fromDate + ", notes=" + notes
				+ ", complaints=" + complaints + ", observations=" + observations + ", investigations=" + investigations
				+ ", provisionalDiagnoses=" + provisionalDiagnoses + ", presentComplaints=" + presentComplaints
				+ ", presentComplaintHistories=" + presentComplaintHistories + ", generalExams=" + generalExams
				+ ", systemExams=" + systemExams + ", menstrualHistories=" + menstrualHistories
				+ ", obstetricHistories=" + obstetricHistories + "]";
	}
}
