package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.EyePrescription;
import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.enums.VisitedFor;

public class PatientVisitResponse extends GenericCollection {

	private String id;

	private String uniqueEmrId;

	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Date visitedTime;

	private List<VisitedFor> visitedFor;

	private List<Prescription> prescriptions;

	private List<ClinicalNotes> clinicalNotes;

	private List<Diagram> clinicalNotesDiagrams;

	private List<PatientTreatment> patientTreatment;

	private List<Records> records;

	private String appointmentId;

	private Appointment appointmentRequest;

	private WorkingHours time;

	private Date fromDate;

	private EyePrescription eyePrescription;

	private Boolean discarded = false;

	private PatientCollection patient;

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

	public Date getVisitedTime() {
		return visitedTime;
	}

	public void setVisitedTime(Date visitedTime) {
		this.visitedTime = visitedTime;
	}

	public List<VisitedFor> getVisitedFor() {
		return visitedFor;
	}

	public void setVisitedFor(List<VisitedFor> visitedFor) {
		this.visitedFor = visitedFor;
	}

	public List<Prescription> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(List<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

	public List<ClinicalNotes> getClinicalNotes() {
		return clinicalNotes;
	}

	public void setClinicalNotes(List<ClinicalNotes> clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}

	public List<PatientTreatment> getPatientTreatment() {
		return patientTreatment;
	}

	public void setPatientTreatment(List<PatientTreatment> patientTreatment) {
		this.patientTreatment = patientTreatment;
	}

	public List<Records> getRecords() {
		return records;
	}

	public void setRecords(List<Records> records) {
		this.records = records;
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

	public EyePrescription getEyePrescription() {
		return eyePrescription;
	}

	public void setEyePrescription(EyePrescription eyePrescription) {
		this.eyePrescription = eyePrescription;
	}

	public List<Diagram> getClinicalNotesDiagrams() {
		return clinicalNotesDiagrams;
	}

	public void setClinicalNotesDiagrams(List<Diagram> clinicalNotesDiagrams) {
		this.clinicalNotesDiagrams = clinicalNotesDiagrams;
	}

	public Appointment getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(Appointment appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public PatientCollection getPatient() {
		return patient;
	}

	public void setPatient(PatientCollection patient) {
		this.patient = patient;
	}

	@Override
	public String toString() {
		return "PatientVisitResponse [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", patientId=" + patientId
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", visitedTime=" + visitedTime + ", visitedFor=" + visitedFor + ", prescriptions=" + prescriptions
				+ ", clinicalNotes=" + clinicalNotes + ", clinicalNotesDiagrams=" + clinicalNotesDiagrams
				+ ", patientTreatment=" + patientTreatment + ", records=" + records + ", appointmentId=" + appointmentId
				+ ", appointmentRequest=" + appointmentRequest + ", time=" + time + ", fromDate=" + fromDate
				+ ", eyePrescription=" + eyePrescription + ", discarded=" + discarded + ", patient=" + patient + "]";
	}

}
