package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Manufacturer extends GenericCollection {

	private String id;
	private String name;
	private String locationId;
	private String hospitalId;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return "Manufacturer [id=" + id + ", name=" + name + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", discarded=" + discarded + "]";
	}

}
