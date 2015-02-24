package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="group_cl")
public class GroupCollection {

	@Id
	private String id;
	@Field
	private String name;
	@Field
	private String description;
	@Field
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
		return "GroupCollection [id=" + id + ", name=" + name
				+ ", description=" + description + ", doctorId=" + doctorId
				+ "]";
	}
	
	
}
