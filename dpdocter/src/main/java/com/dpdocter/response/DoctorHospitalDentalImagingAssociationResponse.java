package com.dpdocter.response;

import com.dpdocter.beans.Location;
import com.dpdocter.beans.User;

public class DoctorHospitalDentalImagingAssociationResponse {
	private String id;
	private String doctorId;
	private String doctorLocationId;
	private String hospitalId;
	private Location location;
	private Boolean discarded = true;
	private User doctor;

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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getDoctorLocationId() {
		return doctorLocationId;
	}

	public void setDoctorLocationId(String doctorLocationId) {
		this.doctorLocationId = doctorLocationId;
	}

	@Override
	public String toString() {
		return "DoctorHospitalDentalImagingAssociationResponse [id=" + id + ", doctorId=" + doctorId
				+ ", doctorLocationId=" + doctorLocationId + ", hospitalId=" + hospitalId + ", location=" + location
				+ ", discarded=" + discarded + ", doctor=" + doctor + "]";
	}

}
