package com.dpdocter.beans;

public class PatientDetails {

    private Boolean showName;

    private Boolean showMobileNumber;

    private Boolean showDOB;

    private Boolean showGender;
    
    private Boolean showReferedBy;
    
    private Boolean showDate;
    
    private Boolean showBloodGroup;
    
    private Boolean showResourceId;

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

	@Override
	public String toString() {
		return "PatientDetails [showName=" + showName + ", showMobileNumber=" + showMobileNumber + ", showDOB="
				+ showDOB + ", showGender=" + showGender + ", showReferedBy=" + showReferedBy + ", showDate=" + showDate
				+ ", showBloodGroup=" + showBloodGroup + ", showResourceId=" + showResourceId + "]";
	}
}
