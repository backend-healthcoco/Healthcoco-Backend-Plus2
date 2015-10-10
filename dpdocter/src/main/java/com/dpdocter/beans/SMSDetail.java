package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.enums.SMSStatus;

public class SMSDetail {
    private String patientId;

    private String patientName;

    private SMS sms;

    private SMSStatus deliveryStatus;

    private Date sentTime = new Date();

    private Date deliveredTime;

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public String getPatientName() {
	return patientName;
    }

    public void setPatientName(String patientName) {
	this.patientName = patientName;
    }

    public SMS getSms() {
	return sms;
    }

    public void setSms(SMS sms) {
	this.sms = sms;
    }

    public SMSStatus getDeliveryStatus() {
	return deliveryStatus;
    }

    public void setDeliveryStatus(SMSStatus deliveryStatus) {
	this.deliveryStatus = deliveryStatus;
    }

    public Date getSentTime() {
	return sentTime;
    }

    public void setSentTime(Date sentTime) {
	this.sentTime = sentTime;
    }

    public Date getDeliveredTime() {
	return deliveredTime;
    }

    public void setDeliveredTime(Date deliveredTime) {
	this.deliveredTime = deliveredTime;
    }

    @Override
    public String toString() {
	return "SMSDetail [patientId=" + patientId + ", patientName=" + patientName + ", sms=" + sms + ", deliveryStatus=" + deliveryStatus + ", sentTime="
		+ sentTime + ", deliveredTime=" + deliveredTime + "]";
    }

}
