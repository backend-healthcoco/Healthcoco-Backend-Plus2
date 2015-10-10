package com.dpdocter.beans;

public class SMSDeliveryReports {

    private String requestId;

    private String userId;

    private SMSReport report;

    private String senderId;

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

    public SMSReport getReport() {
	return report;
    }

    public void setReport(SMSReport report) {
	this.report = report;
    }

    public String getSenderId() {
	return senderId;
    }

    public void setSenderId(String senderId) {
	this.senderId = senderId;
    }

    @Override
    public String toString() {
	return "SMSDeliveryReports [requestId=" + requestId + ", userId=" + userId + ", report=" + report + ", senderId=" + senderId + "]";
    }
}
