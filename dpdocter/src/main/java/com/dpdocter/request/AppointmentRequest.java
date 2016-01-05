package com.dpdocter.request;

import java.util.Date;

import com.dpdocter.beans.Timing;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentCreatedBy;

public class AppointmentRequest {

    private String appointmentId;

    private AppointmentState state;

    private String doctorId;

    private String locationId;

    private String patientId;

    private WorkingHours time;

    private Date date;

    private AppointmentCreatedBy createdBy;

    private Boolean notifyPatientBySms;

    private Boolean notifyPatientByEmail;

    private Boolean notifyDoctorBySms;

    private Boolean notifyDoctorByEmail;
 
    public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public AppointmentState getState() {
		return state;
	}

	public void setState(AppointmentState state) {
		this.state = state;
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

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

	public AppointmentCreatedBy getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AppointmentCreatedBy createdBy) {
		this.createdBy = createdBy;
	}

	public Boolean getNotifyPatientBySms() {
		return notifyPatientBySms;
	}

	public void setNotifyPatientBySms(Boolean notifyPatientBySms) {
		this.notifyPatientBySms = notifyPatientBySms;
	}

	public Boolean getNotifyPatientByEmail() {
		return notifyPatientByEmail;
	}

	public void setNotifyPatientByEmail(Boolean notifyPatientByEmail) {
		this.notifyPatientByEmail = notifyPatientByEmail;
	}

	public Boolean getNotifyDoctorBySms() {
		return notifyDoctorBySms;
	}

	public void setNotifyDoctorBySms(Boolean notifyDoctorBySms) {
		this.notifyDoctorBySms = notifyDoctorBySms;
	}

	public Boolean getNotifyDoctorByEmail() {
		return notifyDoctorByEmail;
	}

	public void setNotifyDoctorByEmail(Boolean notifyDoctorByEmail) {
		this.notifyDoctorByEmail = notifyDoctorByEmail;
	}

	@Override
	public String toString() {
		return "AppointmentRequest [appointmentId=" + appointmentId + ", state=" + state + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", patientId=" + patientId + ", time=" + time + ", date=" + date
				+ ", createdBy=" + createdBy + ", notifyPatientBySms=" + notifyPatientBySms + ", notifyPatientByEmail="
				+ notifyPatientByEmail + ", notifyDoctorBySms=" + notifyDoctorBySms + ", notifyDoctorByEmail="
				+ notifyDoctorByEmail + "]";
	}
}
