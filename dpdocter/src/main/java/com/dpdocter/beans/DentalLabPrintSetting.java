package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class DentalLabPrintSetting extends GenericCollection {

	private String id;
	private HeaderSetup headerSetup;
	private String locationId;
	private String hospitalId;
	private Boolean customHeader = true;
	private Boolean customLogo = true;
	private Boolean discarded = false;
	private String clinicLogoUrl;
	private String hospitalUId;
	private Boolean showPoweredBy = true;

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public HeaderSetup getHeaderSetup() {
		return headerSetup;
	}

	public void setHeaderSetup(HeaderSetup headerSetup) {
		this.headerSetup = headerSetup;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

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
