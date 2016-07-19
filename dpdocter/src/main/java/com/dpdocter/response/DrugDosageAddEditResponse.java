package com.dpdocter.response;

import com.dpdocter.beans.DosageWithTime;
import com.dpdocter.collections.GenericCollection;

public class DrugDosageAddEditResponse extends GenericCollection {

    private String id;

    private DosageWithTime dosage;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private Boolean discarded = false;
    
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public DosageWithTime getDosage() {
	return dosage;
    }

    public void setDosage(DosageWithTime dosage) {
	this.dosage = dosage;
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

	@Override
	public String toString() {
		return "DrugDosageAddEditResponse [id=" + id + ", dosage=" + dosage + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + "]";
	}

}
