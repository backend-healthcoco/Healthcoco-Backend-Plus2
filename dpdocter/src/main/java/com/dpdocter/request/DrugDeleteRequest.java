package com.dpdocter.request;

public class DrugDeleteRequest {
	private String id;

	private String doctorId;

	private String hospitalId;

	private String locationId;

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

	@Override
	public String toString() {
		return "DrugDeleteRequest [id=" + id + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId + ", locationId="
				+ locationId + "]";
	}

}
