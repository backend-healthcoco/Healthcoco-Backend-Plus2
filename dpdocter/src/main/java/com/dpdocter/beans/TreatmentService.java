package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class TreatmentService extends GenericCollection{
    private String id;

    private String name;

    private String speciality;

    private String locationId;

    private String hospitalId;

    private String doctorId;

    private Boolean discarded = false;

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

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "ProductAndService [id=" + id + ", name=" + name + ", speciality=" + speciality + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", doctorId=" + doctorId + ", discarded=" + discarded
				+ "]";
	}
}
