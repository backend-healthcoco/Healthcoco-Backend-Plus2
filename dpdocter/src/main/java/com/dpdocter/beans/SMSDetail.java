package com.dpdocter.beans;

import java.util.Date;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.SMSStatus;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SMSDetail {
    private ObjectId userId;

    private String userName;

    private SMS sms;

    private SMSStatus deliveryStatus;

    private Date sentTime = new Date();

    private String deliveredTime;

    public SMSDetail() {
		// TODO Auto-generated constructor stub
	}
    

	public SMSDetail(SMS sms, SMSStatus deliveryStatus) {
		super();
		this.sms = sms;
		this.deliveryStatus = deliveryStatus;
	}
	public ObjectId getUserId() {
	return userId;
    }

    public void setUserId(ObjectId userId) {
	this.userId = userId;
    }

    public String getUserName() {
	return userName;
    }

    public void setUserName(String userName) {
	this.userName = userName;
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

    public String getDeliveredTime() {
	return deliveredTime;
    }

    public void setDeliveredTime(String deliveredTime) {
	this.deliveredTime = deliveredTime;
    }
    
    

    @Override
    public String toString() {
	return "SMSDetail [userId=" + userId + ", userName=" + userName + ", sms=" + sms + ", deliveryStatus=" + deliveryStatus + ", sentTime=" + sentTime
		+ ", deliveredTime=" + deliveredTime + "]";
    }
}
