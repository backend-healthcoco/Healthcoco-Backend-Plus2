package com.dpdocter.response;


import com.dpdocter.beans.BulkSmsCredits;

public class DoctorCollectionLookupResponse {
	private String id;

	private String userId;
	private BulkSmsCredits bulkSmsCredit;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public BulkSmsCredits getBulkSmsCredit() {
		return bulkSmsCredit;
	}

	public void setBulkSmsCredit(BulkSmsCredits bulkSmsCredit) {
		this.bulkSmsCredit = bulkSmsCredit;
	}

}
