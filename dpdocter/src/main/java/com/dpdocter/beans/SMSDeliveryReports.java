package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SMSDeliveryReports {

    private String requestId;

    private String userId;

    private List<SMSReport> report;

    private String senderId;
    
    private String campaignName;

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
    
    

    public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	@Override
    public String toString() {
	return "SMSDeliveryReports [requestId=" + requestId + ", userId=" + userId + ", report=" + report + ", senderId=" + senderId + "]";
    }
}
