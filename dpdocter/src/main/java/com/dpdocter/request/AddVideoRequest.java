package com.dpdocter.request;

public class AddVideoRequest {

	private String name;

	private String speciality;

	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "AddVideoRequest [name=" + name + ", speciality=" + speciality + ", description=" + description + "]";
	}

}
