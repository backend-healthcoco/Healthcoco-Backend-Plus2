package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class DentalLabServiceAssociation extends GenericCollection{

	private String id;
	private String dentalImagingServiceId;
	private String locationId;
	private String hospitalId;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDentalImagingServiceId() {
		return dentalImagingServiceId;
	}

	public void setDentalImagingServiceId(String dentalImagingServiceId) {
		this.dentalImagingServiceId = dentalImagingServiceId;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "DentalLabServiceAssociation [id=" + id + ", dentalImagingServiceId=" + dentalImagingServiceId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + "]";
	}

}
