package com.dpdocter.response;

import com.dpdocter.beans.DentalDiagnosticService;

public class DentalImagingLocationServiceAssociationLookupResponse {

	private String id;
	private String dentalDiagnosticServiceId;
	private String locationId;
	private String hospitalId;
	private Boolean discarded = false;
	private DentalDiagnosticService service;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDentalDiagnosticServiceId() {
		return dentalDiagnosticServiceId;
	}

	public void setDentalDiagnosticServiceId(String dentalDiagnosticServiceId) {
		this.dentalDiagnosticServiceId = dentalDiagnosticServiceId;
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

	public DentalDiagnosticService getService() {
		return service;
	}

	public void setService(DentalDiagnosticService service) {
		this.service = service;
	}

	@Override
	public String toString() {
		return "DentalImagingLocationServiceAssociationLookupResponse [id=" + id + ", dentalDiagnosticServiceId="
				+ dentalDiagnosticServiceId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", discarded=" + discarded + ", service=" + service + "]";
	}

}
