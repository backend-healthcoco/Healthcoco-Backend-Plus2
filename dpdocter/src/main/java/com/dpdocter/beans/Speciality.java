package com.dpdocter.beans;

public class Speciality {
	private String id;

	private String speciality;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	@Override
	public String toString() {
		return "Speciality [id=" + id + ", speciality=" + speciality + "]";
	}

}
