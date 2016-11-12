package com.dpdocter.beans;

import org.bson.types.ObjectId;

public class BirthdaySMSDetailsForPatients {

	private ObjectId Id;

	private ObjectId userLocationId;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private String locationName;
	

	public ObjectId getUserLocationId() {
		return userLocationId;
	}

	public void setUserLocationId(ObjectId userLocationId) {
		this.userLocationId = userLocationId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public ObjectId getId() {
		return Id;
	}

	public void setId(ObjectId id) {
		Id = id;
	}

}
