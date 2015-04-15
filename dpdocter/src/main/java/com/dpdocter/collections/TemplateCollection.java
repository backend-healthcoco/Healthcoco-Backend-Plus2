package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.TemplateItem;

@Document(collection = "template_cl")
public class TemplateCollection {
	@Field
	private String id;

	@Field
	private String name;

	@Field
	private String doctorId;

	@Field
	private String locationId;

	@Field
	private String hospitalId;

	@Field
	private Boolean isDeleted = false;

	@Field
	private List<TemplateItem> items;

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

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public List<TemplateItem> getItems() {
		return items;
	}

	public void setItems(List<TemplateItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "TemplateCollection [id=" + id + ", name=" + name + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", isDeleted=" + isDeleted + ", items=" + items + "]";
	}

}
