package com.dpdocter.request;

public class DoctorLabFavouriteDoctorRequest {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String favouriteDoctorId;

	private String favouriteLocationId;

	private String favouriteHospitalId;

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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getFavouriteDoctorId() {
		return favouriteDoctorId;
	}

	public void setFavouriteDoctorId(String favouriteDoctorId) {
		this.favouriteDoctorId = favouriteDoctorId;
	}

	public String getFavouriteLocationId() {
		return favouriteLocationId;
	}

	public void setFavouriteLocationId(String favouriteLocationId) {
		this.favouriteLocationId = favouriteLocationId;
	}

	public String getFavouriteHospitalId() {
		return favouriteHospitalId;
	}

	public void setFavouriteHospitalId(String favouriteHospitalId) {
		this.favouriteHospitalId = favouriteHospitalId;
	}

}
