package com.dpdocter.response;

import java.util.List;

public class WebAppointmentSlotDataResponse {

	private String doctorSlugURL;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	List<SlotDataResponse> slots;

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
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

	public List<SlotDataResponse> getSlots() {
		return slots;
	}

	public void setSlots(List<SlotDataResponse> slots) {
		this.slots = slots;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public String toString() {
		return "WebAppointmentSlotDataResponse [doctorSlugURL=" + doctorSlugURL + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", slots=" + slots + "]";
	}
}
