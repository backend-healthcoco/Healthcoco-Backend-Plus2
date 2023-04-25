package com.dpdocter.response;

import java.util.List;

import org.bson.types.ObjectId;

public class TreatmentServiceUpdateResponse {

	private String id;

	private String doctorId;

	private String locationId;

	private List<ObjectId> treatmentServiceIds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<ObjectId> getTreatmentServiceIds() {
		return treatmentServiceIds;
	}

	public void setTreatmentServiceIds(List<ObjectId> treatmentServiceIds) {
		this.treatmentServiceIds = treatmentServiceIds;
	}

	@Override
	public String toString() {
		return "TreatmentServiceUpdateResponse [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", treatmentServiceIds=" + treatmentServiceIds + "]";
	}
}
