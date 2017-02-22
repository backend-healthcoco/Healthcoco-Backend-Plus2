package com.dpdocter.response;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;

public class AppointmentDoctorReminderResponse {

	private String id;
	
	private ObjectId doctorId;
	
	private int total;

	private String patientId;
	
	private WorkingHours time;
	
	private UserCollection doctor;
	
	private String appointmentId;
	
	private Date fromDate;
	
    private ObjectId locationId;

    private ObjectId hospitalId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public UserCollection getDoctor() {
		return doctor;
	}

	public void setDoctor(UserCollection doctor) {
		this.doctor = doctor;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
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

	@Override
	public String toString() {
		return "AppointmentDoctorReminderResponse [id=" + id + ", doctorId=" + doctorId + ", total=" + total
				+ ", patientId=" + patientId + ", time=" + time + ", doctor=" + doctor + ", appointmentId=" + appointmentId + ", fromDate=" + fromDate + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + "]";
	}

}
