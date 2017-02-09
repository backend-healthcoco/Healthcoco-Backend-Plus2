package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.enums.VisitedFor;

public class PatientVisitLookupBean {

	private ObjectId id;

	private String uniqueEmrId;

	private ObjectId patientId;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private Date visitedTime;

	private List<VisitedFor> visitedFor;

	private long total;

	private List<ObjectId> prescriptionId;

	private List<ObjectId> clinicalNotesId;

	private List<ObjectId> treatmentId;

	private List<ObjectId> recordId;

	private String appointmentId;

	private WorkingHours time;

	private Date fromDate;

	private Boolean discarded = false;

	private Appointment appointment;

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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
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

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<ObjectId> getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(List<ObjectId> prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	public List<ObjectId> getClinicalNotesId() {
		return clinicalNotesId;
	}

	public void setClinicalNotesId(List<ObjectId> clinicalNotesId) {
		this.clinicalNotesId = clinicalNotesId;
	}

	public List<ObjectId> getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(List<ObjectId> treatmentId) {
		this.treatmentId = treatmentId;
	}

	public List<ObjectId> getRecordId() {
		return recordId;
	}

	public void setRecordId(List<ObjectId> recordId) {
		this.recordId = recordId;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}
	

}
