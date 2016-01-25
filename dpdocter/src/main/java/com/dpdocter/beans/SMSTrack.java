package com.dpdocter.beans;

import java.util.Date;

public class SMSTrack {

    private String id;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String patientName;

    private String deliveryStatus;

    private String deliveredTime;

    private Date sentTime;

    private String type;

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

    public String getPatientName() {
	return patientName;
    }

    public void setPatientName(String patientName) {
	this.patientName = patientName;
    }

    public String getDeliveryStatus() {
	return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
	this.deliveryStatus = deliveryStatus;
    }

    public String getDeliveredTime() {
	return deliveredTime;
    }

    public void setDeliveredTime(String deliveredTime) {
	this.deliveredTime = deliveredTime;
    }

    public Date getSentTime() {
	return sentTime;
    }

    public void setSentTime(Date sentTime) {
	this.sentTime = sentTime;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "SMSTrack [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", patientName=" + patientName + ", deliveryStatus=" + deliveryStatus
				+ ", deliveredTime=" + deliveredTime + ", sentTime=" + sentTime + ", type=" + type + "]";
	}
}
