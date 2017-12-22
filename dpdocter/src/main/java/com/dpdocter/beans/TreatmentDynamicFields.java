package com.dpdocter.beans;

import com.dpdocter.enums.Range;

public class TreatmentDynamicFields {

	private String services = Range.BOTH.getRange();

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

}
