package com.dpdocter.collections;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.VisitedFor;

@Document(collection = "patient_track_cl")
public class PatientTrackCollection extends GenericCollection{
    @Id
    private String id;

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
    private VisitedFor visitedFor;

    private long total;

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

    public VisitedFor getVisitedFor() {
	return visitedFor;
    }

    public void setVisitedFor(VisitedFor visitedFor) {
	this.visitedFor = visitedFor;
    }

    public long getTotal() {
	return total;
    }

    public void setTotal(long total) {
	this.total = total;
    }

    @Override
    public String toString() {
	return "PatientTrackCollection [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
		+ hospitalId + ", visitedTime=" + visitedTime + ", visitedFor=" + visitedFor + ", total=" + total + "]";
    }

}
