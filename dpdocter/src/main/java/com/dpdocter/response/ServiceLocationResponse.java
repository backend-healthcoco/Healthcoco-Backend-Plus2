package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.Location;

public class ServiceLocationResponse {

	DentalDiagnosticService service;
	List<Location> locations;

	public DentalDiagnosticService getService() {
		return service;
	}

	public void setService(DentalDiagnosticService service) {
		this.service = service;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	@Override
	public String toString() {
		return "ServiceLocationResponse [service=" + service + ", locations=" + locations + "]";
	}

}
