package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DentalDiagnosticServiceRequest;

public class ServiceLocationRequest {

	private String hospitalId;
	private String locationId;
	private List<DentalDiagnosticServiceRequest> services;

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public List<DentalDiagnosticServiceRequest> getServices() {
		return services;
	}

	public void setServices(List<DentalDiagnosticServiceRequest> services) {
		this.services = services;
	}

}
