package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.Records;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.VisitedFor;

public class PatientVisitResponse extends GenericCollection{

    private String id;

    private String patientId;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private Date visitedTime;

    private List<VisitedFor> visitedFor;

    private List<Prescription> prescriptions;

    private List<ClinicalNotes> clinicalNotes;

    private List<Records> records;

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

    public List<Records> getRecords() {
	return records;
    }

    public void setRecords(List<Records> records) {
	this.records = records;
    }

    @Override
    public String toString() {
	return "PatientVisitResponse [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
		+ hospitalId + ", visitedTime=" + visitedTime + ", visitedFor=" + visitedFor + ", prescriptions=" + prescriptions + ", clinicalNotes="
		+ clinicalNotes + ", records=" + records + "]";
    }

}
