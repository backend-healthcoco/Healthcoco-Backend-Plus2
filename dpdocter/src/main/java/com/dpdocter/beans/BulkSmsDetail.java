package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.enums.SMSStatus;

public class BulkSmsDetail {

		private SMS sms;

	    private SMSStatus deliveryStatus;

	    private Date sentTime = new Date();

	    private String deliveredTime;

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
	    
	    
}
