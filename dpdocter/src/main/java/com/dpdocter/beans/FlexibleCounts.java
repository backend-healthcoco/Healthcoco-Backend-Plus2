package com.dpdocter.beans;

import java.util.List;

public class FlexibleCounts {
	private String doctorId;

	private String hospitalId;

	private String locationId;

	private List<Count> counts;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public List<Count> getCounts() {
		return counts;
	}

	public void setCounts(List<Count> counts) {
		this.counts = counts;
	}

	@Override
	public String toString() {
		return "FlexibleCountsRequest [doctorId=" + doctorId + ", hospitalId=" + hospitalId + ", locationId=" + locationId + ", counts=" + counts + "]";
	}

}
