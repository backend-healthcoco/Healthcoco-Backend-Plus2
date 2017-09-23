package com.dpdocter.response;

import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocationCollection;

public class FavouriteLookupResponse {

	private String resourceId;
	
	private LocaleCollection pharmacy;
	
	private LocationCollection lab;

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public LocaleCollection getPharmacy() {
		return pharmacy;
	}

	public void setPharmacy(LocaleCollection pharmacy) {
		this.pharmacy = pharmacy;
	}

	public LocationCollection getLab() {
		return lab;
	}

	public void setLab(LocationCollection lab) {
		this.lab = lab;
	}

	@Override
	public String toString() {
		return "FavouriteLookupResponse [resourceId=" + resourceId + ", pharmacy=" + pharmacy + ", lab=" + lab + "]";
	}
}
