package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.SMSReport;

@Document(collection = "sms_delivery_report_cl")
public class SMSDeliveryReportsCollection {

	@Id
	private ObjectId id;
	@Field
	private String requestId;
	@Field
	private String userId;
	@Field
	private List<SMSReport> report;
	@Field
	private String senderId;

		public ObjectId getId() {
			return id;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}

		public String getRequestId() {
			return requestId;
		}

		public void setRequestId(String requestId) {
			this.requestId = requestId;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public List<SMSReport> getReport() {
			return report;
		}

		public void setReport(List<SMSReport> report) {
			this.report = report;
		}

		public String getSenderId() {
			return senderId;
		}

		public void setSenderId(String senderId) {
			this.senderId = senderId;
		}
	    
	    

	
}
