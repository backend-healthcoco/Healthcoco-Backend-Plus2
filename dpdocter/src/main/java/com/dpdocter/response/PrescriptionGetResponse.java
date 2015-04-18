package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.collections.GenericCollection;

public class PrescriptionGetResponse extends GenericCollection {
	private String id;

	private String name;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean isDeleted = false;

	private List<PrescriptionItem> items;

	private String patientId;

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

	public List<PrescriptionItem> getItems() {
		return items;
	}

	public void setItems(List<PrescriptionItem> items) {
		this.items = items;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	@Override
	public String toString() {
		return "PrescriptionGetResponse [id=" + id + ", name=" + name + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", isDeleted=" + isDeleted + ", items=" + items + ", patientId=" + patientId + "]";
	}

}
