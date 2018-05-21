package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class DoctorHospitalDentalImagingAssociation extends GenericCollection {

	private String id;
	private String doctorId;
	private String doctorLocationId;
	private String hospitalId;
	private Boolean discarded = true;

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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getDoctorLocationId() {
		return doctorLocationId;
	}

	public void setDoctorLocationId(String doctorLocationId) {
		this.doctorLocationId = doctorLocationId;
	}

	@Override
	public String toString() {
		return "DoctorHospitalDentalImagingAssociation [id=" + id + ", doctorId=" + doctorId + ", hospitalId="
				+ hospitalId + ", discarded=" + discarded + "]";
	}

}
