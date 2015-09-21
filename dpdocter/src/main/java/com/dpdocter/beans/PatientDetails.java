package com.dpdocter.beans;

public class PatientDetails {

    private Boolean showName;

    private Boolean showMobileNumber;

    private Boolean showDOB;

    private Boolean showGender;

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

    @Override
    public String toString() {
	return "PatientDetails [showName=" + showName + ", showMobileNumber=" + showMobileNumber + ", showDOB=" + showDOB + ", showGender=" + showGender + "]";
    }
}
