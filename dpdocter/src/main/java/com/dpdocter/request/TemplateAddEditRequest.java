package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.TemplateAddItem;

public class TemplateAddEditRequest {
	private String id;

	private String name;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private List<TemplateAddItem> items;

	private Boolean isDefault = false;

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

	public List<TemplateAddItem> getItems() {
		return items;
	}

	public void setItems(List<TemplateAddItem> items) {
		this.items = items;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public String toString() {
		return "TemplateAddEditRequest [id=" + id + ", name=" + name + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", items=" + items + "]";
	}

}
