package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HeaderSetup {

    private Boolean customHeader = true;

    private Boolean customLogo = true;

    private String logoType;

    private PatientDetails patientDetails = new PatientDetails();

    private List<PrintSettingsText> topLeftText;

    private List<PrintSettingsText> topRightText;
    
    private String headerHtml;
    
    private String headerImageUrl;
    
    private Boolean showHeaderImage=false;
    
    private Integer headerHeight=0;
    

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

	public String getHeaderImageUrl() {
		return headerImageUrl;
	}

	public void setHeaderImageUrl(String headerImageUrl) {
		this.headerImageUrl = headerImageUrl;
	}

	public Boolean getShowHeaderImage() {
		return showHeaderImage;
	}

	public void setShowHeaderImage(Boolean showHeaderImage) {
		this.showHeaderImage = showHeaderImage;
	}

	
	public Integer getHeaderHeight() {
		return headerHeight;
	}

	public void setHeaderHeight(Integer headerHeight) {
		this.headerHeight = headerHeight;
	}

	@Override
	public String toString() {
		return "HeaderSetup [customHeader=" + customHeader + ", customLogo=" + customLogo + ", logoType=" + logoType
				+ ", patientDetails=" + patientDetails + ", topLeftText=" + topLeftText + ", topRightText="
				+ topRightText + ", headerHtml=" + headerHtml + "]";
	}

}
