package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.ClinicalNotesDiagnosis;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;

public class ClinicalNotesAddRequest {
	private String id;

	private String patientId;

    private String note;

    private String observation;

    private String investigation;

    private String diagnosis;

    private String complaint;

	private List<ClinicalNotesDiagnosis> diagnoses;

	private List<String> diagrams;

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

	public List<ClinicalNotesDiagnosis> getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(List<ClinicalNotesDiagnosis> diagnoses) {
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

	@Override
	public String toString() {
		return "ClinicalNotesAddRequest [id=" + id + ", patientId=" + patientId + ", note=" + note + ", observation="
				+ observation + ", investigation=" + investigation + ", diagnosis=" + diagnosis + ", complaint="
				+ complaint + ", diagnoses=" + diagnoses + ", diagrams=" + diagrams
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", createdBy=" + createdBy + ", visitId=" + visitId + ", vitalSigns=" + vitalSigns
				+ ", appointmentRequest=" + appointmentRequest + ", appointmentId=" + appointmentId + ", time=" + time
				+ ", fromDate=" + fromDate + "]";
	}
}
