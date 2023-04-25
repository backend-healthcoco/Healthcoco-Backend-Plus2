package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class FormContent extends GenericCollection {

	private String id;

	private String locationId;

	private String doctorId;

	private String hospitalId;

	private String declaration;

	private String title;

	private String type;

	private boolean discarded = false;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationId() {
		return locationId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public String getDeclaration() {
		return declaration;
	}

	public String getTitle() {
		return title;
	}

	public boolean isDiscarded() {
		return discarded;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setDeclaration(String declaration) {
		this.declaration = declaration;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}

}
