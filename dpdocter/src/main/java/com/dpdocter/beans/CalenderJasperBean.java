package com.dpdocter.beans;

import java.util.List;

public class CalenderJasperBean {

	private String timing = "";

	private String fromDate;

	private String patientName = "";

	private String mobileNumber = "";

	private String notes = "";

	private String groupName = "";

	private String status = "";
	
	private String treatments = "";
	
	private String category = "";

	private String branch = "";
	
	private String patientTreatment ="";

	public String getPatientTreatment() {
		return patientTreatment;
	}

	public void setPatientTreatment(String  patientTreatment) {
		this.patientTreatment = patientTreatment;
	}

	public String getTiming() {
		return timing;
	}

	public void setTiming(String timing) {
		this.timing = timing;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTreatments() {
		return treatments;
	}

	public void setTreatments(String treatments) {
		this.treatments = treatments;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	@Override
	public String toString() {
		return "CalenderJasperBean [timing=" + timing + ", fromDate=" + fromDate + ", patientName=" + patientName
				+ ", mobileNumber=" + mobileNumber + ", notes=" + notes + ", groupName=" + groupName + ", status="
				+ status + ", treatments=" + treatments + ", category=" + category + ", branch=" + branch + "]";
	}
}
