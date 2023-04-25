package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PatientDetails {

	private Boolean showName = true;

	private Boolean showPID = true;

	private Boolean showMobileNumber = true;

	private Boolean showDOB = true;

	private Boolean showGender = true;

	private Boolean showReferedBy = true;

	private Boolean showDate = true;

	private Boolean showBloodGroup = true;

	private Boolean showResourceId = true;

	private Boolean showHospitalId = false;

	private Boolean showCity = false;

	private PrintSettingsText style;

	private Boolean showPatientDetailsInCertificate = true;

	private String PIDKey = "PID";

	public Boolean getShowCity() {
		return showCity;
	}

	public void setShowCity(Boolean showCity) {
		this.showCity = showCity;
	}

	public Boolean getShowName() {
		return showName;
	}

	public void setShowName(Boolean showName) {
		this.showName = showName;
	}

	public Boolean getShowMobileNumber() {
		return showMobileNumber;
	}

	public void setShowMobileNumber(Boolean showMobileNumber) {
		this.showMobileNumber = showMobileNumber;
	}

	public Boolean getShowDOB() {
		return showDOB;
	}

	public void setShowDOB(Boolean showDOB) {
		this.showDOB = showDOB;
	}

	public Boolean getShowGender() {
		return showGender;
	}

	public void setShowGender(Boolean showGender) {
		this.showGender = showGender;
	}

	public Boolean getShowReferedBy() {
		return showReferedBy;
	}

	public void setShowReferedBy(Boolean showReferedBy) {
		this.showReferedBy = showReferedBy;
	}

	public Boolean getShowDate() {
		return showDate;
	}

	public void setShowDate(Boolean showDate) {
		this.showDate = showDate;
	}

	public Boolean getShowBloodGroup() {
		return showBloodGroup;
	}

	public void setShowBloodGroup(Boolean showBloodGroup) {
		this.showBloodGroup = showBloodGroup;
	}

	public Boolean getShowResourceId() {
		return showResourceId;
	}

	public void setShowResourceId(Boolean showResourceId) {
		this.showResourceId = showResourceId;
	}

	public Boolean getShowPID() {
		return showPID;
	}

	public void setShowPID(Boolean showPID) {
		this.showPID = showPID;
	}

	public PrintSettingsText getStyle() {
		return style;
	}

	public void setStyle(PrintSettingsText style) {
		this.style = style;
	}

	public Boolean getShowHospitalId() {
		return showHospitalId;
	}

	public void setShowHospitalId(Boolean showHospitalId) {
		this.showHospitalId = showHospitalId;
	}

	public Boolean getShowPatientDetailsInCertificate() {
		return showPatientDetailsInCertificate;
	}

	public void setShowPatientDetailsInCertificate(Boolean showPatientDetailsInCertificate) {
		this.showPatientDetailsInCertificate = showPatientDetailsInCertificate;
	}

	public String getPIDKey() {
		return PIDKey;
	}

	public void setPIDKey(String pIDKey) {
		PIDKey = pIDKey;
	}

	@Override
	public String toString() {
		return "PatientDetails [showName=" + showName + ", showPID=" + showPID + ", showMobileNumber="
				+ showMobileNumber + ", showDOB=" + showDOB + ", showGender=" + showGender + ", showReferedBy="
				+ showReferedBy + ", showDate=" + showDate + ", showBloodGroup=" + showBloodGroup + ", showResourceId="
				+ showResourceId + ", showHospitalId=" + showHospitalId + ", showCity=" + showCity + ", style=" + style
				+ ", showPatientDetailsInCertificate=" + showPatientDetailsInCertificate + ", PIDKey=" + PIDKey + "]";
	}

}
