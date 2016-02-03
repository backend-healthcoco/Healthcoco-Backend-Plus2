package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.VisitedFor;

@Document(collection = "patient_visit_cl")
public class PatientVisitCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private String uniqueId;
    
    @Field
    private String patientId;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private Date visitedTime;

    @Field
    private List<VisitedFor> visitedFor;

    private long total;

    @Field
    private List<String> prescriptionId;

    @Field
    private List<String> clinicalNotesId;

    @Field
    private List<String> recordId;

    @Field
    private Boolean discarded = false;

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

    public long getTotal() {
	return total;
    }

    public void setTotal(long total) {
	this.total = total;
    }

    public List<String> getPrescriptionId() {
	return prescriptionId;
    }

    public void setPrescriptionId(List<String> prescriptionId) {
	this.prescriptionId = prescriptionId;
    }

    public List<String> getClinicalNotesId() {
	return clinicalNotesId;
    }

    public void setClinicalNotesId(List<String> clinicalNotesId) {
	this.clinicalNotesId = clinicalNotesId;
    }

    public List<String> getRecordId() {
	return recordId;
    }

    public void setRecordId(List<String> recordId) {
	this.recordId = recordId;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@Override
	public String toString() {
		return "PatientVisitCollection [id=" + id + ", uniqueId=" + uniqueId + ", patientId=" + patientId
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", visitedTime=" + visitedTime + ", visitedFor=" + visitedFor + ", total=" + total
				+ ", prescriptionId=" + prescriptionId + ", clinicalNotesId=" + clinicalNotesId + ", recordId="
				+ recordId + ", discarded=" + discarded + "]";
	}

}
