package com.dpdocter.beans;

import com.dpdocter.enums.ComponentType;

public class PrintSettings {

    private String id;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private ComponentType componentType = ComponentType.ALL;

    private PageSetup pageSetup;

    private HeaderSetup headerSetup;

    private FooterSetup footerSetup;

    private Boolean discarded = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public ComponentType getComponentType() {
	return componentType;
    }

    public void setComponentType(ComponentType componentType) {
	this.componentType = componentType;
    }

    public PageSetup getPageSetup() {
	return pageSetup;
    }

    public void setPageSetup(PageSetup pageSetup) {
	this.pageSetup = pageSetup;
    }

    public HeaderSetup getHeaderSetup() {
	return headerSetup;
    }

    public void setHeaderSetup(HeaderSetup headerSetup) {
	this.headerSetup = headerSetup;
    }

    public FooterSetup getFooterSetup() {
	return footerSetup;
    }

    public void setFooterSetup(FooterSetup footerSetup) {
	this.footerSetup = footerSetup;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    @Override
    public String toString() {
	return "PrintSettings [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", componentType="
		+ componentType + ", pageSetup=" + pageSetup + ", headerSetup=" + headerSetup + ", footerSetup=" + footerSetup + ", discarded=" + discarded
		+ "]";
    }
}
