package com.dpdocter.beans;

public class HeaderSetup {

	private Boolean customHeader;
	
	private Boolean customLogo;
	
	private String logoType;
	
	private PatientDetails patientDetails;
	
	private String topLeftText;
	
	private String topRightText;

	public Boolean getCustomHeader() {
		return customHeader;
	}

	public void setCustomHeader(Boolean customHeader) {
		this.customHeader = customHeader;
	}

	public Boolean getCustomLogo() {
		return customLogo;
	}

	public void setCustomLogo(Boolean customLogo) {
		this.customLogo = customLogo;
	}

	public String getLogoType() {
		return logoType;
	}

	public void setLogoType(String logoType) {
		this.logoType = logoType;
	}

	public PatientDetails getPatientDetails() {
		return patientDetails;
	}

	public void setPatientDetails(PatientDetails patientDetails) {
		this.patientDetails = patientDetails;
	}

	public String getTopLeftText() {
		return topLeftText;
	}

	public void setTopLeftText(String topLeftText) {
		this.topLeftText = topLeftText;
	}

	public String getTopRightText() {
		return topRightText;
	}

	public void setTopRightText(String topRightText) {
		this.topRightText = topRightText;
	}

	@Override
	public String toString() {
		return "HeaderSetup [customHeader=" + customHeader + ", customLogo=" + customLogo + ", logoType=" + logoType
				+ ", patientDetails=" + patientDetails + ", topLeftText=" + topLeftText + ", topRightText="
				+ topRightText + "]";
	}
}
