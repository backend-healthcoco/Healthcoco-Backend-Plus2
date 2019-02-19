package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.HeaderSetup;

@Document(collection = "dental_lab_print_settings_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class DentalLabPrintSettingCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private HeaderSetup headerSetup;

	@Field
	private Boolean discarded = false;

	@Field
	private String clinicLogoUrl;

	@Field
	private String hospitalUId;

	@Field
	private Boolean showPoweredBy = true;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public HeaderSetup getHeaderSetup() {
		return headerSetup;
	}

	public void setHeaderSetup(HeaderSetup headerSetup) {
		this.headerSetup = headerSetup;
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

	public String getHospitalUId() {
		return hospitalUId;
	}

	public void setHospitalUId(String hospitalUId) {
		this.hospitalUId = hospitalUId;
	}

	public Boolean getShowPoweredBy() {
		return showPoweredBy;
	}

	public void setShowPoweredBy(Boolean showPoweredBy) {
		this.showPoweredBy = showPoweredBy;
	}

}
