package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FooterSetup;
import com.dpdocter.beans.HeaderSetup;
import com.dpdocter.beans.PageSetup;
import com.dpdocter.enums.ComponentType;

@Document(collection = "print_settings_cl")
public class PrintSettingsCollection extends GenericCollection {

    @Id
    private String id;

    @Field
    private String doctorId;

    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String componentType = ComponentType.ALL.getType();

    @Field
    private PageSetup pageSetup;

    @Field
    private HeaderSetup headerSetup;

    @Field
    private FooterSetup footerSetup;

    @Field
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

    public String getComponentType() {
	return componentType;
    }

    public void setComponentType(String componentType) {
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
	return "PrintSettingsCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
		+ ", componentType=" + componentType + ", pageSetup=" + pageSetup + ", headerSetup=" + headerSetup + ", footerSetup=" + footerSetup
		+ ", discarded=" + discarded + "]";
    }
}
