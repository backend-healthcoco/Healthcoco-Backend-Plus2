package com.dpdocter.beans;

public class RateCard {

	private String id;
	private String locationId;
	private String hospitalId;
	private String name;
	private Boolean discarded = false;
	private Boolean isDefault = false;
	private Boolean isForPatient = false;

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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public Boolean getIsForPatient() {
		return isForPatient;
	}

	public void setIsForPatient(Boolean isForPatient) {
		this.isForPatient = isForPatient;
	}

	@Override
	public String toString() {
		return "RateCard [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", name=" + name
				+ ", discarded=" + discarded + "]";
	}

}
