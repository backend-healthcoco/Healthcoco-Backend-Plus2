package com.dpdocter.request;

public class DoctorAddEditIBSRequest {

	private String id;

    private String doctorId;

    private String locationId;

    private Boolean isIBSOn = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Boolean getIsIBSOn() {
		return isIBSOn;
	}

	public void setIsIBSOn(Boolean isIBSOn) {
		this.isIBSOn = isIBSOn;
	}

	@Override
	public String toString() {
		return "DoctorAddEditIBSRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", isIBSOn=" + isIBSOn + "]";
	}
}
