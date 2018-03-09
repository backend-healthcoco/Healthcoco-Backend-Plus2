package com.dpdocter.response;

import com.dpdocter.beans.Location;
import com.dpdocter.beans.User;

public class CBDoctorAssociationLookupResponse {

	private String id;
	private String collectionBoyId;
	private String dentalLabId;
	private String doctorId;
	private Boolean isActive = true;
	private User doctor;
	private Location dentalLab;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public String getDentalLabId() {
		return dentalLabId;
	}

	public void setDentalLabId(String dentalLabId) {
		this.dentalLabId = dentalLabId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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
		return "CBDoctorAssociationLookupResponse [id=" + id + ", collectionBoyId=" + collectionBoyId + ", dentalLabId="
				+ dentalLabId + ", doctorId=" + doctorId + ", isActive=" + isActive + ", doctor=" + doctor + "]";
	}

}
