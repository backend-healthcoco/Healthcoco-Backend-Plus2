package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Fields;
import com.dpdocter.beans.WorkingHours;

public class CalenderResponse {

	private String id;

	private WorkingHours time;

	private Date fromDate;

	private String patientName;

	private String PID;

	private String mobileNumber;

	private String notes;

	private String patientId;

	private String status;

	private String state = "NEW";

	private String doctorId;

	private String category;

	private String branch;

	private List<Fields> treatmentFields;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<Fields> getTreatmentFields() {
		return treatmentFields;
	}

	public void setTreatmentFields(List<Fields> treatmentFields) {
		this.treatmentFields = treatmentFields;
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
		return "CalenderResponse [id=" + id + ", time=" + time + ", fromDate=" + fromDate + ", patientName="
				+ patientName + ", PID=" + PID + ", mobileNumber=" + mobileNumber + ", notes=" + notes + ", patientId="
				+ patientId + ", status=" + status + ", state=" + state + ", doctorId=" + doctorId + ", category="
				+ category + ", branch=" + branch + ", treatmentFields=" + treatmentFields + "]";
	}
}