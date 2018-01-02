package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class DentalWork extends GenericCollection {

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String workName;
	private Boolean isShadeRequired = false;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public Boolean getIsShadeRequired() {
		return isShadeRequired;
	}

	public void setIsShadeRequired(Boolean isShadeRequired) {
		this.isShadeRequired = isShadeRequired;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	@Override
	public String toString() {
		return "CustomWork [id=" + id + ", workName=" + workName + ", isShadeRequired=" + isShadeRequired
				+ ", discarded=" + discarded + "]";
	}

}
