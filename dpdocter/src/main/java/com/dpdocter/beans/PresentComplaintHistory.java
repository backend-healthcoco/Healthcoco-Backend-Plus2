package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class PresentComplaintHistory extends GenericCollection {

	private String id;

	private String presentComplaintHistory;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private String speciality;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPresentComplaintHistory() {
		return presentComplaintHistory;
	}

	public void setPresentComplaintHistory(String presentComplaintHistory) {
		this.presentComplaintHistory = presentComplaintHistory;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	@Override
	public String toString() {
		return "PresentComplaintHistory [id=" + id + ", presentComplaintHistory=" + presentComplaintHistory
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", discarded=" + discarded + ", speciality=" + speciality + "]";
	}

}
