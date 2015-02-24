package com.dpdocter.beans;

public class Group {
	private String id;
	private String name;
	private String description;
	private String doctorId;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", description="
				+ description + ", doctorId=" + doctorId + "]";
	}
	

}
