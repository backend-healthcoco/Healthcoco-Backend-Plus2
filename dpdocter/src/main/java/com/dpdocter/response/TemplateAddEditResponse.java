package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.TemplateItem;

public class TemplateAddEditResponse {
	private String id;

	private String name;

	private String doctorId;

	private String locationId;

	private String hospitalId;

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

	public List<TemplateItem> getItems() {
		return items;
	}

	public void setItems(List<TemplateItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "TemplateAddEditResponse [id=" + id + ", name=" + name + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", items=" + items + "]";
	}

}
