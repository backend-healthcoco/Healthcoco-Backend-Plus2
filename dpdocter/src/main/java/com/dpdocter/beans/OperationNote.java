package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class OperationNote extends GenericCollection {
	
	private String id;

	private String operationNotes;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private String speciality;

	public String getId() {
		return id;
	}

	public String getOperationNotes() {
		return operationNotes;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOperationNotes(String operationNotes) {
		this.operationNotes = operationNotes;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	
	

}
