package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.FooterSetup;
import com.dpdocter.beans.HeaderSetup;
import com.dpdocter.beans.PageSetup;
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.enums.ComponentType;

@Document(collection = "print_settings_cl")
@CompoundIndexes({
    @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}")
})
public class PrintSettingsCollection extends GenericCollection {

    @Id
    private ObjectId id;

    @Indexed
    private ObjectId doctorId;

    @Field
    private ObjectId locationId;

    @Field
    private ObjectId hospitalId;

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

    @Field
    private String clinicLogoUrl;

    @Field
    private PrintSettingsText contentSetup;
    
    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public ObjectId getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(ObjectId doctorId) {
	this.doctorId = doctorId;
    }

    public ObjectId getLocationId() {
	return locationId;
    }

    public void setLocationId(ObjectId locationId) {
	this.locationId = locationId;
    }

    public ObjectId getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(ObjectId hospitalId) {
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

    public String getClinicLogoUrl() {
	return clinicLogoUrl;
    }

    public void setClinicLogoUrl(String clinicLogoUrl) {
	this.clinicLogoUrl = clinicLogoUrl;
    }

	public PrintSettingsText getContentSetup() {
		return contentSetup;
	}

	public void setContentSetup(PrintSettingsText contentSetup) {
		this.contentSetup = contentSetup;
	}

	@Override
	public String toString() {
		return "PrintSettingsCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", componentType=" + componentType + ", pageSetup=" + pageSetup
				+ ", headerSetup=" + headerSetup + ", footerSetup=" + footerSetup + ", discarded=" + discarded
				+ ", clinicLogoUrl=" + clinicLogoUrl + ", contentSetup=" + contentSetup + "]";
	}
}
