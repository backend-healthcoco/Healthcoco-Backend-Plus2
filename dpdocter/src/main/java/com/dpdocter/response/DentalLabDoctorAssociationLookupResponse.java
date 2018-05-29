package com.dpdocter.response;

import com.dpdocter.beans.Location;
import com.dpdocter.beans.User;
import com.dpdocter.collections.GenericCollection;

public class DentalLabDoctorAssociationLookupResponse extends GenericCollection {

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String dentalLabLocationId;
	private String dentalLabHospitalId;
	private Boolean isActive = true;
	private User doctor;
	private Location dentalLab;
	private Location location;

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

	public String getDentalLabLocationId() {
		return dentalLabLocationId;
	}

	public void setDentalLabLocationId(String dentalLabLocationId) {
		this.dentalLabLocationId = dentalLabLocationId;
	}

	public String getDentalLabHospitalId() {
		return dentalLabHospitalId;
	}

	public void setDentalLabHospitalId(String dentalLabHospitalId) {
		this.dentalLabHospitalId = dentalLabHospitalId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public Location getDentalLab() {
		return dentalLab;
	}

	public void setDentalLab(Location dentalLab) {
		this.dentalLab = dentalLab;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "DentalLabDoctorAssociationLookupResponse [id=" + id + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", dentalLabLocationId=" + dentalLabLocationId
				+ ", dentalLabHospitalId=" + dentalLabHospitalId + ", isActive=" + isActive + ", doctor=" + doctor
				+ ", dentalLab=" + dentalLab + "]";
	}

}
