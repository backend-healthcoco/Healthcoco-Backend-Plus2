package com.dpdocter.beans;

import java.util.List;

public class HeaderSetup {

    private Boolean customHeader = true;

    private Boolean customLogo = true;

    private String logoType;

    private PatientDetails patientDetails;

    private List<PrintSettingsText> topLeftText;

    private List<PrintSettingsText> topRightText;
    
    private String headerHtml;

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

    public List<PrintSettingsText> getTopLeftText() {
	return topLeftText;
    }

    public void setTopLeftText(List<PrintSettingsText> topLeftText) {
	this.topLeftText = topLeftText;
    }

    public List<PrintSettingsText> getTopRightText() {
	return topRightText;
    }

    public void setTopRightText(List<PrintSettingsText> topRightText) {
	this.topRightText = topRightText;
    }

	public String getHeaderHtml() {
		return headerHtml;
	}

	public void setHeaderHtml(String headerHtml) {
		this.headerHtml = headerHtml;
	}

	@Override
	public String toString() {
		return "HeaderSetup [customHeader=" + customHeader + ", customLogo=" + customLogo + ", logoType=" + logoType
				+ ", patientDetails=" + patientDetails + ", topLeftText=" + topLeftText + ", topRightText="
				+ topRightText + ", headerHtml=" + headerHtml + "]";
	}

}
