package com.dpdocter.response;

import com.dpdocter.beans.Location;
import com.dpdocter.beans.User;
import com.dpdocter.collections.GenericCollection;

public class DentalLabDoctorAssociationLookupResponse extends GenericCollection {

	private String id;
	private String doctorId;
	private String dentalLabId;
	private Boolean isActive = true;
	private User doctor;
	private Location dentalLab;

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

	public String getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(String dentalLabId) {
		this.dentalLabId = dentalLabId;
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

	@Override
	public String toString() {
		return "DentalLabDoctorAssociationLookupResponse [id=" + id + ", doctorId=" + doctorId + ", dentalLabId="
				+ dentalLabId + ", isActive=" + isActive + "]";
	}

}
