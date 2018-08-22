package com.dpdocter.beans.v2;

import java.util.Date;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AppointmentState;

public class AppointmentDetails extends GenericCollection{

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private WorkingHours time;

	private AppointmentState state;

	private Boolean isRescheduled = false;

	private Date fromDate;

	private Date toDate;

	private String appointmentId;
    
	private String localPatientName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public AppointmentState getState() {
		return state;
	}

	public void setState(AppointmentState state) {
		this.state = state;
	}

	public Boolean getIsRescheduled() {
		return isRescheduled;
	}

	public void setIsRescheduled(Boolean isRescheduled) {
		this.isRescheduled = isRescheduled;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	@Override
	public String toString() {
		return "AppointmentDetails [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", time=" + time + ", state=" + state
				+ ", isRescheduled=" + isRescheduled + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", appointmentId=" + appointmentId + ", localPatientName=" + localPatientName + "]";
	}
	
	
	
}
