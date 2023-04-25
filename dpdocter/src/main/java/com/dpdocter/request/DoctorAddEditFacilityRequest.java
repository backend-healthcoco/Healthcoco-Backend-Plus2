package com.dpdocter.request;

import com.dpdocter.enums.DoctorFacility;

public class DoctorAddEditFacilityRequest {

	private String id;

	private String doctorId;

	private String locationId;

	private DoctorFacility facility;

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

	public DoctorFacility getFacility() {
		return facility;
	}

	public void setFacility(DoctorFacility facility) {
		this.facility = facility;
	}

	@Override
	public String toString() {
		return "DoctorAddEditFacilityRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", facility=" + facility + "]";
	}
}
