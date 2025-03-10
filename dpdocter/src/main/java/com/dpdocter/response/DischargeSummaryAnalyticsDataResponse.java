package com.dpdocter.response;

import java.util.Date;

public class DischargeSummaryAnalyticsDataResponse {
	private String id;

	private String patientId;
	
	private String patientName;

	private String mobileNumber;

	private Integer numberOfDaysAdmitted = 0;

	private Date dateOfDischarge;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Integer getNumberOfDaysAdmitted() {
		return numberOfDaysAdmitted;
	}

	public void setNumberOfDaysAdmitted(Integer numberOfDaysAdmitted) {
		this.numberOfDaysAdmitted = numberOfDaysAdmitted;
	}

	public Date getDateOfDischarge() {
		return dateOfDischarge;
	}

	public void setDateOfDischarge(Date dateOfDischarge) {
		this.dateOfDischarge = dateOfDischarge;
	}

}
